package com.basic.flowable.tool;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
/**
 * @description 通用返回结果封装对象，携带响应码、响应消息与数据，配合 ReturnCode 判断是否成功（已停用）
 * @author yanbin
 * @author bruce.liu
 * @date 2023/10/24
 */
@Data
@Builder
public class ReturnVo<T> implements Serializable {
  private static final long serialVersionUID = -5580228202640516960L;
  // 响应编码
  @Schema(description = "响应编码 100成功 101失败", example = "100")
  private String code;
  // 响应消息
  @Schema(description = "响应消息", example = "操作成功")
  private String msg;
  // 返回的vo
  @Schema(description = "返回数据对象")
  private T data;

  /**
   * @return boolean 响应码非空且等于成功码时返回 true，否则返回 false
   * @description 判断响应码是否为成功码
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  public boolean isSuccess() {
    if (StringUtils.isNotBlank(code) && ReturnCode.SUCCESS.equals(code)) {
      return true;
    }
    return false;
  }

  /**
   * @description 构造空的返回对象，响应码、响应消息与数据均为 null
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  public ReturnVo() {
    super();
  }

  /**
   * @description 用响应码与响应消息构造返回对象，不携带数据
   * @params [code, msg] code 响应码；msg 响应消息
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  public ReturnVo(String code, String msg) {
    super();
    this.code = code;
    this.msg = msg;
  }

  /**
   * @description 用响应码、响应消息与数据对象构造返回对象
   * @params [code, msg, data] code 响应码；msg 响应消息；data 返回数据对象
   * @author liguanwei
   * @date 2026/10/17 20:52
   * @remark (改代码时写明修改时间修改人修改原因)
   */
  public ReturnVo(String code, String msg, T data) {
    super();
    this.code = code;
    this.msg = msg;
    this.data = data;
  }
}
