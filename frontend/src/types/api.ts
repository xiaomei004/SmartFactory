/**
 * API 通用响应类型
 */

/** 分页信息 */
export interface PageInfo {
  pageNum: number
  pageSize: number
  total: number
  msg?: string
}

/** 分页列表响应 */
export interface PageObject<T> {
  status: 'ok' | 'error'
  dataList: T[]
  pageInfo: PageInfo
}

/** 操作结果响应 */
export interface Result {
  status: 'ok' | 'error'
  msg: string
}

/** 登录响应 */
export interface LoginResult {
  status: 'ok' | 'error'
  msg: string
  user: {
    id: number
    userName: string
    userRealName: string
    userJobNum?: string
    userPhoneNum?: string
    userEmail?: string
    roleId: number
    factoryId: number
    userStatus: number
  }
}

/** 设备统计 */
export interface EquipmentStats {
  total: number
  enabled: number
  disabled: number
  fault: number
  producing: number
  standby: number
  startupRate: number
  faultRate: number
  runningRate: number
  efficiency: number
}

/** 订单状态统计 */
export interface OrderStats {
  notAccepted: number
  accepted: number
  rejected: number
  producing: number
  completed: number
  total: number
}

/** 年度月度汇总条目 */
export interface YearlySummaryItem {
  month: string
  orders: number
  plans: number
  schedules: number
}

/** 仪表盘响应 */
export interface DashboardData {
  status: 'ok'
  equipment: EquipmentStats
  order: OrderStats
  yearlySummary: YearlySummaryItem[]
}

/** 通用分页查询参数 */
export interface PageQuery {
  pageNum: number
  pageSize: number
  factoryId?: number
}

/** 设备关联产品产能 */
export interface EquipmentProductCapacity {
  id: number
  equipmentId: number
  productId: number
  yield: number
  unit: number
  factoryId: number
}
