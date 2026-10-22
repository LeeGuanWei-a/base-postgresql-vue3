import { http } from '@/utils/request'
import { appConfig } from '@/utils/config'

/**
 * 登录 / 注销 / 验证码 / 用户信息
 * 对接后端 `/login` `/logout` `/captchaImage` `/getInfo` `/getRouters` 等标准接口
 */

/** 1. 获取验证码（silent: 失败时不弹错）。type 可选 SLIDER/ROTATE/WORD_IMAGE_CLICK */
export function getCaptcha(type) {
  const params = type ? { type } : undefined
  return http.get('/captchaImage', params, { silent: true })
}

/** 1.5 行为验证码二次校验(可选,主流程走 /login 一次性提交) */
export function checkCaptcha(data) {
  return http.post('/captcha/check', data, { silent: true })
}

/**
 * 2. 登录
 * 后端默认启用 RSA 加密登录：前端拿到公钥 → 用 jsencrypt 加密密码 → 提交 /login
 * 移动端简化处理：若不启用 RSA 直接明文；若启用，则在 login 页用 jsencrypt 处理。
 */
export function login(data) {
  return http.post('/login', data)
}

/** 3. 注销 */
export function logout() {
  return http.post('/logout')
}

/** 4. 获取当前用户信息（含角色 / 权限） */
export function getInfo() {
  return http.get('/getInfo')
}

/** 5. RSA 公钥（登录密码加密用） */
export function getPublicKey() {
  return http.get('/getPublicKey')
}

/** 6. 路由表 */
export function getRouters() {
  return http.get('/getRouters')
}

export { appConfig }
