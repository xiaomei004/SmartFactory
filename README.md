# SmartFactory 东软智能制造云平台

Spring Boot 4.0.7 + Vue 3 + MyBatis + MySQL 智能制造管理系统。

## 项目结构

```
SamrtFactory/
├── backend/           ← Spring Boot 后端（Java 21 + MyBatis）
├── frontend/          ← Vue 3 前端（TypeScript + Element Plus）
├── docs/
│   ├── api/           ← API 接口文档
│   ├── dev/           ← 开发 TODO + 设计说明
│   ├── prd/           ← 产品需求文档
│   ├── sql/           ← 数据库脚本
│   ├── reference/     ← 东软参考文档
│   └── learning/      ← 学习经验
├── scripts/           ← 测试脚本
└── README.md
```

## 快速启动

### 1. 数据库

```bash
mysql -u root -p123456 < docs/sql/dbFactory.sql
```

### 2. 后端

```bash
cd backend
./mvnw spring-boot:run
# → http://localhost:8080
```

### 3. 前端

```bash
cd frontend
npm install
npm run dev
# → http://localhost:3000（代理到 :8080）
```

### 4. 测试

```powershell
# PowerShell
.\scripts\test_api.ps1

# CMD
.\scripts\test_api.cmd
```

## 技术栈

| 层 | 技术 |
|---|------|
| 后端框架 | Spring Boot 4.0.7 |
| Java 版本 | 21 |
| ORM | MyBatis（注解） |
| 数据库 | MySQL 5.6+ |
| 前端框架 | Vue 3（Composition API） |
| 语言 | TypeScript |
| 构建 | Vite |
| UI 组件 | Element Plus |
| HTTP | Axios |
| 路由 | Vue Router 4 |
| 状态管理 | Pinia |
| 图表 | ECharts + vue-echarts |
| 主题 | 工业灰蓝（钢蓝 #2c5f8a） |

## 模块

| 模块 | 接口 | 说明 |
|------|------|------|
| Product | 5 | 产品 CRUD |
| Equipment | 7 | 设备 + 产能关联 |
| Order | 7 | 订单 + 状态流转 |
| Plan | 6 | 生产计划 |
| Schedule | 7 | 生产工单 |
| DailyWork | 5 | 报工 + 完成报工 |
| User | 3 | 登录 + 注册 + 登出 |
| Factory | 2 | 注册 + 详情 |
| Dashboard | 1 | 首页统计 |

**共 44 个 POST 接口**，详见 [API 文档](docs/api/API.md)

## 数据库

13 张表，factory_id 数据隔离。详见 [建表脚本](docs/sql/dbFactory.sql)
