/**
 * 仪表盘 API
 */
import request from './request'
import type { DashboardData } from '@/types/api'

/** 首页统计 */
export function getDashboardStatistics(factoryId: number) {
  return request.post<DashboardData>('/dashboard/statistics', { factoryId })
}
