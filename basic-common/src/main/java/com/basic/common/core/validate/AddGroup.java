package com.basic.common.core.validate;

/**
 * 新增分组校验标识（参考 RuoYi-Vue 4.x）。
 * <p>
 * 用法：
 * <pre>
 * {@code
 * public class WfFormBo {
 *     @NotBlank(groups = AddGroup.class, message = "新增时表单编码不能为空")
 *     private String code;
 * }
 * }
 * </pre>
 *
 * @author basic
 */
public interface AddGroup {
}
