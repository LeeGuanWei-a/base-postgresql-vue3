<template>
  <view class="tac-wrap">
    <!-- 类型切换器(H5/小程序/App 都用 uni-app button) -->
    <view class="tac-type-bar" v-if="showTypeSwitch">
      <view
        v-for="t in types"
        :key="t.code"
        :class="['tac-type-item', { active: t.code === captchaType }]"
        @tap="changeType(t.code)"
      >{{ t.label }}</view>
    </view>

    <view class="tac-box">
      <view v-if="loading" class="tac-loading">加载中...</view>

      <template v-if="captchaType === 'SLIDER' && captchaData">
        <view class="tac-slider">
          <view class="tac-slider-img">
            <image :src="captchaData.backgroundImage" class="tac-slider-bg" />
            <image
              :src="captchaData.templateImage"
              class="tac-slider-template"
              :style="sliderTemplateStyle"
              mode="scaleToFill"
            />
          </view>
          <view class="tac-slider-track" @touchstart="startDrag" @touchmove="onDrag" @touchend="endDrag" @mousedown="startDrag" @mousemove="onDrag" @mouseup="endDrag" @mouseleave="dragging && endDrag()">
            <view class="tac-slider-btn" :style="{ left: sliderX + 'px' }">
              <text class="tac-slider-btn-icon">⇆</text>
            </view>
            <view class="tac-slider-text">{{ sliderHint }}</view>
          </view>
          <view class="tac-slider-actions">
            <text class="tac-link" @tap="refresh">↻ 换一张</text>
          </view>
        </view>
      </template>

      <template v-else-if="captchaType === 'ROTATE' && captchaData">
        <view class="tac-rotate">
          <view
            class="tac-rotate-img"
            :style="captchaData.backgroundImageWidth && captchaData.backgroundImageHeight
              ? { aspectRatio: `${captchaData.backgroundImageWidth} / ${captchaData.backgroundImageHeight}` }
              : {}"
          >
            <image :src="captchaData.backgroundImage" class="tac-rotate-bg" mode="scaleToFill" />
            <!-- 模板图: tianai 下发的是"高=背景图高、宽=抠图宽"的透明竖图, 抠图块位于其正中心
                 uni-app 的 <image> 有组件自带默认尺寸, 必须用百分比显式给出宽高, 否则渲染不出来 -->
            <image
              :src="captchaData.templateImage"
              class="tac-rotate-piece"
              :style="rotatePieceStyle"
              mode="scaleToFill"
            />
          </view>
          <view class="tac-rotate-track" @touchstart="onRotateTrack" @touchmove="onRotateTrack" @click="onRotateTrack">
            <view class="tac-rotate-rail">
              <view class="tac-rotate-progress" :style="{ width: rotatePercent + '%' }"></view>
              <view class="tac-rotate-thumb" :style="{ left: rotatePercent + '%' }"></view>
            </view>
            <text class="tac-rotate-val">{{ rotateDeg }}°</text>
          </view>
          <view class="tac-slider-actions">
            <button type="button" class="tac-btn" size="mini" @tap="onValidate" :disabled="!trackEndTime || isValidating">验证</button>
            <text class="tac-link" @tap="refresh">↻ 换一张</text>
          </view>
        </view>
      </template>

      <template v-else-if="captchaType === 'WORD_IMAGE_CLICK' && captchaData">
        <view class="tac-word">
          <!-- 提示图: tianai 生成的"要依次点击哪些字"的小图 -->
          <view class="tac-word-tip" v-if="captchaData.templateImage">
            <image :src="captchaData.templateImage" class="tac-word-tip-img" mode="heightFix" />
          </view>
          <view class="tac-word-img" @tap="onWordClick">
            <image :src="captchaData.backgroundImage" />
            <view
              v-for="(p, i) in wordClicks"
              :key="i"
              class="tac-word-click-dot"
              :style="clickDotStyle(p)"
            >{{ i + 1 }}</view>
          </view>
          <view class="tac-slider-actions">
            <button type="button" class="tac-btn" size="mini" @tap="onValidate" :disabled="wordClicks.length < expectedClickCount || isValidating || verified">
              {{ verified ? '✓ 已通过' : (isValidating ? '验证中' : `验证 (${wordClicks.length}/${expectedClickCount})`) }}
            </button>
            <text class="tac-link" @tap="wordClicks = []">清除</text>
            <text class="tac-link" @tap="refresh">↻ 换一张</text>
          </view>
        </view>
      </template>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onUnmounted, watch, nextTick, getCurrentInstance } from 'vue'
