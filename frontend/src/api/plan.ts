/**
 * 生产计划 API
 */
import request from './request'
import type { PageObject, Result, PageQuery } from '@/types/api'
import type { ProductPlan } from '@/types/entities'

/** 分页列表 */
export function getPlanList(params: PageQuery & { planSeq?: string }) {
  return request.post<PageObject<ProductPlan>>('/plan/list', params)
}

/** 详情 */
export function getPlanDetail(id: number) {
  return request.post<ProductPlan>('/plan/detail', { id })
}

/** 创建 */
export function addPlan(data: {
  planSeq: string
  orderId: number
  productId: number
  planCount: number
  deliveryDate: string
  planStartDate: string
  planEndDate: string
  factoryId: number
}) {
  return request.post<Result>('/plan/add', data)
}

/** 启动 */
export function startPlan(id: number) {
  return request.post<Result>('/plan/start', { id })
}

/** 完成 */
export function completePlan(id: number) {
  return request.post<Result>('/plan/complete', { id })
}

/** 删除 */
export function deletePlan(id: number) {
  return request.post<Result>('/plan/delete', { id })
}
