package com.basic.common.core.page;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.io.Serializable;
import java.util.List;

/**
 * 表格分页数据对象（泛型版）
 *
 * @author basic
 * @description 从 process 同步升级，原版无泛型且缺少 build 静态方法，
 *              导致 extend-flowable 的 Service 接口无法声明 TableDataInfo<WfXxxVo>
 * @date 2026-10
 */
public class TableDataInfo<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final int HTTP_OK = 200;

    /** 总记录数 */
    private long total;

    /** 列表数据 */
    private List<?> rows;

    /** 消息状态码 */
    private int code;

    /** 消息内容 */
    private String msg;

    public TableDataInfo() {
    }

    public TableDataInfo(List<?> list, int total) {
        this.rows = list;
        this.total = total;
    }

    /** 从 MyBatis-Plus 的 IPage 构建分页结果 */
    public static <T> TableDataInfo<T> build(IPage<T> page) {
        TableDataInfo<T> rspData = new TableDataInfo<>();
        rspData.setCode(HTTP_OK);
        rspData.setMsg("查询成功");
        rspData.setRows(page.getRecords());
        rspData.setTotal(page.getTotal());
        return rspData;
    }

    /** 从普通 List 构建分页结果（无 IPage 时用） */
    public static <T> TableDataInfo<T> build(List<T> list) {
        TableDataInfo<T> rspData = new TableDataInfo<>();
        rspData.setCode(HTTP_OK);
        rspData.setMsg("查询成功");
        rspData.setRows(list);
        rspData.setTotal(list.size());
        return rspData;
    }

    /** 空分页结果 */
    public static <T> TableDataInfo<T> build() {
        TableDataInfo<T> rspData = new TableDataInfo<>();
        rspData.setCode(HTTP_OK);
        rspData.setMsg("查询成功");
        return rspData;
    }

    /**
     * 内存分页（对 list 按 pageNum/pageSize 截取）
     * 用于 service 中没有 IPage 时的手工分页场景
     */
    public static <T> TableDataInfo<T> subList(List<T> list, int pageNum, int pageSize) {
        TableDataInfo<T> rspData = new TableDataInfo<>();
        rspData.setCode(HTTP_OK);
        rspData.setMsg("查询成功");
        if (list == null) {
            rspData.setTotal(0);
            rspData.setRows(new java.util.ArrayList<>());
            return rspData;
        }
        rspData.setTotal(list.size());
        int from = Math.max(0, (pageNum - 1) * pageSize);
        int to = Math.min(list.size(), from + pageSize);
        if (from >= list.size()) {
            rspData.setRows(new java.util.ArrayList<>());
        } else {
            rspData.setRows(new java.util.ArrayList<>(list.subList(from, to)));
        }
        return rspData;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<?> getRows() {
        return rows;
    }

    public void setRows(List<?> rows) {
        this.rows = rows;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
