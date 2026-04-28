# DEV-001 Coding Notes

## 文档信息

| 字段 | 内容 |
|---|---|
| 项目 | studyRomm |
| 阶段 | 技术开发阶段 |
| 状态 | 已更新 |
| 版本 | v0.2 |
| 最后更新 | 2026-04-28 |
| 负责人 | AI coding agent |
| 相关文档 | docs/04_development/implementation-plan.md |

## 文档定位

- 本文档记录仓库中的实现现状与执行备注。
- 本文档不是阶段 4 的权威规划口径。
- 阶段 4 的开发规划、门禁判断和下一步建议以 `docs/04_development/implementation-plan.md` 为准。

## 本次实现范围

- 沿用并补强后端 Maven 多模块父工程与首批三服务基座。
- 补齐公共任务状态契约：`/api/common/tasks/{taskId}`。
- 补齐 `testing-common` 的最小测试辅助能力。
- 补齐 `auth-service`、`knowledge-service` 的统一错误结构与未知路由测试。
- 调整 `gateway` 测试方式，避免环境对 Netty 随机端口启动的影响。
- 新建前端最小工作区：`web-client`、`admin-console`、`shared`、`mock`。

## 关键实现说明

- 按阶段规划，仅落 `DEV-001` 基座，不实现业务逻辑。
- 健康接口统一为 `/api/common/health`。
- 新增公共任务状态返回结构 `TaskStatusResponse` 与 `TaskStatus`，用于前端并行开发阶段对齐任务契约。
- `security-common` 当前采用 `permitAll`，只作为后续权限能力的占位基座。
- Servlet 服务侧 `traceId` 通过 `X-Trace-Id` 请求头透传或自动生成；Gateway 侧沿用响应头透传，并在任务基线接口中回传同一请求的 trace 值。
- 前端工作区采用 `frontend/` 子目录和 npm workspaces 组织两个应用与两个共享包，满足 `DEV-001` 的工程壳层、路由骨架、API client 与 mock/stub 基座要求。

## 当前环境与验证结论

- 本机构建环境使用 `Java 21.0.10` + `Apache Maven 3.6.3`。
- 需要显式设置 `JAVA_HOME=D:\java21` 才能稳定执行 Maven 命令。
- 已执行后端聚合 `mvn test` 验证：`gateway`、`auth-service`、`knowledge-service` 当前测试全部通过。
- `gateway` 原始 `SpringBootTest(RANDOM_PORT)` 在当前 Windows 环境下因 loopback / selector 问题无法稳定启动，已改为 `@WebFluxTest` slice 测试后通过。
- 前端文件已落地，但 Node/npm 在当前环境下启动即触发 `ncrypto::CSPRNG(nullptr, 0)` 断言失败，因此本轮无法执行前端 `npm` / `vitest` / `vite build` 验证。

## 本次验证证据

- 执行命令：`cmd /c "set JAVA_HOME=D:\java21&& set PATH=D:\java21\bin;%PATH%&& D:\soft\apache-maven-3.6.3\bin\mvn.cmd test -f backend\pom.xml"`
- 执行目录：仓库根目录
- 执行时间：2026-04-28
- 结果摘要：Reactor 8 个模块全部 `BUILD SUCCESS`
- 新增通过项：
  - `gateway` 健康检查与公共任务契约测试
  - `auth-service` 健康检查、公共任务契约、未知路由统一错误结构测试
  - `knowledge-service` 健康检查、公共任务契约、未知路由统一错误结构测试
- 未执行项：
  - 前端 `npm` / `vitest` / `vite build`，受 Node 运行时异常阻塞

## TODO

| ID | 问题 | 类型 | 优先级 | 负责人 | 状态 |
|---|---|---|---|---|---|
| TODO-CODE-001 | 补充 Maven Wrapper 或稳定的团队级构建入口 | 技术 | P1 | AI coding agent | 部分处理 |
| TODO-CODE-002 | 在 DEV-002 中引入 Flyway 与数据库连接配置 | 技术 | P0 | AI coding agent | 未处理 |
| TODO-CODE-003 | 修复当前 Node / npm 运行时 `ncrypto::CSPRNG(nullptr, 0)` 异常，恢复前端依赖安装与测试能力 | 技术 | P0 | AI coding agent | 未处理 |
| TODO-CODE-004 | 为前端工作区补跑 `typecheck`、`vitest` 与 `build`，并补齐真实验证证据 | 测试 | P0 | AI coding agent | 未处理 |
