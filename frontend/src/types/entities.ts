/**
 * 业务实体类型定义
 * 字段名与后端 Java Entity 完全一致
 */

/** 产品 */
export interface Product {
  id: number
  flag: number
  createTime: string
  createUserid: number
  updateTime: string
  updateUserid: number
  productNum: string
  productName: string
  productImgUrl: string
  factoryId: number
}

/** 设备 */
export interface Equipment {
  id: number
  flag: number
  createTime: string
  createUserid: number
  updateTime: string
  updateUserid: number
  equipmentSeq: string
  equipmentName: string
  equipmentImgUrl: string
  equipmentStatus: number
  factoryId: number
}

/** 工厂 */
export interface Factory {
  id: number
  flag: number
  createTime: string
  createUserid: number
  updateTime: string
  updateUserid: number
  bak: string
  factoryName: string
  factoryImgUrl: string
  factoryAddr: string
  factoryUrl: string
  factoryWorker: number
  factoryStatus: number
}

/** 订单 */
export interface ProductOrder {
  id: number
  flag: number
  createTime: string
  createUserid: number
  updateTime: string
  updateUserid: number
  orderSeq: string
  orderSource: number
  productId: number
  productCount: number
  endDate: string
  orderStatus: number
  factoryId: number
  factoryYield: number
}

/** 生产计划 */
export interface ProductPlan {
  id: number
  flag: number
  createTime: string
  createUserid: number
  updateTime: string
  updateUserid: number
  planSeq: string
  orderId: number
  productId: number
  planCount: number
  deliveryDate: string
  planStartDate: string
  planEndDate: string
  planStatus: number
  factoryId: number
}

/** 生产调度（工单） */
export interface ProductSchedule {
  id: number
  flag: number
  createTime: string
  createUserid: number
  updateTime: string
  updateUserid: number
  scheduleSeq: string
  scheduleCount: number
  scheduleStatus: number
  planId: number
  productId: number
  equipmentId: number
  startDate: string
  endDate: string
  factoryId: number
}

/** 报工 */
export interface DailyWork {
  id: number
  flag: number
  createTime: string
  createUserid: number
  updateTime: string
  updateUserid: number
  scheduleId: number
  equipmentId: number
  equipmentSeq: string
  startTime: string
  endTime: string
  workingCount: number
  qualifiedCount: number
  unqualifiedCout: number
  completeFlag: number
  factoryId: number
  bak: string
}

/** 用户 */
export interface User {
  id: number
  flag: number
  createTime: string
  createUserid: number
  updateTime: string
  updateUserid: number
  userStatus: number
  userName: string
  userRealName: string
  userPasswd?: string
  userJobNum: string
  userPhoneNum: string
  userEmail: string
  roleId: number
  factoryId: number
}

/** 设备-产品关联（含产能） */
export interface EquipmentProduct {
  id: number
  equipmentId: number
  productId: number
  yield: number
  unit: number
  factoryId: number
}
