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
          <tianai-captcha
            ref="captchaRef"
            :show-type-switch="true"
            default-type="SLIDER"
            @success="onCaptchaSuccess"
          />
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

<script setup>
import { ref, reactive } from 'vue'
import { getCaptcha, login } from '@/api/login'
import { useUserStore } from '@/store/modules/user'
import config from '@/config'
import { encrypt as rsaEncrypt } from '@/utils/jsencrypt'
import tianaiCaptcha from '@/components/tianai-captcha/tianai-captcha.vue'

const userStore = useUserStore()

const formRef = ref(null)
const captchaRef = ref(null)
const loading = ref(false)
const form = reactive({ username: 'admin', password: 'admin123', code: '1', uuid: '', captchaData: '' })
const captchaEnabled = ref(false)

const defaultAccount = { username: 'admin', password: 'admin123' }

const rules = {
  username: [{ required: true, message: '请输入账号', trigger: ['blur', 'change'] }],
  password: [{ required: true, message: '请输入密码', trigger: ['blur', 'change'] }],
  code: [{ required: captchaEnabled.value, message: '请完成行为验证码', trigger: ['blur', 'change'] }]
}

// 行为验证码成功:把 id / data 写入 form,后续登录一次性提交
function onCaptchaSuccess(payload) {
  form.uuid = payload.id
  form.captchaData = JSON.stringify(payload.data)
  form.code = '1'
  uni.showToast({ title: '验证通过', icon: 'success', duration: 600 })
}

function encodePwd(pwd) {
  if (!config.rsa) return pwd
  try {
    const cipher = rsaEncrypt(pwd)
    if (!cipher) return pwd
    return cipher
  } catch (e) {
    return pwd
  }
}

async function handleLogin() {
  if (captchaEnabled.value && (!form.captchaData || !form.uuid)) {
    uni.showToast({ title: '请先完成行为验证码', icon: 'none' })
    return
  }
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await userStore.login({
      username: form.username,
      password: encodePwd(form.password),
      code: form.code,
      uuid: form.uuid,
      captchaData: form.captchaData
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

// 启动后由 TianaiCaptcha 内部去拉 /captchaImage,根据返回 captchaOnOff 决定是否显示
// 这里默认开,组件会自动隐藏
captchaEnabled.value = true

if (typeof window !== 'undefined') {
  setTimeout(() => {
    window.__LOGIN_DEBUG = { captchaEnabled, form }
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
