package com.basic.enm;

import java.io.Serializable;

/**
 * @description 流程状态枚举，定义草稿、审批中、驳回、撤回、加签、暂存、转办、办结、终止等状态及其中文说明
 * @author yanbin
 * @date 2023/10/25
 */
public enum ProcessStatusEnum implements Serializable {
  CG("草稿"),
  SPZ("审批中"),
  BH("驳回"),
  CH("撤回"),
  JQ("加签"),
  ZC("暂存"),
  ZB("转办"),
  BJ("办结"),
  ZZ("终止");

  private String msg;
  private String type;

  /**
   * 绑定流程状态对应的中文说明。
   *
   * @param msg 中文状态说明
   */
  ProcessStatusEnum(String msg) {
    this.msg = msg;
  }

  /**
   * 通过type获取Msg
   *
   * @param type
   * @return @Description:
   */
  public static String getEnumMsgByType(String type) {
    for (ProcessStatusEnum e : ProcessStatusEnum.values()) {
      if (e.toString().equals(type)) {
        return e.msg;
      }
    }
    return "";
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
