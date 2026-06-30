/**
 * 生产调度（工单） API
 */
import request from './request'
import type { PageObject, Result, PageQuery } from '@/types/api'
import type { ProductSchedule } from '@/types/entities'

/** 分页列表 */
export function getScheduleList(params: PageQuery & { scheduleSeq?: string }) {
  return request.post<PageObject<ProductSchedule>>('/schedule/list', params)
}

/** 详情 */
export function getScheduleDetail(id: number) {
  return request.post<ProductSchedule>('/schedule/detail', { id })
}

/** 创建 */
export function addSchedule(data: {
  scheduleSeq: string
  planId: number
  productId: number
  scheduleCount: number
  startDate: string
  endDate: string
  factoryId: number
}) {
  return request.post<Result>('/schedule/add', data)
}

/** 分配设备 */
export function assignEquipment(id: number, equipmentId: number) {
  return request.post<Result>('/schedule/assignEquipment', { id, equipmentId })
}

/** 启动 */
export function startSchedule(id: number) {
  return request.post<Result>('/schedule/start', { id })
}

/** 完成 */
export function completeSchedule(id: number) {
  return request.post<Result>('/schedule/complete', { id })
}

/** 删除 */
export function deleteSchedule(id: number) {
  return request.post<Result>('/schedule/delete', { id })
}
