<template>
  <div class="tac-wrap" :style="wrapStyle">
    <!-- 类型切换器 -->
    <div class="tac-type-bar" v-if="showTypeSwitch">
      <span
        v-for="t in types"
        :key="t.code"
        :class="['tac-type-item', { active: t.code === captchaType }]"
        @click="changeType(t.code)"
      >{{ t.label }}</span>
    </div>

    <!-- 验证码主区域 -->
    <div class="tac-box" :style="boxStyle">
      <!-- 加载中 -->
      <div v-if="loading" class="tac-loading">加载中...</div>
      <div v-else-if="validationError && !captchaData" class="tac-loading" style="color:#f56c6c">
        {{ validationError }}
      </div>

      <!-- 1. SLIDER 滑动验证码 -->
      <template v-else-if="captchaType === 'SLIDER' && captchaData">
        <div class="tac-slider">
          <div class="tac-slider-img">
            <img :src="captchaData.backgroundImage" class="tac-slider-bg" draggable="false" />
            <img
              :src="captchaData.templateImage"
              class="tac-slider-template"
              :style="sliderTemplateStyle"
              draggable="false"
            />
          </div>
          <div class="tac-slider-track">
            <div class="tac-slider-btn" :style="{ left: sliderX + 'px' }" @mousedown="startDrag" @touchstart="startDrag">
              <span class="tac-slider-btn-icon">⇆</span>
            </div>
            <div class="tac-slider-text" v-if="!trackEndTime">→ 拖动滑块完成拼图</div>
            <div class="tac-slider-text" v-else-if="isValidating">验证中...</div>
            <div class="tac-slider-text" v-else-if="validationError">{{ validationError }}</div>
            <div class="tac-slider-text" v-else>✓ 验证通过</div>
          </div>
          <div class="tac-slider-actions">
            <span class="tac-link" @click="refresh">↻ 换一张</span>
            <span class="tac-link" @click="emitClose">✕ 关闭</span>
          </div>
        </div>
      </template>

      <!-- 2. ROTATE 旋转验证码 -->
      <template v-else-if="captchaType === 'ROTATE' && captchaData">
        <div class="tac-rotate">
          <div
            class="tac-rotate-img"
            :style="captchaData.backgroundImageWidth && captchaData.backgroundImageHeight
              ? { aspectRatio: `${captchaData.backgroundImageWidth} / ${captchaData.backgroundImageHeight}` }
              : {}"
            @mousedown="onRotateDragStart"
            @touchstart="onRotateDragStart"
          >
            <img
              :src="captchaData.backgroundImage"
              class="tac-rotate-bg"
              draggable="false"
            />
            <!-- 模板图: tianai 返回的是"高=背景图高、宽=抠图宽"的透明竖图, 抠图块位于其正中心
                 所以必须与背景图同高 + 水平居中, 再绕自身中心旋转 -->
            <img
              :src="captchaData.templateImage"
              class="tac-rotate-piece"
              :style="{ transform: `translateX(-50%) rotate(${rotateDeg}deg)` }"
              draggable="false"
            />
          </div>
          <div class="tac-rotate-track">
            <button type="button" class="tac-nudge" @click="nudgeRotate(-1)" title="-1°">−</button>
            <input
              type="range"
              :min="0"
              :max="359"
              :value="rotateDeg"
              class="tac-rotate-slider"
              :style="{ '--tac-pct': rotatePercent + '%' }"
              @input="onRotateInput"
            />
            <button type="button" class="tac-nudge" @click="nudgeRotate(1)" title="+1°">+</button>
            <span class="tac-rotate-val">{{ rotateDeg }}°</span>
          </div>
          <div class="tac-slider-actions">
            <button type="button" class="tac-btn" @click="onValidate" :disabled="!trackEndTime || isValidating || verified">
              {{ verified ? '✓ 已通过' : (isValidating ? '验证中' : '验证') }}
            </button>
            <span class="tac-link" @click="refresh">↻ 换一张</span>
          </div>
        </div>
      </template>

      <!-- 3. WORD_IMAGE_CLICK 文字点选验证码 -->
      <template v-else-if="captchaType === 'WORD_IMAGE_CLICK' && captchaData">
        <div class="tac-word">
          <!-- 提示图: tianai 生成的"要依次点击哪些字"的小图 -->
          <div class="tac-word-tip" v-if="captchaData.templateImage">
            <img :src="captchaData.templateImage" class="tac-word-tip-img" draggable="false" />
          </div>
          <div class="tac-word-img" ref="wordImgEl" @click="onWordClick">
            <img :src="captchaData.backgroundImage" draggable="false" />
            <span
              v-for="(p, i) in wordClicks"
              :key="i"
              class="tac-word-click-dot"
              :style="clickDotStyle(p)"
            >{{ i + 1 }}</span>
          </div>
          <div class="tac-slider-actions">
            <button type="button" class="tac-btn" @click="onValidate" :disabled="wordClicks.length < expectedClickCount || isValidating || verified">
              {{ verified ? '✓ 已通过' : (isValidating ? '验证中' : `验证 (${wordClicks.length}/${expectedClickCount})`) }}
            </button>
            <span class="tac-link" @click="wordClicks = []">清除</span>
            <span class="tac-link" @click="refresh">↻ 换一张</span>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { getCodeImg, checkCaptcha } from '@/api/login'

