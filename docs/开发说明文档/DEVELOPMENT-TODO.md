# SmartFactory 开发TODO与规范指南

> **用途**: 上下文恢复 + 开发规范 + 进度追踪  
> **更新**: 每完成一个子任务就勾选 `[x]`，上下文丢失后读此文件即可接续  
> **最后更新**: 2026-06-30

---

## 快速恢复（给断点续接的 AI 看）

```
当前阶段:  Phase 1 — 基础设施搭建
当前状态:  项目骨架已建，Entity/基类/工具类待写
数据库:    db_factory 已设计完毕，建表 SQL 已就绪 (dbFactory.sql)
最近完成:  需求文档、PRD、本 TODO
下一步:    1.1 创建包结构 → 1.2 Entity实体类 → 1.3 基类 → 1.4 工具类 → 1.5 验证链路
```

---

## 一、项目技术基线

| 项 | 值 |
|---|-----|
| 基础包名 | `com.xiaomei` |
| Spring Boot | 4.0.7 |
| Java | 21 |
| ORM | MyBatis (注解优先，复杂查询用XML) |
| 数据库 | MySQL — `db_factory` (utf8) |
| 连接信息 | `localhost:3306/db_factory` / root / 123456 |
| 简化工具 | Lombok |
| 项目路径 | `src/main/java/com/xiaomei/` |
| 资源路径 | `src/main/resources/` |
| SQL脚本 | `docs/开发说明文档/dbFactory.sql` |
| PRD文档 | `docs/开发说明文档/SmartFactory-PRD-产品需求文档.md` |

> ⚠️ 设计文档里的包名是 `com.neuedu`，实际项目用 `com.xiaomei`。所有代码以实际项目为准。

---

## 二、代码规范速查

### 2.1 包结构约定

```
com.xiaomei
├── SamrtFactoryApplication.java    ← 启动类（已有）
├── common/                         ← 通用基类与工具
│   ├── base/
│   │   ├── BaseController.java     ← 控制器基类（注入当前用户/工厂ID）
│   │   ├── BaseQuery.java          ← 查询基类（分页参数）
│   │   └── PageObject.java         ← 前端统一返回对象
│   ├── constant/
│   │   └── StatusEnum.java         ← 状态常量定义
│   └── util/
│       ├── JacksonUtil.java        ← JSON转换
│       ├── FileUploadUtil.java     ← 文件上传
│       ├── PrimaryGenerater.java   ← 业务编号生成
│       └── StringUtil.java         ← 字符串工具
├── config/
│   └── CorsConfig.java             ← 跨域配置
├── interceptor/
│   └── LoginInterceptor.java       ← 登录拦截器（模拟登录+注入factory_id）
├── entity/                         ← 数据库实体（与表一一对应）
│   ├── Factory.java
│   ├── User.java
│   ├── UserRole.java
│   ├── Permit.java
│   ├── RolePermit.java
│   ├── Product.java
│   ├── Equipment.java
│   ├── EquipmentProduct.java
│   ├── ProductOrder.java
│   ├── ProductPlan.java
│   ├── ProductSchedule.java
│   ├── DailyWork.java
│   └── OrderTrack.java
├── mapper/                         ← MyBatis Mapper接口
│   ├── FactoryMapper.java
│   ├── UserMapper.java
│   ├── ProductMapper.java
│   ├── EquipmentMapper.java
│   ├── EquipmentProductMapper.java
│   ├── ProductOrderMapper.java
│   ├── ProductPlanMapper.java
│   ├── ProductScheduleMapper.java
│   ├── DailyWorkMapper.java
│   └── OrderTrackMapper.java
├── service/                        ← 业务逻辑层
│   ├── ProductService.java
│   ├── EquipmentService.java
│   ├── OrderService.java
│   ├── PlanService.java
│   ├── ScheduleService.java
│   ├── DailyWorkService.java
│   ├── DashboardService.java
│   └── impl/                       ← 实现类
│       ├── ProductServiceImpl.java
│       ├── EquipmentServiceImpl.java
│       └── ...
└── controller/                     ← REST API层
    ├── ProductController.java
    ├── EquipmentController.java
    ├── OrderController.java
    ├── PlanController.java
    ├── ScheduleController.java
    ├── DailyWorkController.java
    └── DashboardController.java
```

