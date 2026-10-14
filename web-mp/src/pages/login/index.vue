<template>
  <view class="login-page">
    <view class="login-header">
      <image class="logo" src="/static/logo.png" mode="aspectFit" />
      <text class="title">基础平台</text>
      <text class="subtitle">基础平台移动端</text>
    </view>

    <up-form :model="form" :rules="rules" ref="formRef" label-width="80" label-align="left">
      <up-form-item label="账号" prop="username" :border-bottom="true">
        <up-input v-model="form.username" placeholder="请输入账号" :clearable="true" />
      </up-form-item>
      <up-form-item label="密码" prop="password" :border-bottom="true">
        <up-input v-model="form.password" placeholder="请输入密码" type="password" :clearable="true" />
      </up-form-item>
      <up-form-item v-if="captchaEnabled" label="验证码" prop="code" :border-bottom="true">
        <view class="captcha-row">
          <up-input v-model="form.code" placeholder="请输入验证码" :clearable="true" class="captcha-input" />
          <image v-if="captchaImg" :src="captchaImg" class="captcha-img" mode="aspectFit" @click="refreshCaptcha" />
        </view>
      </up-form-item>
    </up-form>

    <view class="login-btn">
      <up-button type="primary" :loading="loading" @click="handleLogin">登录</up-button>
    </view>

    <view class="login-tip">
      <text>默认账号：{{ defaultAccount.username }} / {{ defaultAccount.password }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { getCaptcha } from '@/api/login'
import { useUserStore } from '@/store/modules/user'
import config from '@/config'
import { encrypt as rsaEncrypt } from '@/utils/jsencrypt'

interface Account { username: string; password: string }
interface LoginForm { username: string; password: string; code: string; uuid: string }

const userStore = useUserStore()

const formRef = ref()
const loading = ref(false)
const form = reactive<LoginForm>({ username: 'admin', password: 'admin123', code: '', uuid: '' })
// 用 ref + 整体替换：避开 mp wxml 对 reactive 嵌套属性的追踪问题
const captchaEnabled = ref(false)
const captchaImg = ref('')
const captchaUuid = ref('')

const defaultAccount: Account = { username: 'admin', password: 'admin123' }

const rules = {
  username: [{ required: true, message: '请输入账号', trigger: ['blur', 'change'] }],
  password: [{ required: true, message: '请输入密码', trigger: ['blur', 'change'] }],
  code: [{ required: captchaEnabled.value, message: '请输入验证码', trigger: ['blur', 'change'] }]
}

async function refreshCaptcha(): Promise<void> {
  try {
    console.log('[login] refreshCaptcha() start')
    const res = await getCaptcha()
    console.log('[login] getCaptcha response:', res)
    captchaEnabled.value = !!(res.captchaOnOff ?? res.captchaEnabled)
    console.log('[login] captchaEnabled =', captchaEnabled.value)
    const raw = res.img || ''
    captchaImg.value = raw && raw.startsWith('data:') ? raw : 'data:image/jpeg;base64,' + raw
    captchaUuid.value = res.uuid || ''
    form.uuid = captchaUuid.value
  } catch (e) {
    console.error('[login] refreshCaptcha error:', e)
  }
}

function encodePwd(pwd: string): string {
  // config.rsa = true 时用公钥 RSA 加密（与 web-ui 同套密钥）
  if (!config.rsa) return pwd
  try {
    const cipher = rsaEncrypt(pwd)
    if (!cipher) {
      console.error('[login] RSA 加密返回空')
      return pwd
    }
    return cipher
  } catch (e) {
    console.error('[login] RSA 加密失败', e)
    return pwd
  }
}

async function handleLogin(): Promise<void> {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await userStore.login({
      username: form.username,
      password: encodePwd(form.password),
      code: form.code,
      uuid: form.uuid
    })
    await userStore.fetchUserInfo()
    uni.showToast({ title: '登录成功', icon: 'success' })
    setTimeout(() => uni.reLaunch({ url: '/pages/index/index' }), 600)
  } catch (e) {
    console.error('[login] failed', e)
  } finally {
    loading.value = false
  }
}

refreshCaptcha()

// 调试：把 reactive 状态暴露到 window，浏览器 console 可看
if (typeof window !== 'undefined') {
  setTimeout(() => {
    window.__LOGIN_DEBUG = { captchaEnabled, captchaImg, captchaUuid, form }
    console.log('[login] captcha after 1.5s:', {
      captchaEnabled: captchaEnabled.value,
      hasImg: !!captchaImg.value,
      imgLen: captchaImg.value?.length,
      imgPrefix: captchaImg.value?.slice(0, 30)
    })
  }, 1500)
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(180deg, $uni-color-primary 0%, #ffffff 30%);
  padding: 120rpx 60rpx 0;
  box-sizing: border-box;
}
.login-header {
  text-align: center;
  margin-bottom: 80rpx;
  .logo {
    width: 120rpx;
    height: 120rpx;
  }
  .title {
    display: block;
    margin-top: 24rpx;
    font-size: 40rpx;
    font-weight: 600;
    color: #ffffff;
  }
  .subtitle {
    display: block;
    margin-top: 8rpx;
    color: rgba(255, 255, 255, 0.8);
    font-size: 24rpx;
  }
}
.captcha-row {
  display: flex;
  flex-direction: row;
  align-items: center;
  width: 100%;
}
.captcha-input {
  flex: 1;
  min-width: 0;
}
.captcha-img {
  width: 200rpx;
  height: 60rpx;
  margin-left: 20rpx;
  flex-shrink: 0;
  border: 1rpx solid #ebeef5;
  border-radius: 6rpx;
}
.login-btn {
  margin-top: 60rpx;
}
.login-tip {
  margin-top: 40rpx;
  text-align: center;
  color: $uni-text-color-grey;
  font-size: 24rpx;
}
</style>