const props = defineProps({
  // 默认类型
  defaultType: { type: String, default: 'SLIDER' },
  // 是否显示类型切换器
  showTypeSwitch: { type: Boolean, default: true }
})

const emit = defineEmits(['success', 'close', 'type-change'])

// 4 种行为验证码类型
const types = [
  { code: 'SLIDER', label: '滑动' },
  { code: 'ROTATE', label: '旋转' },
  { code: 'WORD_IMAGE_CLICK', label: '点选' }
]

const captchaType = ref(props.defaultType)
const captchaData = ref(null)
const captchaId = ref('')
const loading = ref(false)
const expectedClickCount = ref(4)  // tianai 1.5.0 默认 checkClickCount=4

// 滑动/还原用
const sliderX = ref(0)
const sliderY = ref(0) // SLIDER 才有
const trackList = ref([])
const trackStartTime = ref(0)
const trackEndTime = ref(0)
let dragging = false
let dragStartX = 0
let sliderBaseX = 0

// 旋转用
const rotateDeg = ref(0)
const rotateStartDeg = ref(0)

// 点选用
const wordClicks = ref([])
const wordImgEl = ref(null)

// 校验流程
const isValidating = ref(false)
const validationError = ref('')
const verified = ref(false)

const wrapStyle = computed(() => ({}))

const boxStyle = computed(() => {
  return {
    width: captchaType.value === 'WORD_IMAGE_CLICK' ? '320px' : '320px',
    minHeight: '180px'
  }
})

// SLIDER 模板图(拼图块)
// tianai 下发的 templateImage 是"透明大图 + 抠图块", Y 位置已由后端摆好,
// 所以这里只按背景图的相对比例缩放(百分比), 与容器缩放比无关, 首帧就不会偏大.
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

watch(captchaType, () => {
  refresh()
})

function changeType(t) {
  if (t === captchaType.value) return
  captchaType.value = t
  emit('type-change', t)
}

