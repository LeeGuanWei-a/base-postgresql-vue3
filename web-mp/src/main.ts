import { createSSRApp, type Component, type App as VueApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import { setupPermissionDirective } from './directive/permission'

/**
 * uview-plus 3 显式注册。
 *
 * 关键点：uview-plus 的 install() 把 component.name 从 u-xxx 改成 up-xxx，
 * 所以 `app.use(uviewPlus)` 之后能解析我们手写的 <up-form>。
 * 但 uview-plus **自己的 .vue 内部模板** 仍写 <u-line> <u-icon> 等单横杠组件，
 * 那些子组件被 install 替换成 <up-line> 后又找不到 <u-line>。
 *
 * 解决：手动同时注册 u- 和 up- 两个名字。
 */
import upForm from 'uview-plus/components/u-form/u-form.vue'
import upFormItem from 'uview-plus/components/u-form-item/u-form-item.vue'
import upInput from 'uview-plus/components/u-input/u-input.vue'
import upButton from 'uview-plus/components/u-button/u-button.vue'
import upCell from 'uview-plus/components/u-cell/u-cell.vue'
import upCellGroup from 'uview-plus/components/u-cell-group/u-cell-group.vue'
import upIcon from 'uview-plus/components/u-icon/u-icon.vue'
import upLine from 'uview-plus/components/u-line/u-line.vue'
import upGrid from 'uview-plus/components/u-grid/u-grid.vue'
import upGridItem from 'uview-plus/components/u-grid-item/u-grid-item.vue'
import upTag from 'uview-plus/components/u-tag/u-tag.vue'
import upAvatar from 'uview-plus/components/u-avatar/u-avatar.vue'
import upSearch from 'uview-plus/components/u-search/u-search.vue'
import upEmpty from 'uview-plus/components/u-empty/u-empty.vue'
import upView from 'uview-plus/components/u-view/u-view.vue'
import upText from 'uview-plus/components/u-text/u-text.vue'
import upImage from 'uview-plus/components/u-image/u-image.vue'
import upLoadingIcon from 'uview-plus/components/u-loading-icon/u-loading-icon.vue'
import upSafeBottom from 'uview-plus/components/u-safe-bottom/u-safe-bottom.vue'
import upPopup from 'uview-plus/components/u-popup/u-popup.vue'
import upOverlay from 'uview-plus/components/u-overlay/u-overlay.vue'
import upTransition from 'uview-plus/components/u-transition/u-transition.vue'
import upBadge from 'uview-plus/components/u-badge/u-badge.vue'
import upSwitch from 'uview-plus/components/u-switch/u-switch.vue'
import upToast from 'uview-plus/components/u-toast/u-toast.vue'
import upLoadingPage from 'uview-plus/components/u-loading-page/u-loading-page.vue'
import upNoticeBar from 'uview-plus/components/u-notice-bar/u-notice-bar.vue'
import upDivider from 'uview-plus/components/u-divider/u-divider.vue'
import upLink from 'uview-plus/components/u-link/u-link.vue'
import upGap from 'uview-plus/components/u-gap/u-gap.vue'

/** 同时注册 up- 和 u- 名字，兼容内部模板 */
function dual(app: VueApp, component: Component): void {
  const rawName = (component as Component & { name?: string }).name || ''
  const upName = rawName.replace(/^u-/, 'up-') || 'up-' + ((component as Component & { __name?: string }).__name ?? 'unknown')
  const uName = upName.replace(/^up-/, 'u-')
  app.component(upName, component)
  if (upName !== uName) {
    app.component(uName, component)
  }
}

export function createApp() {
  const app = createSSRApp(App)
  const pinia = createPinia()
  app.use(pinia)

  const registered: string[] = []
  const components: Component[] = [
    upForm, upFormItem, upInput, upButton, upCell, upCellGroup, upIcon, upLine,
    upGrid, upGridItem, upTag, upAvatar, upSearch, upEmpty, upView, upText,
    upImage, upLoadingIcon, upSafeBottom, upPopup, upOverlay, upTransition,
    upBadge, upSwitch, upToast, upLoadingPage, upNoticeBar, upDivider, upLink, upGap
  ]
  components.forEach((c) => {
    dual(app, c)
    const n = (c as Component & { name?: string }).name
    if (n) registered.push(n)
  })

  if (typeof window !== 'undefined') {
    window.__UPLOAD_DEBUG = { registered, app }
    console.log('[uview-plus] 已注册组件:', registered)
  }

  setupPermissionDirective(app)

  return { app }
}
