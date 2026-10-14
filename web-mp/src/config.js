/**
 * 应用全局配置
 * 改这个文件即可：换后端地址 / 切 mock / 关 RSA / 关验证码 / 改超时
 *
 * 用法：
 *   import config from '@/config'
 *   console.log(config.baseUrl)
 */

// ============ 后端地址 ============
// 所有平台都直接用真实 URL，H5 不走 vite proxy
// - 本地开发：改成你的电脑局域网 IP，例如 http://192.168.1.100:8080
// - 手机/真机调试：电脑和手机必须同一局域网
// - 小程序：上线要去「微信公众平台」加 request 合法域名
// - dev 时可在「微信开发者工具详情 → 不校验合法域名」勾选
const baseUrl = 'http://localhost:8080'

// ============ 登录 ============
// 后端是否启用 RSA 公钥加密（默认 true）。false = 明文提交 /login
const rsa = true

// 是否需要图形验证码
const captcha = true

// ============ 网络 ============
const timeout = 30000

// ============ Mock ============
const mock = false

export default {
  baseUrl,
  rsa,
  captcha,
  timeout,
  mock
}