### 2.2 Entity 规范

```java
@Data
@Table(name = "t_xxxx")          // 使用 lombok + mybatis 注解
public class Xxxx implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;           // 主键自增，用 Integer 不用 int

    private Integer flag;         // 有效标识 0:有效 1:无效
    private Date createTime;      // 创建时间，使用 java.util.Date
    private Integer createUserid; // 创建人ID
    private Date updateTime;      // 修改时间
    private Integer updateUserid; // 修改人ID
    private Integer factoryId;    // 工厂ID（数据隔离）

    // 业务字段...
}
```

**严格规则**：
- 实体类名用小驼峰`XxxxEntity`风格，用 `@Table` 注解映射表名（实际表名用下划线 `t_xxxx`）
- 主键 `id` 用 `Integer`（不用 `int`），自增
- 日期用 `java.util.Date`，与 MySQL `datetime` 对应
- `factory_id` 字段所有业务表统一带
- `flag` 逻辑删除标识，查询默认加 `flag = 0`

### 2.3 Mapper 规范

```java
@Mapper
public interface XxxxMapper {
    // 分页查询
    List<Xxxx> selectList(XxxxQuery query);

    // 按ID查
    Xxxx selectById(@Param("id") Integer id);

    // 新增
    int insert(Xxxx entity);

    // 修改
    int update(Xxxx entity);

    // 逻辑删除 (设flag=1)
    int delete(@Param("id") Integer id, @Param("updateUserid") Integer updateUserid);
}
```

**严格规则**：
- 每个 Mapper 加 `@Mapper` 注解
- 多参数用 `@Param` 注解
- 查询方法接受对应的 Query 对象
- 所有查询必须带 `factory_id` 条件（数据隔离）

### 2.4 Service 规范

```java
public interface XxxxService {
    PageObject<Xxxx> queryList(XxxxQuery query);  // 分页查询，返回 PageObject
    Xxxx queryById(Integer id);
    Map<String, String> add(Xxxx entity);          // 返回 {status, msg}
    Map<String, String> edit(Xxxx entity);
    Map<String, String> delete(Integer id);
}
```

### 2.5 Controller 规范

```java
@RestController
@RequestMapping("/product")
public class ProductController extends BaseController {

    @Autowired
    private ProductService productService;

    @PostMapping("/list")
    public PageObject<Product> list(@RequestBody ProductQuery query) {
        return productService.queryList(query);
    }

    @PostMapping("/add")
    public Map<String, String> add(@RequestBody Product product) {
        return productService.add(product);
    }
    // ...
}
```

**严格规则**：
- 统一 `@RestController` + `@RequestMapping("/xxx")`
- 统一使用 `POST` 请求，`@RequestBody` 接收 JSON
- 继承 `BaseController` 获取 `currentUserId` 和 `factoryId`
- 返回格式统一为 `PageObject<T>`（列表）或 `Map<String, String>`（操作结果）
- 操作结果 JSON 格式：`{"status":"ok"/"error", "msg":"提示信息"}`

### 2.6 状态枚举

按照 dbFactory.sql 中的数值：
- **设备状态**: 10=启用, 20=停用, 30=故障
- **订单状态**: 10=未接单, 20=已接单, 30=已拒绝, 40=生产中, 50=已完成
- **计划状态**: 10=未启动, 20=已启动, 30=已完成
- **工单状态**: 10=未开始, 20=生产中, 30=已完成
- **工厂状态**: 0=正常, 1=关闭
- **用户状态**: 0=正常, 1=锁定

### 2.7 文件组织规范

