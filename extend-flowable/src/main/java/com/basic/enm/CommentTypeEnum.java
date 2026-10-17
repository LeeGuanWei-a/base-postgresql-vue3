package com.basic.enm;

/**
 * @description 审批意见的类型枚举，定义审批、驳回、撤回、加签等意见类型的编码与中文名称
 * @author yanbin
 * @date 2023/10/24
 */
public enum CommentTypeEnum {
  SP("审批"),
  BH("驳回"),
  CH("撤回"),
  ZC("暂存"),
  QS("签收"),
  WP("委派"),
  ZH("知会"),
  ZY("转阅"),
  YY("已阅"),
  ZB("转办"),
  QJQ("前加签"),
  HJQ("后加签"),
  XTZX("系统执行"),
  TJ("提交"),
  CXTJ("重新提交"),
  SPJS("审批结束"),
  LCZZ("流程终止"),
  SQ("授权"),
  CFTG("重复跳过"),
  XT("协同"),
  PS("评审");
  private String name; // 名称

  /**
   * @return String 审批意见类型对应的中文名称，未匹配到时返回空串
   * @description 遍历审批意见类型枚举，按类型编码匹配出对应的中文名称
   * @params [type] 审批意见类型编码，与枚举常量名一致
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  public static String getEnumMsgByType(String type) {
    for (CommentTypeEnum e : CommentTypeEnum.values()) {
      if (e.toString().equals(type)) {
        return e.name;
      }
    }
    return "";
  }

  /**
   * @description 用中文名称初始化审批意见类型枚举常量
   * @params [name] 该类型对应的中文名称
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  private CommentTypeEnum(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
