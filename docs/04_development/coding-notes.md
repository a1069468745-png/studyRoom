# DEV-001 / DEV-003 Coding Notes

## 文档信息

| 字段 | 内容 |
|---|---|
| 项目 | studyRomm |
| 阶段 | 技术开发阶段 |
| 状态 | 已更新 |
| 版本 | v0.6 |
| 最后更新 | 2026-04-30 |
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
- 在 `auth-service`、`knowledge-service` 中引入 Flyway、JDBC 与 MySQL 数据源接入配置（环境变量驱动）。
- 新增首批核心主表迁移脚本，覆盖 `auth_service`、`knowledge_service`、`question_service`、`exam_service`、`job_service`。
- 补齐建库回归测试，验证从空库初始化、唯一约束和核心关系插入。
- 在 `security-common` 中落用户 Bearer 令牌校验、服务身份请求头校验、统一 401/403 返回和无状态安全链。
- 在 `auth-service` 中新增登录入口、`/api/common/me` 当前身份接口与登录审计基线。
- 在 `auth-service` 中补齐授权摘要查询与资源归属校验基线，当前覆盖管理员直通、直接 owner_user 归属、班级成员归属和教学授权归属。
- 在 `auth-service` 中新增最小资源访问校验端点，并将资源访问成功/拒绝都写入 `audit_log`，形成 `DEV-003` 第三模块的审计留痕基线。
- 在 `auth-service` 中补齐 `REVIEW/GRADING/PUBLISH/DELETE/EXPORT` 五类业务审计动作的统一落库基线，并新增 `logout` 审计动作。

## 关键实现说明

- 按阶段规划，仅落 `DEV-001` 基座，不实现业务逻辑。
- 健康接口统一为 `/api/common/health`。
- 新增公共任务状态返回结构 `TaskStatusResponse` 与 `TaskStatus`，用于前端并行开发阶段对齐任务契约。
- `security-common` 不再是纯 `permitAll` 占位；当前已切到无状态安全链，并支持 Bearer 令牌与服务身份请求头两条认证通道。
- Servlet 服务侧 `traceId` 通过 `X-Trace-Id` 请求头透传或自动生成；Gateway 侧沿用响应头透传，并在任务基线接口中回传同一请求的 trace 值。
- 前端工作区采用 `frontend/` 子目录和 npm workspaces 组织两个应用与两个共享包，满足 `DEV-001` 的工程壳层、路由骨架、API client 与 mock/stub 基座要求。
- `DEV-002` 与后续阶段统一采用 MySQL 接入口径，不再使用 H2 作为开发态或测试态数据库载体。
- 当前数据库统一口径为 MariaDB（MySQL 协议兼容），不再使用 H2。
- `auth-service` 迁移脚本落地 `user`、`role`、`user_role`、`class_room`、`class_membership`、`teaching_assignment`、`resource_owner_scope`、`audit_log`。
- `knowledge-service` 迁移脚本落地 `textbook_version`、`curriculum_node`、`content_asset`、`question`、`question_option`、`question_answer`、`question_analysis`、`question_knowledge`、`question_curriculum_node`、`exam_plan`、`exam_plan_target`、`exam_session`、`exam_submission`、`job_task`。
- `auth-service` 当前已提供 `POST /api/auth/login`、`GET /api/common/me`、服务身份校验和登录成功/失败审计记录，作为 `DEV-003` 第一模块的最小闭环。
- `/api/common/me` 当前已补 `authorizationSummary`，供前端读取平台管理员标记、班级归属、成员角色、教学学科、教学授权班级和资源类型摘要。
- 当前新增 `ResourceAccessService`，用于后续业务服务复用资源归属校验，不把权限逻辑散落到控制器。

## 当前环境与验证结论

