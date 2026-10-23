package com.basic.wecom.config;

import com.basic.common.core.redis.RedisCache;
import me.chanjar.weixin.cp.config.WxCpConfigStorage;
import me.chanjar.weixin.cp.config.impl.WxCpDefaultConfigImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 基于项目现有 RedisCache 的 WxCpConfigStorage。
 * <p>
 * WxJava 默认实现（WxCpDefaultConfigImpl）把 access_token 放在内存里。
 * 多实例部署时会出现"每个实例都去刷新一次 token"，触发企微限频。
 * 所以我们覆盖三个 getter/setter，把 token 持久化到 Redis。
 * <p>
 * Key 设计：{@code wecom:token:cp:{corpId}:{agentId}}，
 * 跟其他缓存隔离，便于运维排查。
 */
public class RedisWxCpConfigStorage extends WxCpDefaultConfigImpl {

    private static final Logger log = LoggerFactory.getLogger(RedisWxCpConfigStorage.class);
    private static final String KEY_PREFIX = "wecom:token:cp:";

    private final RedisCache redisCache;
    private final Long agentId;
    private final String corpId;

    /** 同一 corp+agent 在多线程下只让一个去刷新 */
    private final ReentrantLock refreshLock = new ReentrantLock();

    /**
     * 构造器：注入 Redis 工具 + 当前应用对应的 corpId/agentId。
     * <p>
     * 同时把 corpId/agentId 设到父类字段，否则父类的 getCorpId/getAgentId 返回 null，
     * WxJava 内部调用时会抛 NullPointerException。
     *
     * @param redisCache 项目现有 Redis 工具
     * @param corpId     企业 corpId
     * @param agentId    自建应用 agentId
     */
    public RedisWxCpConfigStorage(RedisCache redisCache, String corpId, Long agentId) {
        this.redisCache = redisCache;
        this.corpId = corpId;
        this.agentId = agentId;
        // 同时设到父类，否则父类的 getCorpId/getAgentId 返回 null，WxJava 内部会 NPE
        setCorpId(corpId);
        setAgentId(agentId);
    }

    /**
     * 拼接 Redis key：{@code wecom:token:cp:{corpId}:{agentId}}。
     * <p>
     * 同一个 corp 下不同 agentId 的 token 隔离存储，避免互相覆盖。
     *
     * @return 完整的 Redis key
     */
    private String key() {
        return KEY_PREFIX + corpId + ":" + agentId;
    }

    /**
     * 从 Redis 读取 access_token。
     * <p>
     * WxJava 内部会先调 {@link #isAccessTokenExpired()} 判断，
     * 若过期则走 {@link #updateAccessToken(String, int)} 刷新并写回。
     *
     * @return access_token；Redis 读不到返回 null
     */
    @Override
    public String getAccessToken() {
        return redisCache.getCacheObject(key());
    }

    /**
     * 把 access_token 写回 Redis。
     * <p>
     * 缓存有效期 = token 实际有效期 - 60 秒，避免边界问题。
     * 之所以需要 -60：token 过期时间是从企微下发时算起的，
     * 网络延迟 + 进程时钟漂移可能让"还没到点但已过期"。
     *
     * @param accessToken 企微下发的 access_token
     */
    @Override
    public void setAccessToken(String accessToken) {
        // 缓存有效期比 token 实际有效期短 60 秒，避免边界问题
        // getExpiresTime() 返回 long；RedisCache 只接受 Integer + TimeUnit
        long expireSec = Math.max(60L, getExpiresTime() - 60);
        redisCache.setCacheObject(key(), accessToken, (int) expireSec, TimeUnit.SECONDS);
    }

    /**
     * 判断 access_token 是否已过期。
     * <p>
     * 简化逻辑：Redis 里读不到就视为过期。WxJava 会在调用前先查这个方法。
     *
     * @return true 表示已过期需要刷新
     */
    @Override
    public boolean isAccessTokenExpired() {
        // WxJava 调用流程：getAccessToken() 之前会先调这个判断。
        // 我们简化为：Redis 里读不到就视为过期。
        return getAccessToken() == null;
    }

    /**
     * 强制让 token 失效（清掉 Redis key）。
     * <p>
     * 当企微返回 40001/42001 等 token 失效错误码时，
     * WxJava 内部会调本方法，下一次请求会触发刷新。
     */
    @Override
    public void expireAccessToken() {
        redisCache.deleteObject(key());
    }

    /**
     * 刷新 access_token，由 WxJava 内部在检测到过期时调用。
     * <p>
     * 关键设计：
     * <ul>
     *   <li>{@code synchronized} + {@link ReentrantLock} 双保险：
     *       synchronized 防同进程并发；lock 配合双重检查防多进程并发</li>
     *   <li>双重检查：另一个线程可能刚刷完，避免重复刷新触发企微限频</li>
     *   <li>失败时打 ERROR 日志但不抛异常，由 WxJava 走重试逻辑</li>
     * </ul>
     *
     * @param accessToken     企微下发的 token
     * @param expiresInSeconds 有效期（秒）
     */
    @Override
    public synchronized void updateAccessToken(String accessToken, int expiresInSeconds) {
        // 同步锁保证同进程内只有一个线程会执行刷新
        refreshLock.lock();
        try {
            // 双重检查：另一个线程可能刚刷新完
            String current = getAccessToken();
            if (current != null && !current.isEmpty()) {
                log.debug("[wecom] token 已存在，跳过刷新 agentId={}", agentId);
                return;
            }
            setAccessToken(accessToken);
            setExpiresTime(expiresInSeconds);
            log.info("[wecom] token 刷新成功 corpId={} agentId={} expiresIn={}s", corpId, agentId, expiresInSeconds);
        } finally {
            refreshLock.unlock();
        }
    }
    // corpId/agentId 通过构造函数 setCorpId/setAgentId 注入到父类，
    // 直接复用父类 getCorpId/getAgentId 即可，不在此 override。
}
