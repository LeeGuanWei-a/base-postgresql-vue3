import request from '@/utils/request'
import { encrypt } from '@/utils/jsencrypt'

// 登录方法
export function login(username, password, code, uuid, captchaData) {
  const encrypted = encrypt(password)
  console.log('[login] password plaintext=', password, 'encrypted=', encrypted)
  const data = {
    username,
    // 后端 SysLoginService 用 RsaUtils.decryptByPrivateKey 解密 password，必须用前端 jsencrypt 公钥加密
    password: encrypted,
    code,
    uuid,
    // tianai-captcha 行为验证码滑动轨迹;4 种类型都用该字段
    captchaData: captchaData || ''
  }
  return request({
    url: '/login',
    headers: {
      isToken: false,
      repeatSubmit: false
    },
    method: 'post',
    data: data
  })
}

// 注册方法
export function register(data) {
  return request({
    url: '/register',
    headers: {
      isToken: false
    },
    method: 'post',
    data: data
  })
}

// 获取用户详细信息
export function getInfo() {
  return request({
    url: '/getInfo',
    method: 'get'
  })
}

// 解锁屏幕
export function unlockScreen(password) {
  return request({
    url: '/unlockscreen',
    method: 'post',
    data: { password }
  })
}

// 退出方法
export function logout() {
  return request({
    url: '/logout',
    method: 'post'
  })
}

// 获取验证码(tianai-captcha 行为验证码)
export function getCodeImg(type) {
  return request({
    url: '/captchaImage',
    params: type ? { type } : undefined,
    headers: {
      isToken: false
    },
    method: 'get',
    timeout: 20000
  })
}

// 行为验证码二次校验(可选,主流程用 /login 一次性提交)
export function checkCaptcha(data) {
  return request({
    url: '/captcha/check',
    headers: {
      isToken: false
    },
    method: 'post',
    data
  })
}