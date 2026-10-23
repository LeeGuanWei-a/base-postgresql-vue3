package com.basic.common.core.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 分页查询参数对象（参考 RuoYi-Vue 4.x PageQuery 简化版）。
 * <p>
 * 背景：参考的 master-extend-flowable 分支基于 RuoYi 4.x 平台，Service / Controller
 * 大量使用 {@code PageQuery pageQuery} 形参做分页（pageNum / pageSize / orderByColumn / isAsc）。
 * 当前 springboot4-extend-flowable 分支基于 RuoYi 3.9.2，分页走 {@code startPage()} + PageHelper
 * 的 ThreadLocal 模式，没有 PageQuery 类。
 * <p>
 * 为了让搬过来的 200+ 工作流代码"接口形态"保持一致、补齐这部分缺失的胶水类，
 * 本类作为最小可用版本（暂不带 {@code pageNum < 1} 等校验，业务层用 PageHelper.startPage() 兜底）。
 * 后续 PR3 如果平台整体升到 RuoYi 4.x，本类可以被官方 PageQuery 替换。
 *
 * @author basic
 */
@Data
@NoArgsConstructor
public class PageQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 当前页码，从 1 开始 */
    private Integer pageNum = 1;

    /** 每页条数 */
    private Integer pageSize = 10;

    /** 排序列名（驼峰） */
    private String orderByColumn;

    /** 排序方向 asc / desc */
    private String isAsc = "asc";

    /**
     * @description 用指定页码与每页条数构造分页查询参数，排序列留空、排序方向沿用默认升序
     * @params [pageNum, pageSize] 当前页码，从1开始；每页条数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public PageQuery(Integer pageNum, Integer pageSize) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }
}
