/**
 * 全局 TypeScript 类型声明
 *
 * 这个文件定义 TypeScript 找不到的类型（uniapp 全局变量、Vue SFC shim 等）
 */

// ====== Vue SFC shim ======
declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const component: DefineComponent<{}, {}, any>
  export default component
}

// ====== 常用后端响应类型 ======
export interface AjaxResult<T = unknown> {
  msg: string
  code: number
  /** 主数据 */
  data?: T
  /** 是否开启验证码（若依非标准字段） */
  captchaOnOff?: boolean
  /** 是否开启验证码（若依标准字段） */
  captchaEnabled?: boolean
  /** 验证码 base64（不带 data:image 前缀） */
  img?: string
  uuid?: string
  token?: string
}

export interface CaptchaInfo {
  captchaEnabled: boolean
  img: string
  uuid: string
}

export interface LoginPayload {
  username: string
  password: string
  code?: string
  uuid?: string
}

export interface UserInfo {
  userId?: number
  userName?: string
  nickName?: string
  avatar?: string
  email?: string
  phonenumber?: string
  // 任何额外字段
  [key: string]: unknown
}

export interface PageQuery {
  pageNum?: number
  pageSize?: number
  [key: string]: unknown
}

export interface PageResult<T> {
  total: number
  rows: T[]
  code: number
  msg: string
}

// ====== 全局类型补强 ======
declare global {
  interface Window {
    __DSH_BOOT__?: Record<string, unknown>
    __UPLOAD_DEBUG?: {
      registered: string[]
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      app: any
    }
    __LOGIN_DEBUG?: {
      captchaEnabled: { value: boolean }
      captchaImg: { value: string }
      captchaUuid: { value: string }
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      form: any
    }
  }
}

export {}
