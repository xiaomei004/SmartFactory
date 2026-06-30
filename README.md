# SmartFactory 东软智能制造云平台

Spring Boot 4.0.7 + Java 21 + MyBatis + MySQL 智能制造管理系统。

## 快速启动

```bash
# 1. 创建数据库
mysql -u root -p123456 < docs/开发说明文档/dbFactory.sql

# 2. 启动应用
./mvnw spring-boot:run

# 3. 测试
curl -X POST http://localhost:8080/product/list \
  -H "Content-Type: application/json" \
  -d '{"pageNum":1,"pageSize":10,"factoryId":1}'
```

## 技术栈

- Spring Boot 4.0.7
- Java 21
- MyBatis (注解)
- MySQL 5.6+
- Lombok

## 项目结构

```
src/main/java/com/xiaomei/
├── common/base/      BaseController, BaseQuery, PageObject
├── common/constant/   StatusEnum
├── common/util/       JacksonUtil, PrimaryGenerater
├── config/            CorsConfig, WebConfig
├── interceptor/       LoginInterceptor
├── entity/            13个实体类
├── mapper/            MyBatis Mapper
├── service/           业务接口 + impl/
└── controller/        REST API
```

## 模块

| 模块 | 接口数 | 说明 |
|------|--------|------|
| Product | 5 | 产品 CRUD |
| Equipment | 7 | 设备 + 产能关联 |
| Order | 7 | 订单 + 状态流转 |
| Plan | 6 | 生产计划 |
| Schedule | 7 | 生产工单 |
| DailyWork | 5 | 报工 + 完成报工 |
| User | 2 | 登录 + 注册 |
| Factory | 2 | 注册 + 详情 |
| Dashboard | 1 | 首页统计 |

## API 文档

详见 [docs/开发说明文档/API.md](docs/开发说明文档/API.md)

## 数据库

13 张表，所有业务表带 factory_id 数据隔离。详见 [docs/开发说明文档/dbFactory.sql](docs/开发说明文档/dbFactory.sql)

## Git

```bash
git commit -m "Phase5: 全局异常处理 + SQL索引 + API文档 + README"
```
