# DEV-001 / DEV-002 Coding Notes

## 文档信息

| 字段 | 内容 |
|---|---|
| 项目 | studyRomm |
| 阶段 | 技术开发阶段 |
| 状态 | 已更新 |
| 版本 | v0.4 |
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
- 在 `auth-service`、`knowledge-service` 中引入 Flyway、JDBC 与开发态 H2 数据源。
- 新增首批核心主表迁移脚本，覆盖 `auth_service`、`knowledge_service`、`question_service`、`exam_service`、`job_service`。
- 补齐建库回归测试，验证从空库初始化、唯一约束和核心关系插入。

## 关键实现说明

- 按阶段规划，仅落 `DEV-001` 基座，不实现业务逻辑。
- 健康接口统一为 `/api/common/health`。
- 新增公共任务状态返回结构 `TaskStatusResponse` 与 `TaskStatus`，用于前端并行开发阶段对齐任务契约。
- `security-common` 当前采用 `permitAll`，只作为后续权限能力的占位基座。
- Servlet 服务侧 `traceId` 通过 `X-Trace-Id` 请求头透传或自动生成；Gateway 侧沿用响应头透传，并在任务基线接口中回传同一请求的 trace 值。
- 前端工作区采用 `frontend/` 子目录和 npm workspaces 组织两个应用与两个共享包，满足 `DEV-001` 的工程壳层、路由骨架、API client 与 mock/stub 基座要求。
- `DEV-002` 当前采用 H2 MySQL 模式作为测试态迁移载体，既保持 Maven 测试可本地执行，也不改变后续 MySQL 8 的目标落地口径。
- `auth-service` 迁移脚本落地 `user`、`role`、`user_role`、`class_room`、`class_membership`、`teaching_assignment`、`resource_owner_scope`、`audit_log`。
- `knowledge-service` 迁移脚本落地 `textbook_version`、`curriculum_node`、`content_asset`、`question`、`question_option`、`question_answer`、`question_analysis`、`question_knowledge`、`question_curriculum_node`、`exam_plan`、`exam_plan_target`、`exam_session`、`exam_submission`、`job_task`。

## 当前环境与验证结论

- 本机构建环境使用 `Java 21.0.10` + `Apache Maven 3.6.3`。
- 需要显式设置 `JAVA_HOME=D:\java21` 才能稳定执行 Maven 命令。
- 已执行后端聚合 `mvn test` 验证：`gateway`、`auth-service`、`knowledge-service` 当前测试全部通过。
- `gateway` 原始 `SpringBootTest(RANDOM_PORT)` 在当前 Windows 环境下因 loopback / selector 问题无法稳定启动，已改为 `@WebFluxTest` slice 测试后通过。
- 前端工作区依赖安装、`vite build`、`vitest` 与开发服务器冒烟验证已完成。
- 当前 `frontend/packages/shared/tsconfig.json` 中存在本地 `ignoreDeprecations: "6.0"` 配置，导致 `npm run typecheck --workspaces --if-present` 失败；该问题不影响 `DEV-001` 的构建、测试与运行验证，但需要在前端后续回归中修复。
- `DEV-002` 已完成首轮落地：Flyway 迁移脚本在测试中可自动执行，首批核心表可从空库初始化完成。
- 当前 Flyway 在测试输出中会提示 H2 `2.2.224` 高于官方验证版本；该提示不影响迁移执行结果，但后续升级 Flyway 时应一并回归。

## 本次验证证据

- 执行命令：`cmd /c "set JAVA_HOME=D:\java21&& set PATH=D:\java21\bin;%PATH%&& D:\soft\apache-maven-3.6.3\bin\mvn.cmd test -f backend\pom.xml"`
- 执行目录：仓库根目录
- 执行时间：2026-04-28
- 结果摘要：Reactor 8 个模块全部 `BUILD SUCCESS`
- 新增通过项：
  - `gateway` 健康检查与公共任务契约测试
  - `auth-service` 健康检查、公共任务契约、未知路由统一错误结构测试
  - `knowledge-service` 健康检查、公共任务契约、未知路由统一错误结构测试
  - `auth-service` Flyway 建库、唯一约束、班级成员与资源归属关系测试
  - `knowledge-service` Flyway 建库、题目/考试/任务主表约束与关系测试
- 执行命令：`npm run build --workspaces --if-present`
- 执行目录：`frontend/`
- 结果摘要：`@study-room/admin-console` 与 `@study-room/web-client` 生产构建通过
- 执行命令：`npm test`
- 执行目录：`frontend/`
- 结果摘要：`@study-room/shared` 的 3 个测试全部通过
- 执行命令：后台启动 `npm run dev:web-client`、`npm run dev:admin-console` 并检查 `5173`、`5174` 端口监听
- 结果摘要：两个开发服务器均可正常监听后再清理进程
- 当前未闭合项：
  - 前端工作区 `typecheck` 因 `frontend/packages/shared/tsconfig.json` 的本地 `ignoreDeprecations` 配置失败

## TODO

| ID | 问题 | 类型 | 优先级 | 负责人 | 状态 |
|---|---|---|---|---|---|
| TODO-CODE-001 | 补充 Maven Wrapper 或稳定的团队级构建入口 | 技术 | P1 | AI coding agent | 部分处理 |
| TODO-CODE-002 | 在 DEV-002 中引入 Flyway 与数据库连接配置 | 技术 | P0 | AI coding agent | 已处理 |
| TODO-CODE-003 | 修复当前 Node / npm 运行时 `ncrypto::CSPRNG(nullptr, 0)` 异常，恢复前端依赖安装与测试能力 | 技术 | P0 | AI coding agent | 已处理 |
| TODO-CODE-004 | 修复 `frontend/packages/shared/tsconfig.json` 的本地 `ignoreDeprecations` 配置，并补齐前端 `typecheck` 证据 | 测试 | P0 | AI coding agent | 部分处理 |
