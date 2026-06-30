# SmartFactory API 文档

> Base URL: `http://localhost:8080`  
> All: POST, Content-Type: application/json

## 通用

**分页返回格式：**
```json
{"status":"ok","dataList":[...],"pageInfo":{"pageNum":1,"pageSize":10,"total":100}}
```

**操作返回格式：**
```json
{"status":"ok","msg":"操作成功"}
{"status":"error","msg":"错误描述"}
```

---

## Product（产品）

| 接口 | Body |
|------|------|
| `POST /product/list` | `{"pageNum":1,"pageSize":10,"factoryId":1}` |
| `POST /product/detail` | `{"id":1}` |
| `POST /product/add` | `{"productNum":"CP001","productName":"产品名","factoryId":1}` |
| `POST /product/edit` | `{"id":1,"productNum":"CP001","productName":"新名称"}` |
| `POST /product/delete` | `{"id":1}` |

## Equipment（设备）

| 接口 | Body |
|------|------|
| `POST /equipment/list` | `{"pageNum":1,"pageSize":10,"factoryId":1}` |
| `POST /equipment/detail` | `{"id":1}` |
| `POST /equipment/add` | `{"equipmentSeq":"RQQ001","equipmentName":"设备名","equipmentStatus":10,"factoryId":1}` |
| `POST /equipment/edit` | `{"id":1,"equipmentName":"新名称","equipmentStatus":20}` |
| `POST /equipment/delete` | `{"id":1}` |
| `POST /equipment/products` | `{"id":1}` |
| `POST /equipment/setProducts` | `{"equipmentId":1,"productIds":[1,2,3]}` |

## Order（订单）

| 接口 | Body |
|------|------|
| `POST /order/list` | `{"pageNum":1,"pageSize":10,"factoryId":1}` |
| `POST /order/detail` | `{"id":1}` |
| `POST /order/add` | `{"orderSeq":"XH001","productId":1,"productCount":500,"endDate":"2026-07-30","factoryId":1}` |
| `POST /order/accept` | `{"id":1}` |
| `POST /order/reject` | `{"id":1}` |
| `POST /order/complete` | `{"id":1}` |
| `POST /order/delete` | `{"id":1}` |

## Plan（生产计划）

| 接口 | Body |
|------|------|
| `POST /plan/list` | `{"pageNum":1,"pageSize":10,"factoryId":1}` |
| `POST /plan/detail` | `{"id":1}` |
| `POST /plan/add` | `{"planSeq":"JH001","orderId":1,"productId":1,"planCount":500,"deliveryDate":"2026-07-30","planStartDate":"2026-07-01","planEndDate":"2026-07-20","factoryId":1}` |
| `POST /plan/start` | `{"id":1}` |
| `POST /plan/complete` | `{"id":1}` |
| `POST /plan/delete` | `{"id":1}` |

## Schedule（生产调度/工单）

| 接口 | Body |
|------|------|
| `POST /schedule/list` | `{"pageNum":1,"pageSize":10,"factoryId":1}` |
| `POST /schedule/detail` | `{"id":1}` |
| `POST /schedule/add` | `{"scheduleSeq":"GD001","planId":1,"productId":1,"scheduleCount":200,"startDate":"2026-07-01","endDate":"2026-07-10","factoryId":1}` |
| `POST /schedule/assignEquipment` | `{"id":1,"equipmentId":1}` |
| `POST /schedule/start` | `{"id":1}` |
| `POST /schedule/complete` | `{"id":1}` |
| `POST /schedule/delete` | `{"id":1}` |

## DailyWork（生产报工）

| 接口 | Body |
|------|------|
| `POST /dailyWork/list` | `{"pageNum":1,"pageSize":10,"factoryId":1}` |
| `POST /dailyWork/detail` | `{"id":1}` |
| `POST /dailyWork/report` | `{"scheduleId":1,"workingCount":100,"qualifiedCount":95,"unqualifiedCout":5,"factoryId":1}` |
| `POST /dailyWork/completeReport` | `{"id":1}` |
| `POST /dailyWork/delete` | `{"id":1}` |

## User / Factory / Dashboard

| 接口 | Body |
|------|------|
| `POST /user/login` | `{"userName":"admin","userPasswd":"123456"}` |
| `POST /user/register` | `{"userName":"new","userPasswd":"123456","factoryId":1}` |
| `POST /factory/register` | `{"factoryName":"工厂名","factoryAddr":"地址"}` |
| `POST /factory/detail` | `{"id":1}` |
| `POST /dashboard/statistics` | `{"factoryId":1}` |

## 状态枚举

| 枚举 | 值 |
|------|------|
| 设备状态 | 10=启用, 20=停用, 30=故障 |
| 订单状态 | 10=未接单, 20=已接单, 30=已拒绝, 40=生产中, 50=已完成 |
| 计划状态 | 10=未启动, 20=已启动, 30=已完成 |
| 工单状态 | 10=未开始, 20=生产中, 30=已完成 |

## 业务流程

```
下单(10) → 接单(20) → 创建计划(订单→40) → 创建工单(计划→20) → 分配设备 → 启动工单(20) → 报工 → 完成报工
         ↘ 拒单(30)
```
