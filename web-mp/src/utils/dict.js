/**
 * 字典工具 —— 对齐 web-ui 的 src/utils/dict.js
 * 用法（Vue3 setup）：
 *   import { useDict } from '@/utils/dict'
 *   const { sys_user_sex } = useDict('sys_user_sex')
 *   <view v-for="d in sys_user_sex" :key="d.value">{{ d.label }}</view>
 */
import { http } from './request'

const cache = new Map()

export function loadDict(dictType) {
  if (!cache.has(dictType)) {
    cache.set(
      dictType,
      http
        .get('/system/dict/data/type/' + dictType, undefined, { silent: true })
        .then((res) => res.data || [])
        // .then((res) => res.data || [])  // 旧：res.data 不对
        .catch(() => [])
    )
  }
  return cache.get(dictType)
}

export function useDict(...types) {
  const result = {}
  types.forEach((t) => {
    result[t] = []
    loadDict(t).then((arr) => {
      result[t].splice(0, result[t].length, ...arr)
    })
  })
  return result
}

/** 根据 value 找 label */
export function dictLabel(list, value) {
  if (!Array.isArray(list)) return ''
  const item = list.find((d) => String(d.value) === String(value))
  return item ? item.label : ''
}
