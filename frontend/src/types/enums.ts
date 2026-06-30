/**
 * 状态枚举定义
 * 值与后端 StatusEnum 常量严格一致
 */

import type { Component } from 'vue'

/** 状态标签渲染信息 */
export interface StatusDef {
  label: string
  type: '' | 'success' | 'warning' | 'danger' | 'info'
}

// ==================== 设备状态 ====================
export const EQUIPMENT_STATUS: Record<number, StatusDef> = {
  10: { label: '启用', type: 'success' },
  20: { label: '停用', type: 'warning' },
  30: { label: '故障', type: 'danger' },
}

// ==================== 订单状态 ====================
export const ORDER_STATUS: Record<number, StatusDef> = {
  10: { label: '未接单', type: 'info' },
  20: { label: '已接单', type: 'success' },
  30: { label: '已拒绝', type: 'danger' },
  40: { label: '生产中', type: 'warning' },
  50: { label: '已完成', type: '' },
}

// ==================== 计划状态 ====================
export const PLAN_STATUS: Record<number, StatusDef> = {
  10: { label: '未启动', type: 'info' },
  20: { label: '已启动', type: 'warning' },
  30: { label: '已完成', type: 'success' },
}

// ==================== 工单状态 ====================
export const SCHEDULE_STATUS: Record<number, StatusDef> = {
  10: { label: '未开始', type: 'info' },
  20: { label: '生产中', type: 'warning' },
  30: { label: '已完成', type: 'success' },
}

// ==================== 工厂状态 ====================
export const FACTORY_STATUS: Record<number, StatusDef> = {
  0: { label: '正常', type: 'success' },
  1: { label: '关闭', type: 'danger' },
}

// ==================== 用户状态 ====================
export const USER_STATUS: Record<number, StatusDef> = {
  0: { label: '正常', type: 'success' },
  1: { label: '锁定', type: 'danger' },
}

// ==================== 产能单位 ====================
export const YIELD_UNIT: Record<number, string> = {
  10: '件/天',
  20: '件/月',
  30: '件/年',
  40: '件/小时',
}

// ==================== 报工完成标志 ====================
export const WORK_COMPLETE_FLAG: Record<number, StatusDef> = {
  0: { label: '已结束', type: '' },
  1: { label: '进行中', type: 'success' },
}
