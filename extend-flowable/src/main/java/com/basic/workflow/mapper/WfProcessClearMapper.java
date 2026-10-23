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
     * @return List<String> 当前库中真实存在的表名，全部不存在时为空列表
     * @description 从候选表名里筛出「当前库里真实存在」的表，用于清库前确认要清理哪些表
     * @params [tableNames] 候选表名，只传不含 schema 的纯表名
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    List<String> selectExistingTables(@Param("tableNames") List<String> tableNames);

    /**
     * @return int 实际删除的行数
     * @description 清空单表（无 WHERE 条件的 DELETE FROM），tableName 只应来自 {@link #selectExistingTables(List)} 的返回值，
     * 且必须匹配 ^[a-z_][a-z0-9_]*$
     * 这里必须加 @InterceptorIgnore(blockAttack = "true")：项目在 MybatisPlusConfig 里注册了 BlockAttackInnerInterceptor，
     * 它会拦截「没有 WHERE 的 DELETE」（报 Prohibition of full table deletion），本方法按设计就是要清空整表，
     * 且表名已经过白名单 + 正则 + 库内存在性三重校验，因此显式声明跳过该拦截，
     * 这是 MyBatis-Plus 官方提供的忽略机制，只作用于本方法
     * @params [tableName] 要清空的表名，只传不含 schema 的纯表名
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    @InterceptorIgnore(blockAttack = "true")
    int deleteAllFromTable(@Param("tableName") String tableName);

    /**
     * @return int 该表的当前行数，空表为 0
     * @description 统计单表行数用于清库前后核对，同样只允许传白名单内的表名
     * @params [tableName] 要统计的表名，只传不含 schema 的纯表名
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    int countRows(@Param("tableName") String tableName);
}
