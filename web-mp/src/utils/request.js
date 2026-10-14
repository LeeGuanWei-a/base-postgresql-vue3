import { appConfig } from './config'
import { getToken, clearAuth } from './auth'

/**
 * request 工具
 * - 自动加 token（Authorization: Bearer xxx）
 * - 401/5002 跳登录
 * - code != 200 走 reject（约定 200 成功）
 * - 错误 toast
 */

function buildUrl(url) {
  if (/^https?:\/\//.test(url)) return url
  if (url.startsWith('/dev-api') || url.startsWith('/prod-api') || url.startsWith('/stage-api')) {
    return url
  }
  if (url.startsWith('/')) return appConfig.baseUrl + url
  return appConfig.baseUrl + '/' + url
}

function handleAuthFail() {
  clearAuth()
  uni.showToast({ title: '登录已过期，请重新登录', icon: 'none' })
  setTimeout(() => {
    uni.reLaunch({ url: '/pages/login/index' })
  }, 800)
}

export function request(options) {
  return new Promise((resolve, reject) => {
    const token = getToken()
    uni.request({
      url: buildUrl(options.url),
      method: options.method || 'GET',
      data: options.data,
      header: {
        'Content-Type': 'application/json;charset=utf-8',
        ...(token ? { Authorization: 'Bearer ' + token } : {}),
        ...(options.header || {})
      },
      timeout: options.timeout || appConfig.timeout,
      success(res) {
        const body = res.data
        if (body && typeof body === 'object' && 'code' in body) {
          if (body.code === 200 || body.code === 20000) {
            resolve(body)
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
          resolve(res)
        }
      },
      fail(err) {
        if (!options.silent) {
          // 只在非静默模式下提示「网络异常」
          uni.showToast({ title: (err && err.errMsg) || '网络异常', icon: 'none' })
        }
        reject(err)
      }
    })
  })
}

/* —— 便捷方法 —— */
export const http = {
  get: (url, data, opts) => request({ ...(opts || {}), url, method: 'GET', data }),
  post: (url, data, opts) => request({ ...(opts || {}), url, method: 'POST', data }),
  put: (url, data, opts) => request({ ...(opts || {}), url, method: 'PUT', data }),
  del: (url, data, opts) => request({ ...(opts || {}), url, method: 'DELETE', data })
}
