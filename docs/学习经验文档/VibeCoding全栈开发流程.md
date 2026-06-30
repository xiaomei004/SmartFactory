# Vibe Coding 全栈开发流程

> Vue 3 + Spring Boot 4 + MyBatis + MySQL 管理系统开发指南
>
> 2026.6 大三生产实习

---

## 一、什么是 Vibe Coding

**Vibe Coding** 是一种 AI 辅助的现代开发方式，核心思想：

```
你说需求 → AI 出代码 → 你 review → 跑起来看效果 → 不满意就改 → 满意就提交
```

不追求一次写对，而是**快速出 MVP → 跑起来验证 → 迭代打磨**。

---

## 二、项目整体结构

```
SmartFactory/                  ← 你的 Git 仓库根目录
├── backend/                   ← Spring Boot 后端（你现有的）
│   ├── pom.xml
│   ├── src/main/java/com/xiaomei/
│   │   ├── SamrtFactoryApplication.java
│   │   ├── controller/        ← API 接口层
│   │   ├── service/           ← 业务逻辑层
│   │   ├── mapper/            ← 数据库访问层（MyBatis）
│   │   └── entity/            ← 数据实体类
│   └── src/main/resources/
│       ├── application.yaml
│       └── mapper/            ← MyBatis XML（如果用 XML 方式）
│
├── frontend/                  ← Vue 3 前端（待创建）
│   ├── package.json
│   ├── src/
│   │   ├── views/             ← 页面组件
│   │   ├── components/        ← 通用组件
│   │   ├── api/               ← 后端 API 封装
│   │   ├── router/            ← 路由配置
│   │   └── stores/            ← 状态管理（Pinia）
│   └── vite.config.js
│
└── docs/                      ← 项目文档
    └── 数据库设计文档.md
```

---

## 三、标准开发流程（四步走）

### 📐 第一步：数据库设计

**目标**：搞清楚要存什么数据，表怎么建。

```
1. 整理需求
   "我要一个仓库管理系统，能管库存、出入库记录、员工信息"

2. 画表结构（用 AI 帮你）
   对 AI 说："设计 MySQL 表结构，包含：
   - 仓库表（名称、地址、容量）
   - 物料表（名称、规格、库存数量、所属仓库）
   - 出入库记录表（物料、数量、类型、操作人、时间）
   写出建表 SQL"

3. 执行 SQL
   在 MySQL 中创建数据库和表
```

**产物**：`docs/数据库设计.md` + 建表 SQL

---

### 🔧 第二步：后端 API 开发

**目标**：写出 RESTful API，让前端能调接口拿数据。

开发顺序：**Entity → Mapper → Service → Controller**

```
Step 1: Entity（实体类）
  ┌─────────────────────────┐
  │ 和数据库表一一对应        │
  │ 例：Warehouse.java       │
  │   - id, name, address   │
  │   - getter/setter       │
  └─────────────────────────┘
          ↓
Step 2: Mapper（数据访问层）
  ┌─────────────────────────┐
  │ 操作数据库的接口          │
  │ 例：WarehouseMapper.java  │
  │   - selectAll()          │
  │   - insert()             │
  │   - update()             │
  │   - deleteById()         │
  └─────────────────────────┘
          ↓
Step 3: Service（业务逻辑层）
  ┌─────────────────────────┐
  │ 处理业务规则              │
  │ 例：WarehouseService.java │
  │   - 新增仓库前检查重名     │
  │   - 删除前检查是否有库存   │
  └─────────────────────────┘
          ↓
Step 4: Controller（接口层）
  ┌─────────────────────────┐
  │ 暴露 REST API 给前端      │
  │ GET    /api/warehouses   │
  │ POST   /api/warehouses   │
  │ PUT    /api/warehouses/1 │
  │ DELETE /api/warehouses/1 │
  └─────────────────────────┘
```

**每写完一层就测试**，不要全部写完再测：

```bash
# 启动后端
./mvnw spring-boot:run

# 用 curl 或浏览器测试 API
curl http://localhost:8080/api/warehouses
```

---

### 🎨 第三步：前端页面开发

**目标**：用 Vue 搭建页面，调后端 API 展示数据。

