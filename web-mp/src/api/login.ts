import { http } from '@/utils/request'
import { appConfig } from '@/utils/config'
import type { AjaxResult, LoginPayload, UserInfo, CaptchaInfo } from '@/types/global'

/**
 * 登录 / 注销 / 验证码 / 用户信息
 * 对接后端 `/login` `/logout` `/captchaImage` `/getInfo` `/getRouters` 等标准接口
 */

/** 1. 获取验证码（silent: 失败时不弹错） */
export function getCaptcha(): Promise<AjaxResult<CaptchaInfo>> {
  return http.get<AjaxResult<CaptchaInfo>>('/captchaImage', undefined, { silent: true })
}

/**
 * 2. 登录
 * 后端默认启用 RSA 加密登录：前端拿到公钥 → 用 jsencrypt 加密密码 → 提交 /login
 * 移动端简化处理：若不启用 RSA 直接明文；若启用，则在 login 页用 jsencrypt 处理。
 */
export function login(data: LoginPayload): Promise<AjaxResult<{ token: string }>> {
  return http.post<AjaxResult<{ token: string }>>('/login', data)
}

/** 3. 注销 */
export function logout(): Promise<AjaxResult<null>> {
  return http.post<AjaxResult<null>>('/logout')
}

/** 4. 获取当前用户信息（含角色 / 权限） */
export function getInfo(): Promise<AjaxResult<{ user: UserInfo; roles: string[]; permissions: string[] }>> {
  return http.get<AjaxResult<{ user: UserInfo; roles: string[]; permissions: string[] }>>('/getInfo')
}

/** 5. RSA 公钥（登录密码加密用） */
export function getPublicKey(): Promise<AjaxResult<{ publicKey: string }>> {
  return http.get<AjaxResult<{ publicKey: string }>>('/getPublicKey')
}

/** 6. 路由表 */
export function getRouters(): Promise<AjaxResult<unknown[]>> {
  return http.get<AjaxResult<unknown[]>>('/getRouters')
}

export { appConfig }