- 本机构建环境使用 `Java 21.0.10` + `Apache Maven 3.6.3`。
- 需要显式设置 `JAVA_HOME=D:\java21` 才能稳定执行 Maven 命令。
- 已执行后端聚合 `mvn test` 验证：`gateway`、`auth-service`、`knowledge-service` 当前测试全部通过。
- `gateway` 原始 `SpringBootTest(RANDOM_PORT)` 在当前 Windows 环境下因 loopback / selector 问题无法稳定启动，已改为 `@WebFluxTest` slice 测试后通过。
- 前端工作区依赖安装、`vite build`、`vitest` 与开发服务器冒烟验证已完成。
- 当前 `frontend/packages/shared/tsconfig.json` 中存在本地 `ignoreDeprecations: "6.0"` 配置，导致 `npm run typecheck --workspaces --if-present` 失败；该问题不影响 `DEV-001` 的构建、测试与运行验证，但需要在前端后续回归中修复。
- `DEV-002` 已完成首轮落地：Flyway 迁移脚本在测试中可自动执行，首批核心表可从空库初始化完成。
- 当前验证与联调以 MySQL 为准；Flyway 的数据库兼容性回归基线也以 MySQL 版本为准。
- `DEV-003` 第一模块已完成：认证入口、当前身份查询、服务身份校验与登录审计测试通过。
- `DEV-003` 第二模块已完成：授权摘要查询与资源归属校验基线测试通过。
- `DEV-003` 第三模块已完成首轮最小闭环：资源访问校验成功/拒绝都会落审计，相关接口与测试已通过。
- `DEV-003` 第三模块已完成完整基线：登录、登出、资源访问检查、审核、批改、发布、删除、导出动作均已具备统一审计落库路径，并通过测试。

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
| TODO-CODE-005 | 将本机临时 `root/123456` 切换为专用应用账号（建议 `studyromm_app`）并完成最小权限收口 | 安全 | P0 | AI coding agent | 已处理 |

## DEV-004-A 实施记录（课程树与教材版本接口）

- 新增接口：
  - `GET /api/common/dictionaries`
  - `GET /api/common/dictionaries/textbook-versions`
  - `GET /api/common/dictionaries/curriculum-nodes`
- 新增查询服务：`KnowledgeDictionaryQueryService`，按 `textbookVersionId/subjectCode/nodeType/parentNodeId` 过滤课程树节点，按 `sortOrder` 稳定排序。
- 新增返回模型：`TextbookVersionDictionaryItem`、`CurriculumNodeDictionaryItem`。
- 保留 `GET /api/knowledge/bootstrap` 作为服务引导检查接口。

本轮验证证据：

- 执行命令：`cmd /c "set JAVA_HOME=D:\java21&& set PATH=D:\java21\bin;%PATH%&& D:\soft\apache-maven-3.6.3\bin\mvn.cmd test -f backend\pom.xml -pl services/knowledge-service -am"`
- 结果：`knowledge-service` 测试通过（`Tests run: 7, Failures: 0, Errors: 0`）。
- 新增通过测试：
  - 字典接口返回教材版本过滤结果。
  - 课程树子节点按 `sort_order` 返回并保持父子关系筛选。
  - `GET /api/common/dictionaries` 返回课程节点类型与教材版本字典。

## DEV-004-B 实施记录（知识点图文内容接口）

- 新增接口：
  - `GET /api/admin/knowledge/content-assets`
  - `POST /api/admin/knowledge/content-assets`
  - `GET /api/client/knowledge/content-assets`
- 新增服务：`KnowledgeContentAssetService`
  - 管理端支持按 `curriculumNodeId/reviewStatus/publishStatus` 查询图文内容；
  - 管理端新增图文内容时默认落 `reviewStatus=DRAFT`、`publishStatus=UNPUBLISHED`；
  - 客户端仅返回 `APPROVED + PUBLISHED` 的图文内容。
- 新增模型：`ContentAssetItem`、`CreateContentAssetRequest`、`CreateContentAssetResponse`。

本轮验证证据：

- 执行命令：`cmd /c "set JAVA_HOME=D:\java21&& set PATH=D:\java21\bin;%PATH%&& D:\soft\apache-maven-3.6.3\bin\mvn.cmd test -f backend\pom.xml -pl services/knowledge-service -am"`
- 数据源：`knowledge_service`（MariaDB，本地 `root/123456`）
- 结果：`knowledge-service` 测试通过（`Tests run: 8, Failures: 0, Errors: 0`）。
- 新增通过测试：
  - 管理端新增图文内容草稿并返回默认状态；
  - 管理端图文内容列表查询；
  - 客户端仅返回已发布图文内容。

## DEV-004-C 实施记录（题库录入与管理查询接口）

- 新增接口：
  - `GET /api/admin/questions`
  - `POST /api/admin/questions`
- 新增服务：`QuestionBankService`
  - 管理端支持按 `subjectCode/reviewStatus` 查询题目；
  - 题目录入时同步写入 `question`、`question_answer`、`question_analysis`；
  - 题目录入时要求至少绑定 1 个知识点，落 `question_knowledge`；
  - 支持可选课程树冗余绑定，落 `question_curriculum_node`。