```
每个业务模块的后端开发顺序（不可变）：
  Entity → Mapper → Service接口 → ServiceImpl → Controller

每个模块完成后：
  1. 启动应用 mvnw spring-boot:run
  2. 用 curl 验证接口通不通
  3. git add + git commit
```

---

## 三、开发总览 TODO

### Phase 1 — 基础设施搭建 [ ]

```
目标: 搭好所有基类/工具/Entity，跑通一条验证链路
估计: 需要一次性完成，不可拆分
```

| 编号 | 任务 | 状态 | 产出物 |
|------|------|------|--------|
| 1.1 | 创建包结构 | `[ ]` | 所有 package |
| 1.2 | 创建 13 个 Entity 实体类 | `[ ]` | entity/*.java |
| 1.3 | 创建 BaseController / BaseQuery / PageObject | `[ ]` | common/base/*.java |
| 1.4 | 创建 StatusEnum 常量类 | `[ ]` | common/constant/StatusEnum.java |
| 1.5 | 创建工具类 (JacksonUtil, PrimaryGenerater) | `[ ]` | common/util/*.java |
| 1.6 | 创建 CorsConfig 跨域配置 | `[ ]` | config/CorsConfig.java |
| 1.7 | 创建 LoginInterceptor | `[ ]` | interceptor/LoginInterceptor.java |
| 1.8 | 创建 Product 全链路（Entity→Mapper→Service→Controller） | `[ ]` | 验证链路 |
| 1.9 | 启动应用，curl 验证接口通 | `[ ]` | 接口返回正常 |
| 1.10 | git commit | `[ ]` | 提交记录 |

### Phase 2 — 基础数据模块 [ ]

```
目标: 产品管理 + 设备管理（含产能关联）完整可用
依赖: Phase 1 完成
```

| 编号 | 任务 | 状态 | 产出物 |
|------|------|------|--------|
| 2.1 | 产品管理 CRUD（已在1.8验证，补全图片上传） | `[ ]` | ProductController 完整体 |
| 2.2 | 设备管理 CRUD（含设备-产品产能关联） | `[ ]` | EquipmentController + EquipmentProductMapper |
| 2.3 | 工厂注册 + 用户登录接口 | `[ ]` | FactoryController + UserController |
| 2.4 | 对接前端页面（产品列表、设备列表、登录） | `[ ]` | 前端页面 |
| 2.5 | git commit | `[ ]` | 提交记录 |

### Phase 3 — 核心业务模块 [ ]

```
目标: 订单→计划→调度，完整生产链路
依赖: Phase 2 完成
```

| 编号 | 任务 | 状态 | 产出物 |
|------|------|------|--------|
| 3.1 | 订单管理（新建→接单→拒单→转计划→完成） | `[ ]` | OrderController |
| 3.2 | 生产计划管理（新建→启动→删除） | `[ ]` | PlanController |
| 3.3 | 生产调度管理（新建工单→分配设备→启动→删除） | `[ ]` | ScheduleController |
| 3.4 | 对接前端页面 | `[ ]` | 前端页面 |
| 3.5 | 端到端流程测试（订单→计划→工单→报工→完成） | `[ ]` | 测试通过 |
| 3.6 | git commit | `[ ]` | 提交记录 |

### Phase 4 — 生产跟踪与首页 [ ]

```
目标: 报工闭环 + 仪表盘可视化
依赖: Phase 3 完成
```

| 编号 | 任务 | 状态 | 产出物 |
|------|------|------|--------|
| 4.1 | 生产报工（报工→完成报工→更新订单进度） | `[ ]` | DailyWorkController |
| 4.2 | 首页仪表盘（设备统计+订单统计+年度汇总） | `[ ]` | DashboardController |
| 4.3 | 对接前端页面 | `[ ]` | 前端页面 |
| 4.4 | 全流程回归测试 | `[ ]` | 测试通过 |
| 4.5 | git commit | `[ ]` | 提交记录 |

### Phase 5 — 打磨交付 [ ]

```
目标: Bug修复、优化、文档、交付
依赖: Phase 4 完成
```

| 编号 | 任务 | 状态 | 产出物 |
|------|------|------|--------|
| 5.1 | 边界处理与异常提示优化 | `[ ]` | 代码优化 |
| 5.2 | SQL 索引优化 | `[ ]` | 索引脚本 |
| 5.3 | 编写 API 文档 | `[ ]` | API.md |
| 5.4 | 编写 README | `[ ]` | README.md |
| 5.5 | 最终测试与 git push | `[ ]` | 提交记录 |

---

## 四、每次开发的 SOP（标准操作流程）

```
┌─────────────────────────────────────────┐
│  新功能开发 SOP（每次开始前过一遍）        │
├─────────────────────────────────────────┤
│                                         │
│  Step 0: 读本文件，确认当前阶段和下一步    │
│                                         │
│  Step 1: 写 Entity                       │
│    → 参考 dbFactory.sql 字段             │
│    → 用 @Data + @Table 注解              │
│    → 统一6个审计字段                     │
│                                         │
│  Step 2: 写 Mapper                       │
│    → @Mapper + @Select/@Insert/@Update   │
│    → 所有查询带 factory_id               │
│                                         │
│  Step 3: 写 Service 接口 + Impl          │
│    → 返回 PageObject 或 Map              │
│    → 业务校验写在这里                     │
│                                         │
│  Step 4: 写 Controller                   │
│    → extends BaseController              │
│    → @PostMapping 统一 POST             │
│    → @RequestBody 接收                   │
│                                         │
│  Step 5: 启动验证                        │
│    → ./mvnw spring-boot:run             │
│    → curl 测试接口                       │
│                                         │
│  Step 6: 提交代码                        │
│    → git add .                          │
│    → git commit -m "完成XXX模块"         │
│    → 在本文件中勾选 [x]                  │
│                                         │
└─────────────────────────────────────────┘
```

---

## 五、接口验证模板

每完成一个模块，用以下模板验证：

```bash
# 启动应用
cd /c/Users/chen/Desktop/javacode/vibe_coding/SamrtFactory
./mvnw spring-boot:run

# 另开终端，测试接口

# 1. 分页查询
curl -X POST http://localhost:8080/product/list \
  -H "Content-Type: application/json" \
  -d '{"pageNum":1,"pageSize":10,"factoryId":1}'

# 2. 新增
curl -X POST http://localhost:8080/product/add \
  -H "Content-Type: application/json" \
  -d '{"productName":"测试产品","factoryId":1}'

# 3. 修改
curl -X POST http://localhost:8080/product/edit \
  -H "Content-Type: application/json" \
  -d '{"id":1,"productName":"修改后名称"}'

# 4. 删除
curl -X POST http://localhost:8080/product/delete \
  -H "Content-Type: application/json" \
  -d '{"id":1}'
```

**预期响应格式**：
```json
// 列表成功
{
  "dataList": [...],
  "pageInfo": {"pageNum": 1, "pageSize": 10, "total": 50},
  "status": "ok"
}

// 操作成功
{"status": "ok", "msg": "操作成功"}

// 操作失败
{"status": "error", "msg": "产品名称已存在"}
```

---

## 六、Git 提交规范

```bash
# 提交格式
git add .
git commit -m "模块名: 简短描述"

# 示例
git commit -m "Phase1: 完成基础设施搭建（Entity/基类/工具类）"
git commit -m "Product: 完成产品管理CRUD接口"
git commit -m "Order: 完成订单状态流转逻辑"
git commit -m "BugFix: 修复接单时产能校验异常"
```

**每个 commit 的粒度**：一个完整的子任务（表 + Mapper + Service + Controller）

---

## 七、当前数据库连接确认

```yaml
# application.yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/db_factory?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: 123456
```

> 开发前确认 MySQL 服务已启动，`db_factory` 库已创建，建表 SQL 已执行。

---

> **最后更新**: 2026-06-30  
> **当前状态**: Phase 1 待开工  
> **下一步**: 任务 1.1 — 创建包结构
