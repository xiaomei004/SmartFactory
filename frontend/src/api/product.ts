/**
 * 产品管理 API
 */
import request from './request'
import type { PageObject, Result, PageQuery } from '@/types/api'
import type { Product } from '@/types/entities'

/** 分页列表 */
export function getProductList(params: PageQuery & { productName?: string }) {
  return request.post<PageObject<Product>>('/product/list', params)
}

/** 详情 */
export function getProductDetail(id: number) {
  return request.post<Product>('/product/detail', { id })
}

/** 新增 */
export function addProduct(data: {
  productNum: string
  productName: string
  productImgUrl?: string
  factoryId: number
}) {
  return request.post<Result>('/product/add', data)
}

/** 修改 */
export function editProduct(data: {
  id: number
  productNum?: string
  productName?: string
  productImgUrl?: string
}) {
  return request.post<Result>('/product/edit', data)
}

/** 删除 */
export function deleteProduct(id: number) {
  return request.post<Result>('/product/delete', { id })
}