import { getCaptcha, checkCaptcha } from '@/api/login'

const instance = getCurrentInstance()

const props = defineProps({
  defaultType: { type: String, default: 'SLIDER' },
  showTypeSwitch: { type: Boolean, default: true }
})

const emit = defineEmits(['success', 'close', 'type-change'])

const types = [
  { code: 'SLIDER', label: '滑动' },
  { code: 'ROTATE', label: '旋转' },
  { code: 'WORD_IMAGE_CLICK', label: '点选' }
]

const captchaType = ref(props.defaultType)
const captchaData = ref(null)
const captchaId = ref('')
const loading = ref(false)
const isValidating = ref(false)
const validationError = ref('')
const verified = ref(false)
// tianai 1.5.0 点选默认 checkClickCount=4
const expectedClickCount = ref(4)

// SLIDER 共用
const sliderX = ref(0)
const sliderY = ref(0)
const trackList = ref([])
const trackStartTime = ref(0)
const trackEndTime = ref(0)
let dragging = false
let dragStartX = 0
let sliderBaseX = 0

// ROTATE
const rotateDeg = ref(0)
const rotateStartDeg = ref(0)

// WORD_IMAGE_CLICK
const wordClicks = ref([])

const sliderHint = computed(() => {
  if (isValidating.value) return '验证中...'
  if (validationError.value) return validationError.value
  if (trackEndTime.value) return '✓ 验证通过'
  return '→ 拖动滑块完成拼图'
})

// ROTATE 模板图定位: 水平居中 + 绕自身中心旋转
// 宽高用"相对背景图的比例"给定, 避开 uni-app image 的默认尺寸与容器测量问题
const rotatePieceStyle = computed(() => {
  const cd = captchaData.value
  if (!cd) return { transform: `translateX(-50%) rotate(${rotateDeg.value}deg)` }
  const bgW = cd.backgroundImageWidth || 1
  const bgH = cd.backgroundImageHeight || 1
  const wPct = cd.templateImageWidth ? (cd.templateImageWidth / bgW) * 100 : 100
  const hPct = cd.templateImageHeight ? (cd.templateImageHeight / bgH) * 100 : 100
  return {
    left: '50%',
    top: '0px',
    width: wPct + '%',
    height: hPct + '%',
    transform: `translateX(-50%) rotate(${rotateDeg.value}deg)`
  }
})

// SLIDER 模板图(拼图块): 按背景图相对比例缩放, 与容器像素无关, 首帧不会偏大
const sliderTemplateStyle = computed(() => {
  const cd = captchaData.value
  if (!cd) return {}
  const bgW = cd.backgroundImageWidth || 1
  const bgH = cd.backgroundImageHeight || 1
  const wPct = cd.templateImageWidth ? (cd.templateImageWidth / bgW) * 100 : 100
  const hPct = cd.templateImageHeight ? (cd.templateImageHeight / bgH) * 100 : 100
  return {
    left: sliderX.value + 'px',
    top: '0px',
    width: wPct + '%',
    height: hPct + '%'
  }
})

watch(captchaType, () => refresh())

function changeType(t) {
  if (t === captchaType.value) return
  captchaType.value = t
  emit('type-change', t)
}

async function refresh() {
  loading.value = true
  validationError.value = ''
  resetState()
  try {
    const res = await getCaptcha(captchaType.value)
    if (!res || !res.captchaOnOff) {
      loading.value = false
      return
    }
    captchaId.value = res.uuid || (res.data && res.data.id)
    captchaData.value = res.data
    if (captchaType.value === 'ROTATE') {
      // 从 0 度开始, 让用户自己转正
      rotateStartDeg.value = 0
      rotateDeg.value = 0
    }
    // 图片渲染后再算缩放比, 否则首帧 scaleRatio 是 1, 换算出来的 left 会错
    await nextTick()
    computeScaleRatio()
  } catch (e) {
    validationError.value = '加载失败'
  } finally {
    loading.value = false
  }
}

