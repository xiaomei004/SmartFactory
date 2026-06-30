# SmartFactory Vue 3 前端开发 TODO

> **用途**: 上下文恢复 + 进度追踪  
> **项目路径**: `smartfactory-web/`  
> **独立前端**: Vite dev server :3000 → proxy → Spring Boot :8080  
> **最后更新**: 2026-06-30

---

## 快速恢复（给 AI 看）

```
当前阶段:  Phase 1 — 脚手架搭建
项目目录:  smartfactory-web/ 已由 Vite 创建，node_modules 待安装
后端状态:  44 API 全部就绪，CORS 已开启
下一步:    安装依赖 → 配置 vite proxy → 创建目录结构
```

---

## 技术基线

| 项 | 值 |
|---|-----|
| 前端框架 | Vue 3 (Composition API + `<script setup>`) |
| 语言 | TypeScript |
| 构建 | Vite |
| UI | Element Plus |
| HTTP | Axios |
| 路由 | Vue Router 4 |
| 状态 | Pinia |
| 图表 | ECharts + vue-echarts |
| 开发端口 | 3000 |
| 后端代理 | `localhost:3000/product|equipment|...` → `localhost:8080` |

---

## 项目结构约定

```
smartfactory-web/src/
├── api/             # Axios实例 + 9个模块API
├── types/           # TS接口 + 枚举
├── stores/          # auth.ts + app.ts
├── router/          # 路由 + 导航守卫
├── composables/     # usePagination, useFormModal
├── components/
│   ├── layout/      # AppLayout, SidebarNav, HeaderBar
│   ├── common/      # PaginationTable, StatusTag, FormModal, PageHeader
│   └── charts/      # EquipmentStatsPanel, OrderStatusChart, YearlySummaryChart
├── views/           # 10个页面视图
└── utils/           # format, validate
```

---

## Phase 1 — 脚手架搭建 [ ]

| 编号 | 任务 | 状态 |
|------|------|------|
| 1.1 | `npm install` 安装所有依赖 | `[ ]` |
| 1.2 | 配置 vite.config.ts（proxy、path alias） | `[ ]` |
| 1.3 | 创建完整目录结构 | `[ ]` |
| 1.4 | 配置 main.ts（Element Plus + Router + Pinia） | `[ ]` |
| 1.5 | `npm run dev` 启动验证 | `[ ]` |

## Phase 2 — 类型系统 + API 层 [ ]

| 编号 | 任务 | 状态 |
|------|------|------|
| 2.1 | `types/api.ts` — PageObject、Result、LoginResult | `[ ]` |
| 2.2 | `types/entities.ts` — 10 个实体接口 | `[ ]` |
| 2.3 | `types/enums.ts` — 状态常量 + label/color 映射 | `[ ]` |
| 2.4 | `api/request.ts` — Axios 实例 + 拦截器 | `[ ]` |
| 2.5 | `api/*.ts` — 9 个模块 API 文件 | `[ ]` |

## Phase 3 — 认证 + 路由 + 布局 [ ]

| 编号 | 任务 | 状态 |
|------|------|------|
| 3.1 | `stores/auth.ts` — 登录/登出/恢复会话 | `[ ]` |
| 3.2 | `stores/app.ts` — sidebar/theme | `[ ]` |
| 3.3 | `router/index.ts` — 路由 + 导航守卫 | `[ ]` |
| 3.4 | `components/layout/` — AppLayout, SidebarNav, HeaderBar | `[ ]` |
| 3.5 | `App.vue` — 挂载 `<router-view>` | `[ ]` |

## Phase 4 — 公共组件 + 登录页 [ ]

| 编号 | 任务 | 状态 |
|------|------|------|
| 4.1 | `components/common/PaginationTable.vue` | `[ ]` |
| 4.2 | `components/common/StatusTag.vue` | `[ ]` |
| 4.3 | `components/common/FormModal.vue` + `PageHeader.vue` | `[ ]` |
| 4.4 | `views/auth/LoginView.vue` | `[ ]` |
| 4.5 | `views/auth/RegisterView.vue` | `[ ]` |
| 4.6 | 测试登录流程（login → store → headers → 路由跳转） | `[ ]` |

