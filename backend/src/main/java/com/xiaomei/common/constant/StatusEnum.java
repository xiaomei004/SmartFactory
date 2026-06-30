package com.xiaomei.common.constant;

/**
 * 状态枚举常量
 * 按照 dbFactory.sql 中的数值定义
 */
public class StatusEnum {

    /** 设备状态 */
    public static final int EQUIPMENT_ENABLED = 10;   // 启用
    public static final int EQUIPMENT_DISABLED = 20;  // 停用
    public static final int EQUIPMENT_FAULT = 30;     // 故障

    /** 订单状态 */
    public static final int ORDER_NOT_ACCEPTED = 10;  // 未接单
    public static final int ORDER_ACCEPTED = 20;      // 已接单
    public static final int ORDER_REJECTED = 30;      // 已拒绝
    public static final int ORDER_PRODUCING = 40;     // 生产中
    public static final int ORDER_COMPLETED = 50;     // 已完成

    /** 计划状态 */
    public static final int PLAN_NOT_STARTED = 10;    // 未启动
    public static final int PLAN_STARTED = 20;        // 已启动
    public static final int PLAN_COMPLETED = 30;      // 已完成

    /** 工单状态 */
    public static final int SCHEDULE_NOT_STARTED = 10; // 未开始
    public static final int SCHEDULE_PRODUCING = 20;   // 生产中
    public static final int SCHEDULE_COMPLETED = 30;   // 已完成

    /** 工厂状态 */
    public static final int FACTORY_NORMAL = 0;        // 正常
    public static final int FACTORY_CLOSED = 1;        // 关闭

    /** 用户状态 */
    public static final int USER_NORMAL = 0;           // 正常
    public static final int USER_LOCKED = 1;           // 锁定

    /** 产能单位 */
    public static final int UNIT_DAY = 10;             // 天
    public static final int UNIT_MONTH = 20;           // 月
    public static final int UNIT_YEAR = 30;            // 年
    public static final int UNIT_HOUR = 40;            // 小时

    /** 报工完成标识 */
    public static final int WORK_COMPLETE_YES = 0;     // 结束报工
    public static final int WORK_COMPLETE_NO = 1;      // 未结束报工

    /** 有效标识 */
    public static final int FLAG_VALID = 0;            // 有效
    public static final int FLAG_INVALID = 1;          // 无效
}