- 新增模型：`CreateQuestionRequest`、`CreateQuestionResponse`、`QuestionItem`。
- 新增异常映射：`KnowledgeExceptionHandler`，将参数校验异常统一映射为 `400 VALIDATION_FAILED`。

本轮验证证据：

- 执行命令：`cmd /c "set JAVA_HOME=D:\java21&& set PATH=D:\java21\bin;%PATH%&& set STUDYROMM_KNOWLEDGE_DB_USERNAME=root&& set STUDYROMM_KNOWLEDGE_DB_PASSWORD=123456&& D:\soft\apache-maven-3.6.3\bin\mvn.cmd test -f backend\pom.xml -pl services/knowledge-service -am"`
- 数据源：`knowledge_service`（MariaDB，本地 `root/123456`）
- 结果：`knowledge-service` 测试通过（`Tests run: 10, Failures: 0, Errors: 0`）。
- 新增通过测试：
  - 管理端题目录入成功并返回 `DRAFT`；
  - 管理端按学科/审核状态查询题目列表；
  - 缺失知识点绑定时返回 `400 VALIDATION_FAILED`。

## DEV-004-D 实施记录（题目审核入口与状态流转）

- 新增接口：
  - `POST /api/admin/questions/{questionId}/review`
- `QuestionBankService` 新增审核流转：
  - 仅允许 `reviewStatus=APPROVED/REJECTED`；
  - 仅允许 `DRAFT -> APPROVED/REJECTED`；
  - 非 `DRAFT` 状态禁止再次审核。
- 新增响应模型：`ReviewQuestionRequest`、`ReviewQuestionResponse`。
- 异常收口：
  - 非法流转返回 `BUSINESS_RULE_VIOLATION`（400）；
  - 题目不存在返回 `RESOURCE_NOT_FOUND`（404）。

本轮验证证据：

- 执行命令：`cmd /c "set JAVA_HOME=D:\java21&& set PATH=D:\java21\bin;%PATH%&& set STUDYROMM_KNOWLEDGE_DB_USERNAME=root&& set STUDYROMM_KNOWLEDGE_DB_PASSWORD=123456&& D:\soft\apache-maven-3.6.3\bin\mvn.cmd test -f backend\pom.xml -pl services/knowledge-service -am"`
- 数据源：`knowledge_service`（MariaDB，本地 `root/123456`）
- 结果：`knowledge-service` 测试通过（`Tests run: 13, Failures: 0, Errors: 0`）。
- 新增通过测试：
  - `DRAFT` 题目审核通过；
  - 非 `DRAFT` 题目审核被拒绝；
  - 不存在题目返回 `404 RESOURCE_NOT_FOUND`。

## DEV-004 下一子项实施记录（客户端题目查询）

- 新增接口：
  - `GET /api/client/questions`
- 新增返回模型：`ClientQuestionItem`。
- `QuestionBankService` 新增客户端查询能力：
  - 仅返回 `reviewStatus=APPROVED` 题目；
  - 支持 `subjectCode/gradeCode/questionType` 过滤；
  - 按 `created_at desc, id desc` 返回。

本轮验证证据：

- 执行命令：`cmd /c "set JAVA_HOME=D:\java21&& set PATH=D:\java21\bin;%PATH%&& set STUDYROMM_KNOWLEDGE_DB_USERNAME=root&& set STUDYROMM_KNOWLEDGE_DB_PASSWORD=123456&& D:\soft\apache-maven-3.6.3\bin\mvn.cmd test -f backend\pom.xml -pl services/knowledge-service -am"`
- 数据源：`knowledge_service`（MariaDB，本地 `root/123456`）
- 结果：`knowledge-service` 测试通过（`Tests run: 14, Failures: 0, Errors: 0`）。
- 新增通过测试：
  - 客户端仅能查询到 `APPROVED` 题目，`DRAFT` 不可见。

## TASK-022 实施记录（学生端学习链路前端骨架联调）

- `web-client` 新增学习链路页面骨架：`/client/learning`。
- 页面采用“三栏联动”：
  - 左栏课程树（`GET /api/common/dictionaries/curriculum-nodes`）；
  - 中栏图文内容（`GET /api/client/knowledge/content-assets`）；
  - 右栏客户端题目列表（`GET /api/client/questions`）。
- 顶部筛选统一维护 `textbookVersionId/subjectCode/gradeCode/questionType/nodeId`，并同步到 URL query，支持刷新回放。
- 扩展共享 API client：
  - `getCurriculumNodes`
  - `getClientContentAssets`
  - `getClientQuestions`
