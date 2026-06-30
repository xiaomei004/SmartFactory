-- ============================================
-- SmartFactory 数据库索引优化脚本
-- ============================================

-- 1. 订单表 - 按状态+工厂查询（最频繁）
ALTER TABLE t_product_order ADD INDEX idx_order_status_factory (order_status, factory_id, flag);

-- 2. 计划表 - 按订单ID关联查询
ALTER TABLE t_product_plan ADD INDEX idx_plan_order (order_id, factory_id, flag);

-- 3. 工单表 - 按计划ID关联查询
ALTER TABLE t_product_schedule ADD INDEX idx_schedule_plan (plan_id, factory_id, flag);
ALTER TABLE t_product_schedule ADD INDEX idx_schedule_status (schedule_status, factory_id, flag);

-- 4. 报工表 - 按工单ID查询
ALTER TABLE t_daily_work ADD INDEX idx_work_schedule (schedule_id, factory_id, flag);

-- 5. 订单跟踪表 - 按工单ID查询
ALTER TABLE t_order_track ADD INDEX idx_track_schedule (schedule_id, flag);

-- 6. 设备表 - 按状态查询
ALTER TABLE t_equipment ADD INDEX idx_equipment_status (equipment_status, factory_id, flag);

-- 7. 用户表 - 按用户名查询（登录）
ALTER TABLE t_user ADD INDEX idx_user_name (user_name, flag);

-- 8. 设备产品关联表 - 按设备/产品查询
ALTER TABLE t_equipment_product ADD INDEX idx_ep_equipment (equipment_id);
ALTER TABLE t_equipment_product ADD INDEX idx_ep_product (product_id);