## Phase 5 — 仪表盘 [ ]

| 编号 | 任务 | 状态 |
|------|------|------|
| 5.1 | `components/charts/EquipmentStatsPanel.vue` | `[ ]` |
| 5.2 | `components/charts/OrderStatusChart.vue` | `[ ]` |
| 5.3 | `components/charts/YearlySummaryChart.vue` | `[ ]` |
| 5.4 | `views/dashboard/DashboardView.vue` | `[ ]` |

## Phase 6 — 业务页面（每个模块一个 ListView） [ ]

| 编号 | 任务 | 状态 |
|------|------|------|
| 6.1 | `views/product/ProductListView.vue`（CRUD 表格 + 弹窗表单） | `[ ]` |
| 6.2 | `views/equipment/EquipmentListView.vue`（+ 产品产能关联） | `[ ]` |
| 6.3 | `views/order/OrderListView.vue`（+ 接单/拒单/完成按钮） | `[ ]` |
| 6.4 | `views/plan/PlanListView.vue`（+ 启动/完成） | `[ ]` |
| 6.5 | `views/schedule/ScheduleListView.vue`（+ 分配设备/启动/完成） | `[ ]` |
| 6.6 | `views/dailyWork/DailyWorkListView.vue`（+ 报工/完成报工） | `[ ]` |

## Phase 7 — 收尾 [ ]

| 编号 | 任务 | 状态 |
|------|------|------|
| 7.1 | `views/error/NotFoundView.vue` | `[ ]` |
| 7.2 | 全流程回归测试（登录→仪表盘→CRUD→状态流转） | `[ ]` |
| 7.3 | `npm run build` 生产构建 | `[ ]` |
| 7.4 | 部署到 Spring Boot static/ 全栈联调 | `[ ]` |

---

## 接口规范速查

```
所有接口 POST + JSON
分页请求: {pageNum, pageSize, factoryId}
分页响应: {status, dataList, pageInfo: {pageNum, pageSize, total}}
操作响应: {status: "ok"/"error", msg}
登录响应: {status, msg, user: {...}}
仪表盘:   {status, equipment: {...}, order: {...}, yearlySummary: [...]}
```

## 状态枚举

| 模块 | 值 |
|------|-----|
| 设备 | 10=启用, 20=停用, 30=故障 |
| 订单 | 10=未接单, 20=已接单, 30=已拒绝, 40=生产中, 50=已完成 |
| 计划 | 10=未启动, 20=已启动, 30=已完成 |
| 工单 | 10=未开始, 20=生产中, 30=已完成 |
| 报工 | completeFlag: 0=结束, 1=未结束 |

## Auth 机制

```
登录后 authStore 存储 userId + factoryId
Axios 拦截器给每个请求加 Header:
  X-User-Id: userId
  X-Factory-Id: factoryId
```

## 注意事项

## UI 主题速查

| 项 | 值 |
|---|-----|
| 主题方案 | 工业灰蓝（方案C） |
| 主色 | `#2c5f8a` 钢蓝 |
| 侧边栏 | 深海军蓝 `#1a2332` |
| 内容区底色 | `#f0f2f5` 浅灰 |
| 卡片 | 白色，圆角 8px，轻微阴影 |
| 表格行高 | 48px 紧凑型 |
| 暗色切换 | 一期不做，后续可 CSS 变量快速加 |

## 注意事项

⚠️ `unqualifiedCout` 是后端拼写错误，前端接口保留原名，UI 标签写"不合格数量"
⚠️ 订单 complete 接口会级联完成计划和工单，前端只需调一次
⚠️ 设备 setProducts 是整体替换，需先加载已有关联再编辑