- 新增共享类型：`CurriculumNodeDictionaryItem`、`ContentAssetItem`、`ClientQuestionItem`。
- 新增 mock 学习数据 `learningFixtures`，支持 `VITE_USE_MOCK=true` 的本地骨架联调。

本轮验证证据：

- 执行命令：`npm run build --workspace @study-room/web-client`
- 结果：构建通过（Vite build success）。
- 运行命令：`npm run dev:web-client`（后台）
- 结果：`5173` 端口监听成功，可进行本地联调访问。

## TASK-022 下一步实施记录（考试页与结果页骨架联调）

- `/client/exam` 从占位页升级为可操作骨架：
  - 考试提交表单（examId/clientId/answerPayload）
  - 提交任务状态面板（基于共享 task store + mock 任务）
- `/client/result` 从占位页升级为结果骨架：
  - 考试/学生上下文输入
  - 成绩与解析摘要面板（mock 数据）
- 路由更新：
  - `/client/learning`、`/client/exam`、`/client/result` 三页可导航联调。

本轮验证证据：

- 执行命令：`npm run build --workspace @study-room/web-client`
- 结果：构建通过（Vite build success）。

## DEV-004 完成标准证据补齐（Superpowers 分阶段）

说明：本节按 `docs/04_development/implementation-plan.md` 中 DEV-004 的“10. 完成标准”和“12. 验证证据要求”逐项补齐证据，并保留最新一次复核结果。

### 完成标准复核（对应 implementation-plan.md 第 10 节）

- [x] 相关功能实现完成（A/B/C/D 与客户端题目查询已落地，且有接口测试证据）
- [x] 单元/集成测试已运行并通过（`knowledge-service` 累计 `Tests run: 14, Failures: 0, Errors: 0`）
- [x] 相关验收标准已覆盖（AC-A/AC-G 对应能力已具备实现与校验）
- [x] 文档已更新（本文件、`task-breakdown.md` 已补充 DEV-004 收口信息）
- [x] DEV-004 权限边界专项测试证据已独立归档（见“DEV-004 收口追加验证（2026-04-30）”）
- [x] DEV-004 异常路径/回归/人工验证证据已独立归档（见“DEV-004 收口追加验证（2026-04-30）”）
- [x] 无 P0/P1 阻塞问题（`TODO-CODE-005` 已闭合）

### 验证证据复核（对应 implementation-plan.md 第 12 节）

- [x] 课程树、知识点、题目关联测试结果：已在 DEV-004-A/B/C/D 与“下一子项”记录中提供。
- [x] 入库约束验证结果：缺失知识点绑定返回 `400 VALIDATION_FAILED` 已验证。
- [x] 未覆盖项与剩余风险：DEV-004 范围内未覆盖项已收口，跨阶段事项按阶段计划继续跟踪。

### DEV-004 收口追加验证（2026-04-30）

- 权限/异常/边界/回归证据：
  - 执行命令：`cmd /c "set JAVA_HOME=D:\java21&& set PATH=D:\java21\bin;%PATH%&& set STUDYROMM_KNOWLEDGE_DB_USERNAME=studyromm_app&& set STUDYROMM_KNOWLEDGE_DB_PASSWORD=<masked>&& D:\soft\apache-maven-3.6.3\bin\mvn.cmd test -f backend\pom.xml -pl services/knowledge-service -am"`
  - 结果：`Tests run: 14, Failures: 0, Errors: 0`，覆盖课程树、图文内容、题目录入审核、`400 VALIDATION_FAILED`、`404 RESOURCE_NOT_FOUND`、异常路径与回归路径。
- 前端联调骨架回归证据：
  - 执行命令：`cmd /c "npm.cmd run build --workspace @study-room/web-client"`
  - 结果：`vite build` 通过（含学习/考试/结果页骨架）。
- 安全收口证据（TODO-CODE-005）：
  - 已执行：创建本地 `studyromm_app@localhost` 账号并授予 `auth_service`、`knowledge_service`、`question_service`、`exam_service`、`job_service` 最小权限（见 `backend/ops/sql/dev-db-least-privilege.sql`）。
  - 已验证：`studyromm_app` 连接 `knowledge_service` 成功并可运行服务测试。

### 当前结论（2026-04-30）

- `DEV-004` 当前状态：已完成收口，可进入 `DEV-005`。
