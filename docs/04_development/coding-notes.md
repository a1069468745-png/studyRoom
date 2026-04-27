# DEV-001 Coding Notes

## 文档信息

| 字段 | 内容 |
|---|---|
| 项目 | studyRomm |
| 阶段 | 技术开发阶段 |
| 状态 | 草案 |
| 版本 | v0.1 |
| 最后更新 | 2026-04-27 |
| 负责人 | AI coding agent |
| 相关文档 | docs/04_development/implementation-plan.md |

## 文档定位

- 本文档记录仓库中的实现现状与执行备注。
- 本文档不是阶段 4 的权威规划口径。
- 阶段 4 的开发规划、门禁判断和下一步建议以 `docs/04_development/implementation-plan.md` 为准。

## 本次实现范围

- 新建后端 Maven 多模块父工程。
- 新建公共模块：`service-common`、`web-common`、`security-common`、`testing-common`。
- 新建首批服务：`gateway`、`auth-service`、`knowledge-service`。
- 提供最小健康检查、错误模型、`traceId` 过滤器和基础测试样例。

## 关键实现说明

- 按阶段规划，仅落 `DEV-001` 基座，不实现业务逻辑。
- 健康接口统一为 `/api/common/health`。
- `security-common` 当前采用 `permitAll`，只作为后续权限能力的占位基座。
- `traceId` 通过 `X-Trace-Id` 请求头透传或自动生成。

## 当前环境与验证结论

- 本机构建环境已更新为 `Java 21.0.10` + `Apache Maven 3.6.3`。
- 已执行 `mvn test` 验证聚合工程，`gateway`、`auth-service`、`knowledge-service` 三个服务的最小测试样例均通过。
- 构建过程中发现 `web-common` 缺少 `jakarta.servlet-api` 编译依赖，已补齐并复测通过。

## 本次验证证据

- 执行命令：`mvn test`
- 执行目录：`backend/`
- 执行时间：2026-04-27
- 结果摘要：Reactor 8 个模块全部 `BUILD SUCCESS`

## TODO

| ID | 问题 | 类型 | 优先级 | 负责人 | 状态 |
|---|---|---|---|---|---|
| TODO-CODE-001 | 补充 Maven Wrapper 或可执行构建环境 | 技术 | P0 | AI coding agent | 已通过可执行 Maven 环境解决 |
| TODO-CODE-002 | 在 DEV-002 中引入 Flyway 与数据库连接配置 | 技术 | P0 | AI coding agent | 未处理 |