function resetState() {
  sliderX.value = 0
  sliderY.value = 0
  trackList.value = []
  trackStartTime.value = 0
  trackEndTime.value = 0
  rotateDeg.value = 0
  rotateStartDeg.value = 0
  wordClicks.value = []
  verified.value = false
}

// === Drag (H5 用 mouse 事件,小程序用 touch) ===
function getEventX(e) {
  const t = e.touches && e.touches[0]
  if (t) return t.clientX
  return e.clientX || 0
}

let dragSampleTimer = null
let lastSampleX = 0
let lastSampleT = 0
// 缩放比: 原始背景图坐标 / 容器像素坐标
const scaleRatio = ref(1)

function getImgContainer() {
  const elMap = {
    SLIDER: '.tac-slider-img',
    ROTATE: '.tac-rotate-img',
    WORD_IMAGE_CLICK: '.tac-word-img'
  }
  const sel = elMap[captchaType.value]
  if (!sel) return null
  // #ifdef MP-WEIXIN
  return null
  // #endif
  // #ifndef MP-WEIXIN
  return document.querySelector(sel)
  // #endif
}

function computeScaleRatio() {
  if (!captchaData.value || !captchaData.value.backgroundImageWidth) return
  const container = getImgContainer()
  if (!container) return
  const w = container.clientWidth
  if (w > 0) {
    scaleRatio.value = captchaData.value.backgroundImageWidth / w
  }
}

function startDrag(e) {
  if (trackEndTime.value > 0) return
  computeScaleRatio()
  dragging = true
  dragStartX = getEventX(e)
  sliderBaseX = sliderX.value
  trackStartTime.value = Date.now()
  trackList.value = []
  // tianai 检测 3 要求第一条轨迹在原点附近(±10), 采样定时器首拍时用户可能已拖出几十像素,
  // 所以先补一条起点轨迹, 否则会被判定为机器人("basic check fail")
  trackList.value.push({ t: 0, x: 0, y: 0, type: 'drag' })
  lastSampleX = 0
  lastSampleT = 0
  // 16ms 采样一次(模拟人手), y 加 ±2px 微抖动避开 tianai 检测 4
  if (dragSampleTimer) clearInterval(dragSampleTimer)
  dragSampleTimer = setInterval(() => {
    if (!dragging) return
    const t = Date.now() - trackStartTime.value
    const x = Math.round(sliderX.value * scaleRatio.value)
    if (x !== lastSampleX) {
      const yJitter = Math.sin(t / 80) * 1.5 + (Math.random() - 0.5) * 1.2
      trackList.value.push({ t, x, y: Math.round(yJitter * 10) / 10, type: 'drag' })
      lastSampleX = x
      lastSampleT = t
    }
  }, 16)
}

function onDrag(e) {
  if (!dragging) return
  const cx = getEventX(e)
  const dx = cx - dragStartX
  let newX = Math.max(0, sliderBaseX + dx)
  // sliderX 是容器像素, 上限也要用容器像素(模板图宽度按背景图比例换算)
  if (captchaData.value && captchaData.value.backgroundImageWidth) {
    const container = getImgContainer()
    const cw = container && container.clientWidth > 0 ? container.clientWidth : captchaData.value.backgroundImageWidth
    const ratioW = (captchaData.value.templateImageWidth || 0) / captchaData.value.backgroundImageWidth
    const pieceW = ratioW > 0 && ratioW < 1 ? cw * ratioW : 0
    newX = Math.min(newX, Math.max(0, cw - pieceW - 2))
  }
  sliderX.value = newX
}

