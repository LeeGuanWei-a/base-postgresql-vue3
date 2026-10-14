import { appConfig } from './config'
import { getToken, clearAuth } from './auth'
import type { AjaxResult } from '@/types/global'

/**
 * request 工具
 * - 自动加 token（Authorization: Bearer xxx）
 * - 401/5002 跳登录
 * - code != 200 走 reject（约定 200 成功）
 * - 错误 toast
 */

export interface RequestOptions {
  url: string
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH'
  data?: unknown
  header?: Record<string, string>
  timeout?: number
  /** 静默模式：失败时不弹 toast */
  silent?: boolean
}

function buildUrl(url: string): string {
  if (/^https?:\/\//.test(url)) return url
  if (url.startsWith('/dev-api') || url.startsWith('/prod-api') || url.startsWith('/stage-api')) {
    return url
  }
  if (url.startsWith('/')) return appConfig.baseUrl + url
  return appConfig.baseUrl + '/' + url
}

function handleAuthFail(): void {
  clearAuth()
  uni.showToast({ title: '登录已过期，请重新登录', icon: 'none' })
  setTimeout(() => {
    uni.reLaunch({ url: '/pages/login/index' })
  }, 800)
}

export function request<T = unknown>(options: RequestOptions): Promise<T> {
  return new Promise((resolve, reject) => {
    const token = getToken()
    uni.request({
      url: buildUrl(options.url),
      method: options.method || 'GET',
      data: options.data as Record<string, unknown> | undefined,
      header: {
        'Content-Type': 'application/json;charset=utf-8',
        ...(token ? { Authorization: 'Bearer ' + token } : {}),
        ...(options.header || {})
      },
      timeout: options.timeout || appConfig.timeout,
      success(res: UniApp.RequestSuccessCallbackResult) {
        const body = res.data as AjaxResult | undefined
        if (body && typeof body === 'object' && 'code' in body) {
          if (body.code === 200 || body.code === 20000) {
            resolve(body as unknown as T)
            return
          }
          // 401 等只在「有 token 时」才视为过期跳登录；否则交给上层弹业务错误
          if ((body.code === 401 || body.code === 5002 || body.code === 50014) && token) {
            handleAuthFail()
            reject(body)
            return
          }
          if (!options.silent) {
            uni.showToast({ title: body.msg || '请求失败', icon: 'none' })
          }
          reject(body)
        } else {
          // 非标准包结构，原样返回
          resolve(res as unknown as T)
        }
      },
      fail(err: UniApp.GeneralCallbackResult) {
        if (!options.silent) {
          uni.showToast({ title: (err && err.errMsg) || '网络异常', icon: 'none' })
        }
        reject(err)
      }
    })
  })
}

/* —— 便捷方法 —— */
export const http = {
  get: <T = unknown>(url: string, data?: unknown, opts?: RequestOptions): Promise<T> =>
    request<T>({ ...(opts || {}), url, method: 'GET', data }),
  post: <T = unknown>(url: string, data?: unknown, opts?: RequestOptions): Promise<T> =>
    request<T>({ ...(opts || {}), url, method: 'POST', data }),
  put: <T = unknown>(url: string, data?: unknown, opts?: RequestOptions): Promise<T> =>
    request<T>({ ...(opts || {}), url, method: 'PUT', data }),
  del: <T = unknown>(url: string, data?: unknown, opts?: RequestOptions): Promise<T> =>
    request<T>({ ...(opts || {}), url, method: 'DELETE', data })
}
