package com.basic.common.core.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * RuoYi-Vue 4.x 风格的 BaseMapper 扩展接口。
 * <p>提供 selectVoById / selectVoList / insertBatch / selectListByMap 等 RuoYi 4.x Service
 * 业务层常用方法的默认实现，底层走 MyBatis-Plus {@link BaseMapper}。
 * <p>Reference 字段（Domain → Vo 的映射）以 BeanUtils.copyProperties 实现简化版，
 * 生产环境若要严格控制字段映射可在子类覆盖。
 *
 * @param <T> MyBatis-Plus Mapper 类自身（保留 RuoYi 4.x 范式以兼容业务层 ServiceImpl 用法）
 * @param <D> Domain 实体
 * @param <V> Vo 视图对象
 * @author basic
 */
public interface BaseMapperPlus<T, D, V> extends BaseMapper<D> {

    /**
     * @return V 转换后的视图对象，主键不存在时返回 null
     * @description 按主键查询 Domain 实体并转换成 Vo 视图对象
     * @params [id] 实体主键
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    default V selectVoById(Serializable id) {
        D d = selectById(id);
        if (d == null) {
            return null;
        }
        return toVo(d);
    }

    /**
     * @return List<V> 转换后的 Vo 列表
     * @description 按条件查询 Domain 列表并逐条转换成 Vo 列表
     * @params [queryWrapper] MyBatis-Plus 条件构造器
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    default List<V> selectVoList(@Param(Constants.WRAPPER) Wrapper<D> queryWrapper) {
        List<D> list = selectList(queryWrapper);
        return toVos(list);
    }

    /**
     * @return boolean 是否插入成功，实体集合为空时返回 false
     * @description 遍历实体集合逐条调用 insert 完成批量插入
     * @params [entityList] 待插入的实体集合，为空时直接返回 false
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    default boolean insertBatch(Collection<D> entityList) {
        if (entityList == null || entityList.isEmpty()) {
            return false;
        }
        for (D d : entityList) {
            insert(d);
        }
        return true;
    }

    /**
     * @return List<D> Domain 实体列表，当前实现返回全量数据
     * @description 按 Map 条件查询列表，当前实现未使用条件直接查询全部
     * @params [params] Map 形式的查询条件，当前实现未使用
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    default List<D> selectListByMap(Map<String, Object> params) {
        return selectList(null);
    }

    /**
     * @return V 转换后的 Vo 视图对象，入参为空或转换异常时返回 null
     * @description 用反射在 mapper 继承链上查找 toVo 方法完成转换，找不到时用 BeanUtils 复制 Domain 属性兜底
     * 优先用 mapper 自己声明的 toVo(D)（存在则按业务规则映射），否则用 BeanUtils.copyProperties 兜底。
     * 这里不能用 this.getClass().getMethod("toVo", ...) 反射：MyBatis 的 mapper 是动态代理类，
     * getClass 拿到的不是用户写的 mapper，拿不到自定义的 toVo 方法，所以在继承链上逐个 Class.getMethod 扫描。
     * @params [d] 待转换的 Domain 实体，为空时返回 null
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @SuppressWarnings("unchecked")
    default V toVo(D d) {
        if (d == null) {
            return null;
        }
        // 在 mapper 接口的继承链里找 toVo(D) 方法（用户可能写了 default 方法）
        java.lang.reflect.Method toVoMethod = null;
        for (Class<?> c = this.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
            try {
                toVoMethod = c.getMethod("toVo", d.getClass());
                break;
            } catch (NoSuchMethodException ignore) {
                // try parent
            }
        }
        try {
            V v;
            if (toVoMethod != null) {
                v = (V) toVoMethod.invoke(this, d);
            } else {
                // 兜底：用 Spring BeanUtils 复制（要求 V 有无参构造）
                v = (V) org.springframework.beans.BeanUtils.instantiateClass(resolveVoClass(d));
                org.springframework.beans.BeanUtils.copyProperties(d, v);
            }
            return v;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @return Class<?> 解析出的 Vo 类型，全部规则都未命中时返回 Domain 自身类型
     * @description 按类名加 Vo 后缀、domain 包下 domain.vo 包两种命名规则解析出 Vo 类型
     * @params [d] Domain 实体，用于取其运行时类型名
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    default Class<?> resolveVoClass(D d) {
        // 简单规则：把 Domain 类名 + "Vo" 替换。失败就让 instantiateClass 抛错被 catch。
        String domainName = d.getClass().getName();
        // 1) 尝试 domainName 末尾 + "Vo"
        String guess = domainName + "Vo";
        try {
            return Class.forName(guess);
        } catch (ClassNotFoundException ignore) { }
        // 2) 尝试 .domain. -> .domain.vo. 路径
        int idx = domainName.lastIndexOf(".domain.");
        if (idx > 0) {
            String pkg = domainName.substring(0, idx);
            String simple = domainName.substring(idx + ".domain.".length());
            String voGuess = pkg + ".domain.vo." + simple + "Vo";
            try {
                return Class.forName(voGuess);
            } catch (ClassNotFoundException ignore) { }
        }
        // 3) 实在不行返回 Domain 自己，BeanUtils 复制后强转 V 通常是兼容的
        return d.getClass();
    }

    /**
     * @return List<V> 转换后的 Vo 列表，入参为空时返回 null
     * @description 遍历 Domain 列表逐条转换成 Vo，并过滤掉转换失败的项
     * @params [list] 待转换的 Domain 列表
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    default List<V> toVos(List<D> list) {
        if (list == null) {
            return null;
        }
        java.util.ArrayList<V> result = new java.util.ArrayList<>(list.size());
        for (D d : list) {
            V v = toVo(d);
            if (v != null) {
                result.add(v);
            }
        }
        return result;
    }
}
