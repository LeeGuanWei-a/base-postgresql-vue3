package com.basic.workflow.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 流程数据清盘 Mapper。
 *
 * <p>只提供两个"管道型"方法，具体的表清单由 Service 侧的白名单常量决定，
 * 这里不内置任何表名，避免把危险的表名散落在 SQL 里。</p>
 *
 * <p><b>为什么先查存在性再删</b>：不同环境里 Flowable 的辅助表不一定都存在
 * （例如 flowable-ui-modeler 的 act_de_model、事件注册表的 flw_event_* 在部分
 * 部署里没有）。PostgreSQL 中一条语句失败会让整个事务进入 aborted 状态，
 * 后续语句全部报错，所以必须先过滤出真实存在的表，否则清盘会整体失败。</p>
 *
 * @author integration-supplement
 */
public interface WfProcessClearMapper {

    /**
     * 从候选表名里筛出"当前库里真实存在"的表。
     *
     * @param tableNames 候选表名（只传不含 schema 的纯表名）
     * @return 存在的表名列表
     */
    List<String> selectExistingTables(@Param("tableNames") List<String> tableNames);

    /**
     * 清空单表（DELETE FROM）。
     *
     * <p><b>为什么要加 {@code @InterceptorIgnore(blockAttack = "true")}</b>：
     * 项目在 {@code MybatisPlusConfig} 里注册了 {@code BlockAttackInnerInterceptor}，
     * 它会拦截"没有 WHERE 的 DELETE"（报 Prohibition of full table deletion）。
     * 本方法按设计就是要清空整表，且表名已经过
     * 白名单 + 正则 + 库内存在性三重校验，因此显式声明跳过该拦截。
     * 这是 MyBatis-Plus 官方提供的忽略机制，只作用于本方法。</p>
     *
     * <p>tableName 只应来自 {@link #selectExistingTables(List)} 的返回值，
     * 且必须匹配 {@code ^[a-z_][a-z0-9_]*$}。</p>
     *
     * @return 删除行数
     */
    @InterceptorIgnore(blockAttack = "true")
    int deleteAllFromTable(@Param("tableName") String tableName);

    /**
     * 统计单表行数（同样只允许传白名单内的表名）。
     *
     * @return 行数
     */
    int countRows(@Param("tableName") String tableName);
}
