package com.basic.common.core.page;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 表格分页数据对象（增强版，支持 RuoYi-Vue 4.x 的泛型 + 静态工厂用法）。
 *
 * <p>用法：
 * <pre>
 *   TableDataInfo&lt;XxxVo&gt; info = TableDataInfo.build();     // 空分页（总数=0，行=空）
 *   TableDataInfo&lt;XxxVo&gt; info = TableDataInfo.build(page); // 把 MyBatis-Plus Page 转成 TableDataInfo
 *   TableDataInfo&lt;XxxVo&gt; info = TableDataInfo.subList(list, pageNum, pageSize); // 内存分页
 * </pre>
 *
 * @author basic
 */
@Data
@NoArgsConstructor
public class TableDataInfo<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 总记录数 */
    private long total;

    /** 列表数据 */
    private List<T> rows;

    /** 消息状态码 */
    private int code = 200;

    /** 消息内容 */
    private String msg;

    /**
     * @description 用当前页数据与总记录数构造分页结果，状态码固定为200
     * @params [list, total] 当前页数据列表；总记录数
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public TableDataInfo(List<T> list, int total) {
        this.rows = list;
        this.total = total;
        this.code = 200;
    }

    /**
     * @return TableDataInfo<T> 总数为0、行数据为空列表的空分页结果
     * @description 构造空分页结果，行数据置为空列表、总记录数置0、状态码置200
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static <T> TableDataInfo<T> build() {
        TableDataInfo<T> info = new TableDataInfo<>();
        info.setRows(Collections.emptyList());
        info.setTotal(0L);
        info.setCode(200);
        return info;
    }

    /**
     * @return TableDataInfo<T> 转换后的分页结果，page 为空时返回空分页
     * @description 把 MyBatis-Plus 的 Page 对象转成本项目的分页结果，取其中的当前页数据与总记录数
     * @params [page] MyBatis-Plus 分页对象，为空时按空分页处理
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static <T> TableDataInfo<T> build(Page<T> page) {
        TableDataInfo<T> info = new TableDataInfo<>();
        if (page == null) {
            info.setRows(Collections.emptyList());
            info.setTotal(0L);
        } else {
            info.setRows(page.getRecords());
            info.setTotal(page.getTotal());
        }
        info.setCode(200);
        return info;
    }

    /**
     * @return TableDataInfo<T> 截取当前页数据后的分页结果，入参为空时返回空分页
     * @description 对内存中的列表做分页截取，不依赖 MyBatis-Plus 或 PageHelper，页码与页大小非法时按默认值修正
     * @params [list, pageNum, pageSize] 待分页的完整列表；页码，小于1时按1处理；每页条数，小于1时按10处理
     * @author liguanwei
     * @date 2026/10/17 20:52
     * @remark (改代码时写明修改时间修改人修改原因)
     */
    public static <T> TableDataInfo<T> subList(List<T> list, int pageNum, int pageSize) {
        TableDataInfo<T> info = new TableDataInfo<>();
        info.setCode(200);
        if (list == null || list.isEmpty()) {
            info.setRows(Collections.emptyList());
            info.setTotal(0L);
            return info;
        }
        int total = list.size();
        info.setTotal(total);
        if (pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize < 1) {
            pageSize = 10;
        }
        int from = Math.min((pageNum - 1) * pageSize, total);
        int to = Math.min(from + pageSize, total);
        info.setRows(list.subList(from, to));
        return info;
    }
}