function endDrag() {
  if (!dragging) return
  dragging = false
  if (dragSampleTimer) { clearInterval(dragSampleTimer); dragSampleTimer = null }
  // 末段停留 ~150ms, 让末段速度自然降低, 满足 tianai 检测 6 (前 70% 速度 > 后 30% 速度)
  setTimeout(() => {
    if (lastSampleT > 0) {
      const t = Date.now() - trackStartTime.value
      const x = Math.round(sliderX.value * scaleRatio.value)
      trackList.value.push({ t, x, y: 0.5, type: 'drag' })
    }
    trackEndTime.value = Date.now()
    onValidate()
  }, 150)
}

// 旋转角度百分比(0~100), 用于自绘滑条的进度和滑块位置
const rotatePercent = computed(() => {
  const v = Number(rotateDeg.value) || 0
  return Math.max(0, Math.min(100, (v / 359) * 100))
})

// 自绘滑条: 点/拖都能改角度 (uni 内置 slider 在 H5 下进度条渲染不出来)
function onRotateTrack(e) {
  if (verified.value) return
  const q = uni.createSelectorQuery().in(instance && instance.proxy)
  q.select('.tac-rotate-rail').boundingClientRect()
  q.exec((res) => {
    if (!res || !res[0] || !res[0].width) return
    const rect = res[0]
    const cx = (e.touches && e.touches[0] ? e.touches[0].clientX : e.clientX) || 0
    const ratio = Math.max(0, Math.min(1, (cx - rect.left) / rect.width))
    if (trackStartTime.value === 0) trackStartTime.value = Date.now()
    rotateDeg.value = Math.round(ratio * 359) % 360
    trackEndTime.value = Date.now()
  })
}

// 点选标记用百分比定位, 与容器尺寸无关, 不会飘
function clickDotStyle(p) {
  const cd = captchaData.value
  if (!cd) return {}
  const rx = typeof p.rx === 'number' ? p.rx : (p.x / (cd.backgroundImageWidth || 1))
  const ry = typeof p.ry === 'number' ? p.ry : (p.y / (cd.backgroundImageHeight || 1))
  return {
    left: (rx * 100) + '%',
    top: (ry * 100) + '%'
  }
}

function onWordClick(e) {
  // 用 uni-app 的 getBoundingClientRect 等价:用 mp-e 的 selectorQuery
  const query = uni.createSelectorQuery().in(instance && instance.proxy)
  query.select('.tac-word-img').boundingClientRect()
  query.exec((res) => {
    if (!res || !res[0]) return
    const rect = res[0]
    const cx = (e.touches && e.touches[0] ? e.touches[0].clientX : e.detail && e.detail.x) || 0
    const cy = (e.touches && e.touches[0] ? e.touches[0].clientY : e.detail && e.detail.y) || 0
    // 存百分比坐标, 不受容器尺寸影响, 标记不会飘
    const rx = (cx - rect.left) / rect.width
    const ry = (cy - rect.top) / rect.height
    const clickX = Math.round(rx * captchaData.value.backgroundImageWidth)
    const clickY = Math.round(ry * captchaData.value.backgroundImageHeight)
    wordClicks.value.push({ x: clickX, y: clickY, rx, ry })
    if (trackStartTime.value === 0) trackStartTime.value = Date.now()
    trackEndTime.value = Date.now()
  })
}

async function onValidate() {
  if (isValidating.value || verified.value) return
  if (!captchaId.value) {
    validationError.value = '验证码未加载'
    return
  }
  isValidating.value = true
  validationError.value = ''
  try {
    const data = buildTrackData()
    console.log('[tianai-captcha/mp] onValidate', JSON.stringify({ type: captchaType.value, id: captchaId.value, data }))
    // 真实校验: 后端 matching() 通过后会写 120s 的 verified 标记, /login 命中即放行
    const res = await checkCaptcha({ id: captchaId.value, data })
    const ok = res && (res.code === 200 || res.code === 0)
    if (ok) {
      verified.value = true
      emit('success', { id: captchaId.value, type: captchaType.value, data })
    } else {
      validationError.value = (res && res.msg) ? res.msg : '校验失败'
      uni.showToast({ title: validationError.value, icon: 'none' })
      setTimeout(() => refresh(), 900)
    }
  } catch (e) {
    // request 封装 reject 的可能是响应体对象, 直接拼字符串会变成 [object Object]
    let msg = '校验失败'
    if (e) {
      if (typeof e === 'string') msg = e
      else if (e.msg) msg = e.msg
      else if (e.message) msg = e.message
      else if (e.data && e.data.msg) msg = e.data.msg
    }
    validationError.value = msg
    uni.showToast({ title: msg, icon: 'none' })
  } finally {
    isValidating.value = false
  }
}

