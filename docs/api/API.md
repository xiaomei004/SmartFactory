# SmartFactory API 文档 v2.0 (最终版)

> Base: `http://localhost:8080` | All: **POST** | Content-Type: `application/json`

## 通用规范

**分页请求：** `{"pageNum":1,"pageSize":10,"factoryId":1}`

**列表响应：** `{"status":"ok","dataList":[...],"pageInfo":{"pageNum":1,"pageSize":10,"total":N}}`

**操作响应：** `{"status":"ok","msg":"xxx"}` / `{"status":"error","msg":"xxx"}`

**登录响应：** `{"status":"ok","msg":"登录成功","user":{...}}`（密码字段不返回）

---

## 1. Product 产品管理

| 接口 | Body | 说明 |
|------|------|------|
| `POST /product/list` | `{"pageNum":1,"pageSize":10,"factoryId":1}` | 分页列表 |
| `POST /product/detail` | `{"id":1}` | 详情 |
| `POST /product/add` | `{"productNum":"CP001","productName":"产品名","factoryId":1}` | 新增，产品名同工厂唯一 |
| `POST /product/edit` | `{"id":1,"productName":"新名称"}` | 修改，只更新非空字段 |
| `POST /product/delete` | `{"id":1}` | 删除，有关联未完成订单时拒绝 |

## 2. Equipment 设备管理

| 接口 | Body | 说明 |
|------|------|------|
| `POST /equipment/list` | `{"pageNum":1,"pageSize":10,"factoryId":1}` | 分页列表 |
| `POST /equipment/detail` | `{"id":1}` | 详情 |
| `POST /equipment/add` | `{"equipmentSeq":"EQ001","equipmentName":"设备","equipmentStatus":10,"factoryId":1}` | 新增，序列号同工厂唯一 |
| `POST /equipment/edit` | `{"id":1,"equipmentName":"新名称","equipmentStatus":20}` | 修改 |
| `POST /equipment/delete` | `{"id":1}` | 删除，有活跃工单时拒绝 |
| `POST /equipment/products` | `{"id":1}` | 查询关联产品产能列表 |
| `POST /equipment/setProducts` | `{"equipmentId":1,"productIds":[1,2,3]}` | 设置设备-产品关联 |

## 3. Order 订单管理

| 接口 | Body | 说明 |
|------|------|------|
| `POST /order/list` | `{"pageNum":1,"pageSize":10,"factoryId":1}` | 分页列表 |
| `POST /order/detail` | `{"id":1}` | 详情 |
| `POST /order/add` | `{"orderSeq":"ORD001","productId":1,"productCount":500,"endDate":"2026-07-30","factoryId":1}` | 下单，状态=10 |
| `POST /order/accept` | `{"id":1}` | 接单(10→20)，校验产能 |
| `POST /order/reject` | `{"id":1}` | 拒单(10→30) |
| `POST /order/complete` | `{"id":1}` | 完成(40→50)，校验合格数量，级联完成计划和工单 |
| `POST /order/delete` | `{"id":1}` | 删除，生产中订单不可删 |

**状态流转：** 10未接单 → 20已接单 → 40生产中 → 50已完成 | 10→30已拒绝

## 4. Plan 生产计划

| 接口 | Body | 说明 |
|------|------|------|
| `POST /plan/list` | `{"pageNum":1,"pageSize":10,"factoryId":1}` | 分页列表 |
| `POST /plan/detail` | `{"id":1}` | 详情 |
| `POST /plan/add` | `{"planSeq":"P001","orderId":1,"productId":1,"planCount":500,"deliveryDate":"2026-07-30","planStartDate":"2026-07-01","planEndDate":"2026-07-20","factoryId":1}` | 创建，自动推进订单→40 |
| `POST /plan/start` | `{"id":1}` | 启动(10→20) |
| `POST /plan/complete` | `{"id":1}` | 完成(20→30) |
| `POST /plan/delete` | `{"id":1}` | 删除，已启动计划不可删 |

## 5. Schedule 生产调度（工单）

| 接口 | Body | 说明 |
|------|------|------|
| `POST /schedule/list` | `{"pageNum":1,"pageSize":10,"factoryId":1}` | 分页列表 |
| `POST /schedule/detail` | `{"id":1}` | 详情 |
| `POST /schedule/add` | `{"scheduleSeq":"SCH001","planId":1,"productId":1,"scheduleCount":200,"startDate":"2026-07-01","endDate":"2026-07-10","factoryId":1}` | 创建，自动启动计划 |
| `POST /schedule/assignEquipment` | `{"id":1,"equipmentId":1}` | 分配设备，校验设备可生产该产品 |
| `POST /schedule/start` | `{"id":1}` | 启动(10→20)，须先分配设备 |
| `POST /schedule/complete` | `{"id":1}` | 完成(20→30) |
| `POST /schedule/delete` | `{"id":1}` | 删除，仅未开始可删 |

## 6. DailyWork 生产报工

| 接口 | Body | 说明 |
|------|------|------|
| `POST /dailyWork/list` | `{"pageNum":1,"pageSize":10,"factoryId":1}` | 分页列表 |
| `POST /dailyWork/detail` | `{"id":1}` | 详情 |
| `POST /dailyWork/report` | `{"scheduleId":1,"workingCount":100,"qualifiedCount":95,"unqualifiedCout":5,"factoryId":1}` | 报工，仅生产中工单，自动写订单跟踪 |
| `POST /dailyWork/completeReport` | `{"id":1}` | 结束报工，completeFlag: 0=结束 |
| `POST /dailyWork/delete` | `{"id":1}` | 删除 |

## 7. User 用户

| 接口 | Body | 说明 |
|------|------|------|
| `POST /user/login` | `{"userName":"admin","userPasswd":"123456"}` | 登录，MD5加密，自动迁移旧明文密码 |
| `POST /user/register` | `{"userName":"new","userPasswd":"123456","userRealName":"张三","roleId":1,"factoryId":1}` | 注册，密码MD5加密，用户名唯一 |
| `POST /user/logout` | `{}` | 退出，清除Session |

## 8. Factory 工厂

| 接口 | Body | 说明 |
|------|------|------|
| `POST /factory/register` | `{"factoryName":"工厂名","factoryAddr":"地址","factoryWorker":100}` | 注册 |
| `POST /factory/detail` | `{"id":1}` | 详情 |

## 9. Dashboard 仪表盘

| 接口 | Body | 说明 |
|------|------|------|
| `POST /dashboard/statistics` | `{"factoryId":1}` | 设备统计(total/enabled/disabled/fault/producing/standby/开机率/故障率/运行率/综合效率) + 订单统计(各状态数量) + 年度月度汇总 |

## 状态枚举

| 枚举 | 值 |
|------|------|
| 设备 | 10=启用, 20=停用, 30=故障 |
| 订单 | 10=未接单, 20=已接单, 30=已拒绝, 40=生产中, 50=已完成 |
| 计划 | 10=未启动, 20=已启动, 30=已完成 |
| 工单 | 10=未开始, 20=生产中, 30=已完成 |
| 工厂 | 0=正常, 1=关闭 |
| 用户 | 0=正常, 1=锁定 |

---

**共 44 个接口，全部 POST JSON。**
