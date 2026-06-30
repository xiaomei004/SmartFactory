/**
 * 设备管理 API
 */
import request from './request'
import type { PageObject, Result, PageQuery, EquipmentProductCapacity } from '@/types/api'
import type { Equipment } from '@/types/entities'

/** 分页列表 */
export function getEquipmentList(params: PageQuery & { equipmentName?: string; productName?: string }) {
  return request.post<PageObject<Equipment>>('/equipment/list', params)
}

/** 详情 */
export function getEquipmentDetail(id: number) {
  return request.post<Equipment>('/equipment/detail', { id })
}

/** 新增 */
export function addEquipment(data: {
  equipmentSeq: string
  equipmentName: string
  equipmentImgUrl?: string
  equipmentStatus: number
  factoryId: number
}) {
  return request.post<Result>('/equipment/add', data)
}

/** 修改 */
export function editEquipment(data: {
  id: number
  equipmentSeq?: string
  equipmentName?: string
  equipmentImgUrl?: string
  equipmentStatus?: number
}) {
  return request.post<Result>('/equipment/edit', data)
}

/** 删除 */
export function deleteEquipment(id: number) {
  return request.post<Result>('/equipment/delete', { id })
}

/** 查询关联产品产能 */
export function getEquipmentProducts(equipmentId: number) {
  return request.post<EquipmentProductCapacity[]>('/equipment/products', { id: equipmentId })
}

/** 设置设备-产品关联 */
export function setEquipmentProducts(equipmentId: number, productIds: number[]) {
  return request.post<Result>('/equipment/setProducts', { equipmentId, productIds })
}
