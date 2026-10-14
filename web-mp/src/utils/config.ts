/**
 * 工具层配置 —— 转发自项目根 config.js
 * 业务里都从这里 import；不要直接 import 根 config.js，便于以后热替换或 mock。
 */
import rootConfig from '@/config'

export interface AppConfig {
  title: string
  baseUrl: string
  rsa: boolean
  captcha: boolean
  timeout: number
  mock: boolean
}

export const appConfig: AppConfig = {
  title: '基础平台',
  baseUrl: rootConfig.baseUrl,
  rsa: rootConfig.rsa,
  captcha: rootConfig.captcha,
  timeout: rootConfig.timeout,
  mock: rootConfig.mock
}

/** 存储 key —— 与 web-ui 的 cookie 名保持同一前缀 Basic- */
export const TOKEN_KEY = 'Basic-Token'
export const USER_INFO_KEY = 'Basic-UserInfo'
export const PERMISSIONS_KEY = 'Basic-Permissions'
export const ROLES_KEY = 'Basic-Roles'