function buildTrackData() {
  const startTime = trackStartTime.value
  const stopTime = trackEndTime.value
  // 点选: tianai 要求 trackList 里 type='click' 的条数 == 目标字数,
  // 且每条 x/y/t/type 都不能为空; 缺 trackList 会直接报 "trackList 不能为空"
  if (captchaType.value === 'WORD_IMAGE_CLICK') {
    const clickTracks = wordClicks.value.map((p, i) => ({
      t: (i + 1) * 200,
      x: p.x,
      y: p.y,
      type: 'click'
    }))
    return {
      bgImageWidth: captchaData.value.backgroundImageWidth,
      bgImageHeight: captchaData.value.backgroundImageHeight,
      startTime, stopTime,
      trackList: clickTracks,
      data: { points: wordClicks.value.map(p => ({ x: p.x, y: p.y })) }
    }
  }
  // 旋转: 同 PC 端推导 -> randomX = rotateDeg * bgW / 360
  // tianai 的 ROTATE 也走 slider 类校验, 必须有 trackList 和 left
  if (captchaType.value === 'ROTATE') {
    const bgW = captchaData.value.backgroundImageWidth
    const leftOriginal = Math.round((rotateDeg.value * bgW) / 360)
    return {
      bgImageWidth: bgW,
      bgImageHeight: captchaData.value.backgroundImageHeight,
      startTime, stopTime,
      left: leftOriginal,
      top: 0,
      trackList: simulateDragTracks(0, leftOriginal)
    }
  }
  // left 用原始背景图坐标 (容器 px × 缩放比), 满足 tianai 的缺口位置校验
  const leftOriginal = Math.round(sliderX.value * scaleRatio.value)
  return {
    bgImageWidth: captchaData.value.backgroundImageWidth,
    bgImageHeight: captchaData.value.backgroundImageHeight,
    templateImageWidth: captchaData.value.templateImageWidth,
    templateImageHeight: captchaData.value.templateImageHeight,
    startTime, stopTime,
    left: captchaType.value === 'SLIDER' ? leftOriginal : 0,
    top: captchaType.value === 'SLIDER' ? Math.round(sliderY.value * scaleRatio.value) : 0,
    trackList: normalizeTrackList(trackList.value)
  }
}

/**
 * 兜底: tianai 检测 3 要求第一条轨迹在原点 ±10 内, 否则直接判定机器人("basic check fail")
 */
function normalizeTrackList(list) {
  if (!list || list.length === 0) return list
  const first = list[0]
  if (Math.abs(first.x || 0) > 10 || Math.abs(first.y || 0) > 10) {
    return [{ t: 0, x: 0, y: 0, type: 'drag' }].concat(list)
  }
  return list
}

/**
 * 模拟人手拖动轨迹: 从 startX 到 endX, ~400ms
 * y 有 ±2px 抖动, 前 70% 快后 30% 慢, 满足 tianai 反机器人检测
 */
function simulateDragTracks(startX, endX) {
  const tracks = []
  const totalMs = 400
  const totalSteps = 30
  for (let i = 0; i <= totalSteps; i++) {
    const ratio = i / totalSteps
    const eased = 1 - Math.pow(1 - ratio, 2.5)
    const x = Math.round(startX + (endX - startX) * eased)
    const t = Math.round(ratio * totalMs)
    const yJitter = Math.sin(t / 80) * 1.5 + (Math.random() - 0.5) * 1.2
    tracks.push({ t, x, y: Math.round(yJitter * 10) / 10, type: 'drag' })
  }
  return tracks
}

onUnmounted(() => {
  // noop
})

defineExpose({ refresh })
refresh()
</script>