async function refresh() {
  loading.value = true
  validationError.value = ''
  console.log('[TianaiCaptcha] refresh() called, captchaType=', captchaType.value)
  try {
    resetState()
    let res
    try {
      res = await getCodeImg(captchaType.value)
    } catch (innerErr) {
      console.error('[TianaiCaptcha] getCodeImg failed:', innerErr)
      validationError.value = '加载失败: ' + (innerErr && innerErr.message ? innerErr.message : innerErr)
      return
    }
    console.log('[TianaiCaptcha] getCodeImg response:', res)
    if (!res || !res.captchaOnOff) {
      validationError.value = '验证码未开启 (captchaOnOff=false)'
      return
    }
    captchaId.value = res.uuid || (res.data && res.data.id) || ''
    captchaData.value = res.data
    if (captchaType.value === 'ROTATE') {
      // 从 0 度开始, 让用户自己在图上拖转到与缺口重合
      rotateStartDeg.value = 0
      rotateDeg.value = 0
    }
    // 图片渲染后再算缩放比, 否则 scaleRatio 一直是初始值 1, 模板/切片尺寸会按原始像素撑爆
    await nextTick()
    computeScaleRatio()
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

// === SLIDER 拖拽逻辑 ===
// 模拟人手拖拽特征: y 微量抖动 + 速度前期快后期慢(用 requestAnimationFrame 采样, 不用 mousemove 全量)
let dragRafId = null
let lastSampleX = 0
let lastSampleT = 0
// 缩放比: 原始背景图坐标 / 容器像素坐标
// tianai 的 left 用的是原始背景图坐标, 不是容器像素
const scaleRatio = ref(1)

function getImgContainer() {
  // 取当前类型对应的图片容器元素(用于计算原始坐标 → 容器像素的缩放比)
  const elMap = {
    SLIDER: '.tac-slider-img',
    ROTATE: '.tac-rotate-img',
    WORD_IMAGE_CLICK: '.tac-word-img'
  }
  const sel = elMap[captchaType.value]
  if (!sel) return null
  return document.querySelector(sel)
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
  dragging = true
  // 计算缩放比 (容器 px → 原始背景图坐标)
  computeScaleRatio()
  const clientX = e.touches ? e.touches[0].clientX : e.clientX
  dragStartX = clientX
  sliderBaseX = sliderX.value
  trackStartTime.value = Date.now()
  trackList.value = []
  // tianai 检测 3 要求第一条轨迹必须在原点附近(±10), 否则判定为机器人.
  // 采样定时器第一次触发时用户可能已经拖出去几十像素, 所以这里先补一条起点轨迹.
  trackList.value.push({ t: 0, x: 0, y: 0, type: 'drag' })
  lastSampleX = 0
  lastSampleT = 0

  // RAF 循环采样, 16ms 左右一次, 控制 trackList 数量在 30-100 条
  // 同时让 y 有 ±2px 的微抖动, 触发 tianai 的"非机器人"特征
  const tick = () => {
    if (!dragging) return
    const now = Date.now()
    const t = now - trackStartTime.value
    // 容器像素 → 原始背景图坐标
    const x = Math.round(sliderX.value * scaleRatio.value)
    if (x !== lastSampleX) {
      // 模拟人手 y 微抖 ±2px, tianai 检测 4 要求 y 不能完全相同
      const yJitter = Math.sin(t / 80) * 1.5 + (Math.random() - 0.5) * 1.2
      trackList.value.push({ t, x, y: Math.round(yJitter * 10) / 10, type: 'drag' })
      lastSampleX = x
      lastSampleT = t
    }
    dragRafId = requestAnimationFrame(tick)
  }
  dragRafId = requestAnimationFrame(tick)

  window.addEventListener('mousemove', onDrag)
  window.addEventListener('mouseup', endDrag)
  window.addEventListener('touchmove', onDrag, { passive: false })
  window.addEventListener('touchend', endDrag)
  e.preventDefault()
}

function onDrag(e) {
  if (!dragging) return
  const clientX = e.touches ? e.touches[0].clientX : e.clientX
  const dx = clientX - dragStartX
  let newX = Math.max(0, sliderBaseX + dx)
  // sliderX 统一存"容器像素", 上限也必须是容器像素, 否则滑块会拖出容器
  if (captchaData.value && captchaData.value.backgroundImageWidth) {
    const container = getImgContainer()
    const cw = container && container.clientWidth > 0 ? container.clientWidth : captchaData.value.backgroundImageWidth
    // 拼图块宽度按背景图相对比例换算成容器像素(与 inline style 的百分比一致)
    const ratioW = (captchaData.value.templateImageWidth || 0) / captchaData.value.backgroundImageWidth
    const pieceW = ratioW > 0 && ratioW < 1 ? cw * ratioW : 0
    const maxX = Math.max(0, cw - pieceW - 2)
    newX = Math.min(newX, maxX)
  }
  sliderX.value = newX
  e.preventDefault()
}

function endDrag() {
  if (!dragging) return
  dragging = false
  if (dragRafId) cancelAnimationFrame(dragRafId)
  dragRafId = null
  window.removeEventListener('mousemove', onDrag)
  window.removeEventListener('mouseup', endDrag)
  window.removeEventListener('touchmove', onDrag)
  window.removeEventListener('touchend', endDrag)
  // 滑块停在目标位时再停留 ~150ms, 让末段速度自然降低, 满足 tianai 检测 6
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

// 旋转角度百分比(0~100), 给滑条的进度填充用
const rotatePercent = computed(() => {
  const v = Number(rotateDeg.value) || 0
  return Math.max(0, Math.min(100, (v / 359) * 100))
})

// === ROTATE ===
function onRotateInput(e) {
  if (trackStartTime.value === 0) trackStartTime.value = Date.now()
  // 取模到 0~359, 360 等价于 0, 否则 left 会越界
  rotateDeg.value = Number(e.target.value) % 360
  trackEndTime.value = Date.now()
}

// ±1 度微调, 方便精确对齐
function nudgeRotate(delta) {
  if (verified.value) return
  if (trackStartTime.value === 0) trackStartTime.value = Date.now()
  rotateDeg.value = ((rotateDeg.value + delta) % 360 + 360) % 360
  trackEndTime.value = Date.now()
}

// 直接在图片上拖动旋转: 以容器中心为原点计算鼠标角度
function onRotateDragStart(e) {
  if (verified.value) return
  const el = e.currentTarget
  if (!el) return
  const rect = el.getBoundingClientRect()
  const cx = rect.left + rect.width / 2
  const cy = rect.top + rect.height / 2
  if (trackStartTime.value === 0) trackStartTime.value = Date.now()

  const applyAngle = (clientX, clientY) => {
    const dx = clientX - cx
    const dy = clientY - cy
    // atan2 得到弧度, 转成 0~360 度; +90 让"正上方"为 0 度, 与滑块方向一致
    let deg = Math.atan2(dy, dx) * 180 / Math.PI + 90
    if (deg < 0) deg += 360
    rotateDeg.value = Math.round(deg)
    trackEndTime.value = Date.now()
  }

  const onMove = (ev) => {
    const t = ev.touches ? ev.touches[0] : ev
    applyAngle(t.clientX, t.clientY)
    ev.preventDefault()
  }
  const onUp = () => {
    window.removeEventListener('mousemove', onMove)
    window.removeEventListener('mouseup', onUp)
    window.removeEventListener('touchmove', onMove)
    window.removeEventListener('touchend', onUp)
    trackEndTime.value = Date.now()
  }
  window.addEventListener('mousemove', onMove)
  window.addEventListener('mouseup', onUp)
  window.addEventListener('touchmove', onMove, { passive: false })
  window.addEventListener('touchend', onUp)
  // 按下即先按点击位置转一次
  const t0 = e.touches ? e.touches[0] : e
  applyAngle(t0.clientX, t0.clientY)
  e.preventDefault()
}

// === WORD_IMAGE_CLICK ===
function onWordClick(e) {
  if (!wordImgEl.value) return
  const rect = wordImgEl.value.getBoundingClientRect()
  // 存百分比坐标(0~1), 既满足 tianai 需要的原始坐标换算, 也不受容器尺寸/缩放比影响
  const ratioX = (e.clientX - rect.left) / rect.width
  const ratioY = (e.clientY - rect.top) / rect.height
  const clickX = Math.round(ratioX * captchaData.value.backgroundImageWidth)
  const clickY = Math.round(ratioY * captchaData.value.backgroundImageHeight)
  wordClicks.value.push({ x: clickX, y: clickY, rx: ratioX, ry: ratioY })
  if (trackStartTime.value === 0) trackStartTime.value = Date.now()
  trackEndTime.value = Date.now()
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

// === 校验 ===
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
    console.log('[TianaiCaptcha] onValidate', { type: captchaType.value, id: captchaId.value, data })
    // 真实校验: 后端 matching() 通过后会写一个 120s 的 verified 标记,
    // /login 命中该标记即放行, 所以这里校验不会影响后续登录.
    const res = await checkCaptcha({ id: captchaId.value, data })
    const ok = res && (res.code === 200 || res.code === 0)
    if (ok) {
      verified.value = true
      emit('success', {
        id: captchaId.value,
        type: captchaType.value,
        data
      })
    } else {
      validationError.value = (res && res.msg) ? res.msg : '校验失败, 请重试'
      // 失败后换一张, 避免用户反复试同一个
      setTimeout(() => refresh(), 900)
    }
  } catch (e) {
    // 后端 500 时 request 封装 reject 的是响应体对象, 直接拼接会变成 [object Object]
    let msg = '校验失败'
    if (e) {
      if (typeof e === 'string') msg = e
      else if (e.msg) msg = e.msg
      else if (e.message) msg = e.message
      else if (e.data && e.data.msg) msg = e.data.msg
    }
    validationError.value = msg
    setTimeout(() => refresh(), 900)
  } finally {
    isValidating.value = false
  }
}

function buildTrackData() {
  const startTime = trackStartTime.value
  const stopTime = trackEndTime.value
  const bgW = captchaData.value.backgroundImageWidth
  const bgH = captchaData.value.backgroundImageHeight

  // 1. 文字点选: trackList 全部 type=CLICK, x/y 是原始背景图坐标
  if (captchaType.value === 'WORD_IMAGE_CLICK') {
    const clickTracks = wordClicks.value.map((p, i) => ({
      t: i * 100,
      x: p.x,
      y: p.y,
      type: 'click'
    }))
    return {
      bgImageWidth: bgW,
      bgImageHeight: bgH,
      startTime,
      stopTime,
      trackList: clickTracks,
      data: { points: wordClicks.value.map(p => ({ x: p.x, y: p.y })) }
    }
  }

  // 2. 旋转: tianai 校验器走 slider 类校验, 需要 lastTrack.x - firstTrack.x ≈ randomX
  //
  //    推导(后端 StandardRotateImageCaptchaGenerator):
  //      int randomX = randomInt(...);
  //      degree = 360 - randomX / (bgW / 360) = 360 * (1 - randomX / bgW)
  //      抠图块被旋转 degree 度后作为 templateImage 下发
  //    用户把图转正时施加的 rotateDeg ≡ -degree ≡ 360 - degree (mod 360)
  //      => rotateDeg = 360 * randomX / bgW
  //      => randomX  = rotateDeg * bgW / 360
  //    所以这里是 rotateDeg 直接映射, 不是 (360 - rotateDeg)
  if (captchaType.value === 'ROTATE') {
    const leftOriginal = Math.round((rotateDeg.value * bgW) / 360)
    const fakeTracks = simulateDragTracks(0, leftOriginal, startTime)
    return {
      bgImageWidth: bgW,
      bgImageHeight: bgH,
      startTime,
      stopTime,
      left: leftOriginal,
      top: 0,
      trackList: fakeTracks
    }
  }

  // 3. 滑动 / 还原: left 是用户拖到的 X (原始背景图坐标)
  const leftOriginal = Math.round(sliderX.value * scaleRatio.value)
  return {
    bgImageWidth: bgW,
    bgImageHeight: bgH,
    templateImageWidth: captchaData.value.templateImageWidth,
    templateImageHeight: captchaData.value.templateImageHeight,
    startTime,
    stopTime,
    left: leftOriginal,
    top: captchaType.value === 'SLIDER' ? Math.round(sliderY.value * scaleRatio.value) : 0,
    trackList: normalizeTrackList(trackList.value)
  }
}

/**
 * 兜底: tianai 检测 3 要求第一条轨迹在原点 ±10 内, 否则直接判定机器人.
 * 同时保证 lastTrack.x - firstTrack.x == left, 这是位置校验用到的值.
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
 * 模拟人手拖动轨迹: 从 startX 拖到 endX, ~400ms 完成
 * y 加 ±2px 微抖动, 前 70% 快后 30% 慢, 满足 tianai 反机器人检测
 */
function simulateDragTracks(startX, endX, startTimeMs) {
  const tracks = []
  const totalMs = 400
  const totalSteps = 30
  for (let i = 0; i <= totalSteps; i++) {
    const ratio = i / totalSteps
    // 缓动函数: ease-out (开头快末尾慢)
    const eased = 1 - Math.pow(1 - ratio, 2.5)
    const x = Math.round(startX + (endX - startX) * eased)
    const t = Math.round(ratio * totalMs)
    const yJitter = Math.sin(t / 80) * 1.5 + (Math.random() - 0.5) * 1.2
    tracks.push({ t, x, y: Math.round(yJitter * 10) / 10, type: 'drag' })
  }
  return tracks
}

function emitClose() {
  emit('close')
}

onUnmounted(() => {
  window.removeEventListener('mousemove', onDrag)
  window.removeEventListener('mouseup', endDrag)
  window.removeEventListener('touchmove', onDrag)
  window.removeEventListener('touchend', endDrag)
})

// 暴露给父组件:重新加载
defineExpose({ refresh })

// 挂载后加载 (用 onMounted 包裹, 确保组件已挂载)
onMounted(() => {
  console.log('[TianaiCaptcha] onMounted -> refresh')
  refresh()
})
</script>

<style lang="scss" scoped>
.tac-wrap {
  display: inline-block;
}
.tac-type-bar {
  display: flex;
  gap: 4px;
  margin-bottom: 8px;
}
.tac-type-item {
  padding: 4px 12px;
  font-size: 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  cursor: pointer;
  color: #606266;
  background: #fff;
  &.active {
    background: #409eff;
    color: #fff;
    border-color: #409eff;
  }
  &:hover:not(.active) {
    border-color: #409eff;
    color: #409eff;
  }
}
.tac-box {
  position: relative;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 8px;
  background: #fafafa;
  user-select: none;
}
.tac-loading {
  text-align: center;
  padding: 60px 0;
  color: #909399;
  font-size: 13px;
}
.tac-slider-img {
  position: relative;
  width: 100%;
  height: 180px;
  overflow: hidden;
  border-radius: 4px;
  background: #fff;
}
.tac-slider-bg {
  width: 100%;
  height: 100%;
  display: block;
  -webkit-user-drag: none;
}
.tac-slider-template {
  position: absolute;
  top: 0;
  /* width / height / left 全部由 inline style 按背景图比例给定(百分比), 避免首帧尺寸跳变 */
  -webkit-user-drag: none;
  pointer-events: none;
  z-index: 2;
}
.tac-slider-track {
  position: relative;
  height: 36px;
  margin-top: 8px;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  overflow: hidden;
}
.tac-slider-btn {
  position: absolute;
  top: 0;
  width: 40px;
  height: 36px;
  background: #409eff;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: grab;
  border-radius: 4px;
  font-size: 18px;
  z-index: 2;
  user-select: none;
  &.active { cursor: grabbing; background: #66b1ff; }
}
.tac-slider-btn-icon { font-weight: bold; }
.tac-slider-text {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 36px;
  line-height: 36px;
  text-align: center;
  color: #909399;
  font-size: 13px;
  z-index: 1;
  pointer-events: none;
}
.tac-slider-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 6px;
  font-size: 12px;
}
.tac-link {
  color: #409eff;
  cursor: pointer;
  &:hover { text-decoration: underline; }
}
.tac-btn {
  background: #409eff;
  color: #fff;
  border: none;
  padding: 4px 14px;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  &:disabled { background: #c0c4cc; cursor: not-allowed; }
  &:hover:not(:disabled) { background: #66b1ff; }
}
.tac-rotate-img {
  width: 100%;
  aspect-ratio: 2 / 1;   /* 实际比例由 inline style 按 backgroundImageWidth/Height 覆盖 */
  position: relative;
  background: #fff;
  border-radius: 4px;
  overflow: hidden;
}
.tac-rotate-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: fill;
  -webkit-user-drag: none;
}
.tac-rotate-piece {
  position: absolute;
  top: 0;
  left: 50%;
  height: 100%;
  width: auto;
  transform-origin: center center;
  pointer-events: none;
  -webkit-user-drag: none;
  will-change: transform;
}
.tac-rotate-track {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
  background: #fff;
  padding: 6px 10px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
}
.tac-rotate-slider {
  flex: 1;
  -webkit-appearance: none;
  appearance: none;
  height: 6px;
  border-radius: 3px;
  outline: none;
  cursor: pointer;
  background: linear-gradient(
    to right,
    #409eff 0%,
    #409eff var(--tac-pct, 0%),
    #e4e7ed var(--tac-pct, 0%),
    #e4e7ed 100%
  );
  &::-webkit-slider-thumb {
    -webkit-appearance: none;
    width: 16px;
    height: 16px;
    border-radius: 50%;
    background: #fff;
    border: 2px solid #409eff;
    cursor: pointer;
  }
  &::-moz-range-thumb {
    width: 16px;
    height: 16px;
    border-radius: 50%;
    background: #fff;
    border: 2px solid #409eff;
    cursor: pointer;
  }
  &::-moz-range-track {
    height: 6px;
    border-radius: 3px;
    background: transparent;
  }
}
.tac-rotate-val { font-size: 12px; color: #606266; min-width: 36px; }
.tac-nudge {
  width: 22px;
  height: 22px;
  line-height: 1;
  padding: 0;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background: #fff;
  color: #606266;
  font-size: 14px;
  cursor: pointer;
  flex: 0 0 auto;
  &:hover { border-color: #409eff; color: #409eff; }
}
.tac-word-img {
  position: relative;
  width: 100%;
  height: 180px;
  background: #fff;
  border-radius: 4px;
  overflow: hidden;
  cursor: crosshair;
  img { width: 100%; height: 100%; display: block; -webkit-user-drag: none; }
}
.tac-word-tip {
  margin-bottom: 6px;
  font-size: 13px;
  color: #303133;
}
.tac-word-tip-img {
  display: block;
  height: 40px;
  width: auto;
  -webkit-user-drag: none;
}
.tac-word-tip-char {
  display: inline-block;
  margin: 0 2px;
  padding: 1px 6px;
  background: #409eff;
  color: #fff;
  border-radius: 3px;
  font-weight: bold;
}
.tac-word-click-dot {
  position: absolute;
  width: 22px;
  height: 22px;
  background: rgba(64, 158, 255, 0.7);
  border: 2px solid #fff;
  border-radius: 50%;
  color: #fff;
  font-size: 12px;
  line-height: 18px;
  text-align: center;
  transform: translate(-50%, -50%);
  pointer-events: none;
  font-weight: bold;
}
</style>
