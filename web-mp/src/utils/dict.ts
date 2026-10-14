/**
 * 字典工具 —— 对齐 web-ui 的 src/utils/dict.ts
 * 用法（Vue3 setup）：
 *   import { useDict } from '@/utils/dict'
 *   const { sys_user_sex } = useDict('sys_user_sex')
 *   <view v-for="d in sys_user_sex" :key="d.value">{{ d.label }}</view>
 */
import { http } from './request'
import type { AjaxResult } from '@/types/global'

export interface DictItem {
  value: string | number
  label: string
  // 一些业务字段
  [key: string]: unknown
}

const cache = new Map<string, Promise<DictItem[]>>()

export function loadDict(dictType: string): Promise<DictItem[]> {
  if (!cache.has(dictType)) {
    cache.set(
      dictType,
      http
        .get<AjaxResult<DictItem[]>>('/system/dict/data/type/' + dictType, undefined, { silent: true })
        .then((res) => (Array.isArray(res.data) ? res.data : []))
        .catch(() => [] as DictItem[])
    )
  }
  return cache.get(dictType)!
}

export function useDict(...types: string[]): Record<string, DictItem[]> {
  const result: Record<string, DictItem[]> = {}
  types.forEach((t) => {
    result[t] = []
    loadDict(t).then((arr) => {
      result[t].splice(0, result[t].length, ...arr)
    })
  })
  return result
}

/** 根据 value 找 label */
export function dictLabel(list: DictItem[] | undefined, value: string | number): string {
  if (!Array.isArray(list)) return ''
  const item = list.find((d) => String(d.value) === String(value))
  return item ? item.label : ''
}