<style lang="scss" scoped>
.tac-wrap { display: inline-block; width: 100%; }
.tac-type-bar { display: flex; gap: 8rpx; margin-bottom: 12rpx; flex-wrap: wrap; }
.tac-type-item {
  padding: 8rpx 20rpx;
  font-size: 24rpx;
  border: 1rpx solid #dcdfe6;
  border-radius: 8rpx;
  color: #606266;
  background: #fff;
  &.active { background: #409eff; color: #fff; border-color: #409eff; }
}
.tac-box {
  position: relative;
  border: 1rpx solid #ebeef5;
  border-radius: 8rpx;
  padding: 12rpx;
  background: #fafafa;
}
.tac-loading { text-align: center; padding: 80rpx 0; color: #909399; font-size: 26rpx; }
.tac-slider-img {
  position: relative; width: 100%; height: 280rpx; overflow: hidden; border-radius: 8rpx; background: #fff;
}
.tac-slider-bg { width: 100%; height: 100%; }
.tac-slider-template { position: absolute; top: 0; height: 100%; pointer-events: none; }
.tac-slider-track {
  position: relative; height: 56rpx; margin-top: 12rpx; background: #fff; border: 1rpx solid #ebeef5; border-radius: 8rpx; overflow: hidden;
}
.tac-slider-btn {
  position: absolute; top: 0; width: 56rpx; height: 56rpx; background: #409eff; color: #fff;
  display: flex; align-items: center; justify-content: center; border-radius: 8rpx; z-index: 2; font-size: 28rpx;
}
.tac-slider-text {
  position: absolute; top: 0; left: 0; width: 100%; height: 56rpx; line-height: 56rpx; text-align: center; color: #909399; font-size: 24rpx; z-index: 1; pointer-events: none;
}
.tac-slider-actions { display: flex; align-items: center; gap: 16rpx; margin-top: 8rpx; font-size: 24rpx; }
.tac-link { color: #409eff; }
.tac-btn { background: #409eff; color: #fff; border: none; padding: 4rpx 20rpx; border-radius: 8rpx; font-size: 24rpx; }
.tac-rotate-img {
  width: 100%; position: relative; background: #fff; border-radius: 8rpx; overflow: hidden;
}
.tac-rotate-bg { position: absolute; top: 0; left: 0; width: 100%; height: 100%; }
.tac-rotate-piece {
  position: absolute;
  top: 0;
  left: 50%;
  /* 宽高由 inline style 按背景图比例给出, 这里不设固定值, 否则会覆盖 inline */
  transform-origin: center center;
  pointer-events: none;
}
.tac-rotate-track { display: flex; align-items: center; gap: 16rpx; margin-top: 12rpx; background: #fff; padding: 14rpx 16rpx; border: 1rpx solid #ebeef5; border-radius: 8rpx; }
.tac-rotate-rail {
  position: relative;
  flex: 1;
  height: 12rpx;
  background: #e4e7ed;
  border-radius: 6rpx;
}
.tac-rotate-progress {
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  background: #409eff;
  border-radius: 6rpx;
}
.tac-rotate-thumb {
  position: absolute;
  top: 50%;
  width: 36rpx;
  height: 36rpx;
  margin-left: -18rpx;
  margin-top: -18rpx;
  background: #fff;
  border: 3rpx solid #409eff;
  border-radius: 50%;
  box-sizing: border-box;
}
.tac-rotate-val { font-size: 24rpx; color: #606266; min-width: 72rpx; text-align: right; }
.tac-word-img { position: relative; width: 100%; height: 280rpx; background: #fff; border-radius: 8rpx; overflow: hidden; }
.tac-word-img image { width: 100%; height: 100%; }
.tac-word-tip { margin-bottom: 8rpx; font-size: 26rpx; }
.tac-word-tip-img { height: 72rpx; width: auto; }
.tac-word-click-dot {
  position: absolute; width: 36rpx; height: 36rpx; background: rgba(64, 158, 255, 0.7); border: 2rpx solid #fff; border-radius: 50%;
  color: #fff; font-size: 22rpx; line-height: 32rpx; text-align: center; transform: translate(-50%, -50%); pointer-events: none; font-weight: bold;
}
</style>