```
Step 1: 搭建 Vue 项目
  npm create vue@latest frontend
  # 选上：Router、Pinia、ESLint

Step 2: 安装依赖
  cd frontend
  npm install
  npm install axios element-plus   # axios 发请求，Element Plus 做 UI

Step 3: 封装 API 请求
  src/api/request.js        ← axios 实例，统一配置 baseURL、拦截器
  src/api/warehouse.js      ← 仓库相关的 API 调用函数

Step 4: 写页面
  src/views/WarehouseList.vue   ← 仓库列表页
  src/views/WarehouseForm.vue   ← 新增/编辑仓库页

Step 5: 配路由
  src/router/index.js       ← /warehouses → WarehouseList

Step 6: 联调
  前端 npm run dev → http://localhost:5173
  后端 ./mvnw spring-boot:run → http://localhost:8080
  前端页面点按钮 → 看后端接口有没有收到请求 → 看数据库有没有变化
```

**Vibe Coding 前端技巧**：

```
对 AI 说："用 Vue 3 + Element Plus 写一个仓库管理列表页，
包含搜索框、表格、分页、新增/编辑/删除按钮，
调后端 /api/warehouses 接口"
→ AI 直接生成完整 .vue 文件 → 复制进去 → 跑起来看效果 → 微调
```

---

### 🔗 第四步：前后端联调

**关键配置**：前端开发时后端在 `localhost:8080`，需要解决跨域。

```java
// 后端添加跨域配置
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173");
    }
}
```

---

## 四、Vibe Coding 核心心法

### 4.1 "先跑起来，再变好看"

```
❌ 错误做法：花 2 小时把 Entity、Mapper、Service、Controller 全部写完，一运行报 50 个错
✅ 正确做法：先写一个最简单的 /api/hello 接口 → 跑通 → 再加功能
```

### 4.2 "一个功能一个功能地做"

```
❌ 错误做法：先把所有数据库表建好，再写所有后端接口，再写所有前端页面
✅ 正确做法：
   第1轮：仓库 CRUD → 后端接口 → 前端页面 → 跑通 → git commit
   第2轮：物料 CRUD → 后端接口 → 前端页面 → 跑通 → git commit
   第3轮：出入库记录 → ...
```

### 4.3 "遇到 Bug，先问 AI"

```
1. 把报错信息完整复制给 AI
2. 附上相关代码片段
3. AI 给出修复方案 → 改代码 → 重跑
4. 解决了 → 理解原因 → git commit
```

### 4.4 "每完成一个功能就 commit"

```bash
git add .
git commit -m "完成仓库管理列表页"
git commit -m "完成仓库新增/编辑接口"
git commit -m "修复仓库删除时的库存校验bug"
```

---

## 五、你的 SmartFactory 项目实战路线图

基于你现有的项目（Spring Boot + MyBatis + MySQL 已配好），建议按以下顺序开发：

```
📅 第1天：数据库 + 第一个接口跑通
  ├── 设计 3~4 张核心表，写建表 SQL
  ├── 创建一个 Entity（如 Warehouse）
  ├── 创建 Mapper → Service → Controller
  ├── 用 curl/Postman 调通 GET/POST 接口
  └── git commit

📅 第2天：Vue 前端搭建 + 第一个页面
  ├── npm create vue 搭建前端项目
  ├── 装 Element Plus、axios
  ├── 写仓库列表页（表格 + 分页）
  ├── 前后端联调跑通
  └── git commit

📅 第3~4天：完善 CRUD
  ├── 补完仓库的编辑、删除功能
  ├── 物料管理（后端 + 前端）
  └── 每完成一个功能就 commit

📅 第5天：进阶功能
  ├── 出入库记录
  ├── 数据校验、异常处理
  ├── 登录/权限（可选）
  └── git commit

📅 第6天：打磨
  ├── UI 美化
  ├── Bug 修复
  ├── 写 README
  └── 最终 git push
```

---

## 六、常用 Vibe Coding 提问模板

对 AI 助手（如 Claude Code）可以直接这样说：

| 场景 | 提问模板 |
|------|----------|
| 设计表结构 | "设计一个 XXX 管理系统的 MySQL 表结构，包含 A、B、C 功能，写出建表 SQL" |
| 写后端接口 | "给 Warehouse 表写一套完整的 Spring Boot CRUD 接口，用 MyBatis 注解方式，包含分页查询" |
| 写前端页面 | "用 Vue 3 + Element Plus 写一个 XXX 列表页，包含搜索、表格、分页、增删改按钮" |
| 调 Bug | "这段代码报错了：[错误信息]，代码是：[贴代码]，帮我看看哪里有问题" |
| 优化建议 | "review 一下这 3 个文件的代码，有没有可以改进的地方" |

---

## 七、一句话总结

> **想清楚做什么 → 让 AI 帮你写 → 跑起来看效果 → 改到满意 → git commit → 下一个功能**
>
> 核心就四个字：**快跑快改**。
