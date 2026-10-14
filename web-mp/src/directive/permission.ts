/**
 * 权限指令 —— 对齐 web-ui 的 directive/permission/index.ts
 *
 *  - v-hasPermi="['system:user:add']"   单值或数组
 *  - v-hasRole="['admin']"             单值或数组
 *  - 修饰符 .any                        满足任一即通过（默认全部满足）
 *
 * 注意：uniapp 3 的 vue runtime 不提供 default export，所以**不能** `import Vue from 'vue'`。
 * Vue 实例由 uniapp runtime 注入到 globalThis，模块顶层用 globalThis.Vue 拿到。
 *
 * 不过更稳的做法：uniapp 3 在 vite 编译阶段**不支持自定义指令**（编译模板时不解析），
 * 所以页面里直接用 v-if + 函数（如 hasPermi(['system:user:list'])）替代。
 * 本文件保留指令注册逻辑以备未来支持时使用。
 */
import type { App, Directive, DirectiveBinding } from 'vue'
import { useUserStore } from '@/store/modules/user'

type PermOrRole = string | string[] | undefined

function checkArray(value: PermOrRole): string[] {
  if (!value) return []
  if (Array.isArray(value)) return value.map(String)
  return String(value).split(',').map((s) => s.trim()).filter(Boolean)
}

function checkPermi(el: HTMLElement, binding: DirectiveBinding<PermOrRole>): void {
  const userStore = useUserStore()
  const required = checkArray(binding.value)
  if (!required.length) return
  const any = !!binding.modifiers.any
  const perms = userStore.permissions || []
  if (perms.includes('*:*:*')) return
  const ok = any
    ? required.some((p) => perms.includes(p))
    : required.every((p) => perms.includes(p))
  if (!ok && el.parentNode) el.parentNode.removeChild(el)
}

function checkRole(el: HTMLElement, binding: DirectiveBinding<PermOrRole>): void {
  const userStore = useUserStore()
  const required = checkArray(binding.value)
  if (!required.length) return
  const any = !!binding.modifiers.any
  const roles = userStore.roles || []
  const ok = any
    ? required.some((r) => roles.includes(r))
    : required.every((r) => roles.includes(r))
  if (!ok && el.parentNode) el.parentNode.removeChild(el)
}

/**
 * 业务里直接用的工具函数，替代 v-hasPermi 指令。
 * 用法：v-if="hasPermi(['system:user:list'])"
 */
export function hasPermi(perms: PermOrRole): boolean {
  const userStore = useUserStore()
  const owned = userStore.permissions || []
  if (owned.includes('*:*:*')) return true
  if (!Array.isArray(perms)) return false
  return perms.every((p) => owned.includes(p))
}

export function hasRole(roles: PermOrRole): boolean {
  const userStore = useUserStore()
  const owned = userStore.roles || []
  if (!Array.isArray(roles)) return false
  return roles.every((r) => owned.includes(r))
}

/**
 * 兼容 HBuilderX 旧版：保留 directive 形式（当前 HBuilderX 4.56 之前不支持 vite 编译，
 * 跑老 compiler 时需要走 Vue.directive 注册）。如果 Vue 全局存在就注册。
 * 小程序 / Vite 模式下不会执行到这里，因为 H5/小程序没有 DOM removeChild。
 */
export function setupPermissionDirective(app: App): void {
  const hasPermiDirective: Directive<HTMLElement, PermOrRole> = {
    mounted: checkPermi,
    updated: checkPermi
  }
  const hasRoleDirective: Directive<HTMLElement, PermOrRole> = {
    mounted: checkRole,
    updated: checkRole
  }
  try {
    app.directive('hasPermi', hasPermiDirective)
    app.directive('hasRole', hasRoleDirective)
  } catch (e) {
    // 编译时调用 app.directive 可能失败，忽略
  }
}
