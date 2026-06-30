/**
 * 生产报工 API
 */
import request from './request'
import type { PageObject, Result, PageQuery } from '@/types/api'
import type { DailyWork } from '@/types/entities'

/** 分页列表 */
export function getDailyWorkList(params: PageQuery) {
  return request.post<PageObject<DailyWork>>('/dailyWork/list', params)
}

/** 详情 */
export function getDailyWorkDetail(id: number) {
  return request.post<DailyWork>('/dailyWork/detail', { id })
}

/** 报工 */
export function reportDailyWork(data: {
  scheduleId: number
  workingCount: number
  qualifiedCount: number
  unqualifiedCout: number
  factoryId: number
}) {
  return request.post<Result>('/dailyWork/report', data)
}

/** 结束报工 */
export function completeDailyWorkReport(id: number) {
  return request.post<Result>('/dailyWork/completeReport', { id })
}

/** 删除 */
export function deleteDailyWork(id: number) {
  return request.post<Result>('/dailyWork/delete', { id })
}
