/**
 * 密码复杂度规则（与后端 com.basic.common.utils.PasswordPolicy 保持一致）
 *
 * 规则：
 *   1. 长度 8 ~ 15 位
 *   2. 必须同时包含大写字母、小写字母、数字、特殊字符
 *   3. 仅允许可打印 ASCII 字符（不含空格、中文等）
 *
 * 后端在“新增用户 / 重置密码 / 修改密码 / 注册”四个入口做同样的校验，
 * 前端校验仅用于提升体验，不能替代后端校验。
 */

// 密码长度限制
export const PASSWORD_MIN_LENGTH = 8
export const PASSWORD_MAX_LENGTH = 15

// 密码为空提示
export const PASSWORD_EMPTY_MESSAGE = '密码不能为空'

// 密码复杂度提示
export const PASSWORD_MESSAGE = '密码长度必须介于 8 和 15 之间，且必须同时包含大写字母、小写字母、数字和特殊字符'

// 与后端 PasswordPolicy.PATTERN 完全一致
const PASSWORD_PATTERN = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9])[!-~]{8,15}$/

/**
 * 密码是否满足复杂度要求
 * @param {string} value 明文密码
 * @returns {boolean}
 */
export function isValidPassword(value) {
  return typeof value === 'string' && PASSWORD_PATTERN.test(value)
}

/**
 * 生成 el-form 的密码校验规则
 * @param {string} emptyMessage 为空时的提示
 * @returns {Array}
 */
function buildRules(emptyMessage) {
  return [
    { required: true, message: emptyMessage, trigger: 'blur' },
    {
      trigger: 'blur',
      validator: (rule, value, callback) => {
        // 为空由 required 规则提示，这里不重复报错
        if (!value) {
          return callback()
        }
        if (isValidPassword(value)) {
          return callback()
        }
        return callback(new Error(PASSWORD_MESSAGE))
      }
    }
  ]
}

export function usePasswordRule() {
  // 新增用户时的密码校验
  const pwdValidator = buildRules('密码不能为空')
  // 个人中心修改密码时的校验
  const infoPwdValidator = buildRules('新密码不能为空')
  // 注册页面密码校验
  const registerPwdValidator = buildRules('请输入您的密码')

  /**
   * ElMessageBox.prompt 的 inputValidator：校验通过返回 true，否则返回错误提示
   * @param {string} value 明文密码
   * @returns {true|string}
   */
  const pwdPromptValidator = (value) => {
    if (!value) {
      return PASSWORD_EMPTY_MESSAGE
    }
    if (!isValidPassword(value)) {
      return PASSWORD_MESSAGE
    }
    return true
  }

  return {
    pwdValidator,
    infoPwdValidator,
    registerPwdValidator,
    pwdPromptValidator
  }
}
