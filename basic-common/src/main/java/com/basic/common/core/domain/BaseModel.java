package com.basic.common.core.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 基础实体类（参考 RuoYi-Vue 4.x BaseEntity 简化版）。
 * <p>
 * 提供 createTime / updateTime / creator / updater 等通用字段，
 * 供工作流等业务实体（如 ExtendProcinst）继承。
 *
 * @author basic
 */
@Data
public class BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 创建者 */
    private String creator;

    /** 创建时间 */
    private Date createTime;

    /** 更新者 */
    private String updater;

    /** 更新时间 */
    private Date updateTime;

    /** 备注 */
    private String remark;
}
