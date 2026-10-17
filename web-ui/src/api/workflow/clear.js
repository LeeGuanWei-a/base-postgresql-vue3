// 流程数据清盘（危险操作）接口
import request from '@/utils/request'

/**
 * 预览将清空的流程数据量（只统计不删除）
 */
export function previewClear() {
  return request({
    url: '/workflow/process/clear/preview',
    method: 'get'
  })
}

/**
 * 清空全部流程数据（不可恢复）
 * @param {string} confirmText 确认短语，必须为 "清空流程"
 */
export function clearAllProcessData(confirmText) {
  return request({
    url: '/workflow/process/clear/all',
    method: 'post',
    data: { confirmText }
  })
}
