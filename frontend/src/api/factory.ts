/**
 * 工厂 API
 */
import request from './request'
import type { Result } from '@/types/api'
import type { Factory } from '@/types/entities'

/** 注册 */
export function registerFactory(data: {
  factoryName: string
  factoryAddr: string
  factoryWorker: number
}) {
  return request.post<Result>('/factory/register', data)
}

/** 详情 */
export function getFactoryDetail(id: number) {
  return request.post<Factory>('/factory/detail', { id })
}
