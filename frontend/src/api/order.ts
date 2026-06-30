/**
 * 订单管理 API
 */
import request from './request'
import type { PageObject, Result, PageQuery } from '@/types/api'
import type { ProductOrder } from '@/types/entities'

/** 分页列表 */
export function getOrderList(params: PageQuery & { orderSeq?: string }) {
  return request.post<PageObject<ProductOrder>>('/order/list', params)
}

/** 详情 */
export function getOrderDetail(id: number) {
  return request.post<ProductOrder>('/order/detail', { id })
}

/** 新增下单 */
export function addOrder(data: {
  orderSeq: string
  productId: number
  productCount: number
  endDate: string
  factoryId: number
}) {
  return request.post<Result>('/order/add', data)
}

/** 接单 */
export function acceptOrder(id: number) {
  return request.post<Result>('/order/accept', { id })
}

/** 拒单 */
export function rejectOrder(id: number) {
  return request.post<Result>('/order/reject', { id })
}

/** 完成 */
export function completeOrder(id: number) {
  return request.post<Result>('/order/complete', { id })
}

/** 删除 */
export function deleteOrder(id: number) {
  return request.post<Result>('/order/delete', { id })
}
