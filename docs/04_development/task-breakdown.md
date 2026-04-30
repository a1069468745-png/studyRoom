# 技术开发任务拆解

## 文档信息

| 字段 | 内容 |
|---|---|
| 项目 | studyRomm |
| 阶段 | 技术开发阶段 |
| 状态 | 已确认 |
| 版本 | v0.5 |
| 最后更新 | 2026-04-30 |
| 负责人 | AI planning agent |
| 相关文档 | docs/04_development/implementation-plan.md |

## 开发任务拆解

| 任务 ID | 所属阶段 | 任务名称 | 输入 | 输出 | 依赖 | 验证方式 |
|---|---|---|---|---|---|---|
| TASK-001 | DEV-001 | 建立后端父工程与模块结构 | 决策文档、架构文档 | Maven 聚合工程 | 无 | 聚合构建通过 |
| TASK-002 | DEV-001 | 建立公共模块基座 | API 规格、安全设计 | 公共错误、traceId、测试基座 | TASK-001 | 单元/集成测试 |
| TASK-003 | DEV-001 | 建立首批服务骨架 | 架构设计 | gateway/auth/knowledge 骨架 | TASK-001、TASK-002 | 启动测试 |
| TASK-020 | DEV-001 | 建立前端工程壳层与路由骨架 | 用户故事、PRD | 客户端/管理端工程壳、基础路由、权限壳 | 无 | 前端构建与页面冒烟测试 |
| TASK-021 | DEV-001 | 建立 API client、错误结构与任务状态 mock 基座 | API 规格、决策文档 | 接口调用层、统一错误处理、任务状态 mock/stub | TASK-002、TASK-020 | 契约测试与页面集成测试 |
| TASK-004 | DEV-002 | 建立 Flyway 建库基座 | 数据模型、决策 | 初始化目录、命名规则、基线 SQL | DEV-001 | 从空库初始化成功 |
| TASK-005 | DEV-002 | 落首批核心表结构 | data-model.md | 用户/课程树/题目/考试/任务/审计主表 | TASK-004 | 约束测试 |
| TASK-006 | DEV-003 | 落统一认证与服务身份 | security-design.md | 认证入口、服务身份方案 | DEV-002 | 认证测试 |
| TASK-007 | DEV-003 | 落资源归属与审计基线 | security-design.md、decisions.md | 资源归属校验、审计留痕 | TASK-006 | 权限与审计测试 |
| TASK-008 | DEV-004 | 建立课程树与知识库接口 | PRD、API 规格 | 教材版本、课程树、图文内容接口 | DEV-003 | 集成测试 |
| TASK-009 | DEV-004 | 建立题库与审核入口 | PRD、数据模型 | 题目、答案、解析、审核接口 | TASK-008 | 入库校验测试 |
| TASK-022 | DEV-004 | 并行搭建学生端知识库与考试页面骨架 | 用户故事、API 规格 | 学习页、考试页、结果页骨架与 mock 联调 | TASK-021、TASK-008 | 页面集成测试 |
| TASK-010 | DEV-005 | 建立模板生命周期治理 | PRD、验收标准 | 模板草稿/发布/停用/归档/回滚 | DEV-004 | 状态流转测试 |
| TASK-011 | DEV-005 | 建立范围解析与组卷任务 | API 规格、决策 | 组卷范围解析、混排与降级提示 | TASK-010 | 组卷测试 |
| TASK-023 | DEV-005 | 并行搭建老师端模板与组卷页面骨架 | 用户故事、API 规格 | 规则模板、组卷预览、任务查询页面骨架 | TASK-021、TASK-010、TASK-011 | 页面集成测试 |
| TASK-012 | DEV-006 | 建立考试计划与目标范围 | 用户故事、数据模型 | 考试计划、考试实例 | DEV-005 | 集成测试 |
| TASK-013 | DEV-006 | 建立提交、判分与步骤解析 | 验收标准 E | 答卷提交、判分、步骤展示 | TASK-012 | 提交与判分测试 |
| TASK-014 | DEV-007 | 建立分析与错题链路 | metrics.md、user-stories.md | 分析报告、错题记录 | DEV-006 | 分析任务测试 |
| TASK-015 | DEV-007 | 建立视频草稿与审核发布 | PRD、验收标准 F | 草稿、审核、发布、播放授权 | TASK-013、TASK-014 | 权限与播放测试 |
| TASK-024 | DEV-007 | 并行搭建管理员端课程树、题库与视频审核页面骨架 | 用户故事、API 规格 | 管理端主链路页面骨架与 mock 联调 | TASK-021、TASK-009、TASK-015 | 页面集成测试 |
| TASK-016 | DEV-008 | 串联学生端主链路真实接口 | 用户故事、API 规格 | 学习、考试、成绩、视频页面真实联调 | TASK-022、TASK-013、TASK-015 | E2E |
| TASK-017 | DEV-008 | 串联老师与管理员主链路真实接口 | 用户故事、API 规格 | 组卷、审核、管理页面真实联调 | TASK-023、TASK-024、TASK-011、TASK-015 | E2E |
| TASK-025 | DEV-008 | 执行前后端契约审计与差异收口 | API 规格、测试策略、风险文档 | 差异清单、mock 同步说明、联调结论 | TASK-016、TASK-017 | 契约回归与联调审计 |
| TASK-018 | DEV-009 | 补越权、异常、幂等回归 | 风险文档 | 安全与异常补强 | DEV-008 | 回归测试 |
| TASK-019 | DEV-010 | 整理观测、发布、回滚材料 | metrics.md、stage-gates.md | 发布清单与交接材料 | DEV-009 | 门禁检查 |

## 分阶段最小交付物

| 阶段 | 最小交付物 |
|---|---|
| DEV-001 | 工程结构、公共模块、首批服务骨架、基础测试；前端工程壳层、路由骨架、API client 与 mock 基座 |
| DEV-002 | Flyway 建库基座、首批核心表、初始化规则 |
| DEV-003 | 认证、资源归属、审计基线 |
| DEV-004 | 课程树、知识点图文、题目与审核入口；学生端学习/考试页面骨架 |
| DEV-005 | 模板治理、范围解析、试卷生成任务；老师端组卷页面骨架 |
| DEV-006 | 考试计划、交卷、判分、步骤展示 |
| DEV-007 | 分析报告、错题、视频草稿审核发布播放；管理员端审核页面骨架 |
| DEV-008 | 三角色前端主链路真实联调、契约审计与 E2E 验收 |
| DEV-009 | 越权、异常、幂等与回归补强 |
| DEV-010 | 可观测性、发布清单、回滚方案 |

## TODO

| ID | 问题 | 类型 | 优先级 | 负责人 | 状态 |
|---|---|---|---|---|---|
| TODO-TASK-001 | DEV-002 首批核心表范围已按“最小主表集合”收敛：`auth` 侧为 `user`、`role`、`user_role`、`class_room`、`class_membership`、`teaching_assignment`、`resource_owner_scope`、`audit_log`；`knowledge` 侧为 `textbook_version`、`curriculum_node`、`content_asset`；首批业务主表为 `question`、`question_option`、`question_answer`、`question_analysis`、`question_knowledge`、`question_curriculum_node`、`exam_plan`、`exam_plan_target`、`exam_session`、`exam_submission`、`job_task` | 技术 | P0 | AI planning agent | 已处理 |
| TODO-TASK-002 | 已明确教材/题库/历史真题的授权白名单与禁用边界（依据 D-042） | 合规 | P0 | AI planning agent | 已处理 |
| TODO-TASK-003 | 明确自动批改分题型准确率目标与人工复核触发条件 | 产品/质量 | P0 | 用户 | 未处理 |
| TODO-TASK-004 | 明确视频离线缓存是否进入首版及对应加密策略 | 产品/安全 | P1 | 用户 | 未处理 |
| TODO-TASK-005 | 明确微服务运维基线、TLS/密钥管理与服务契约回归策略 | 架构/运维 | P1 | AI planning agent | 未处理 |
| TODO-TASK-006 | 已明确前端 mock/stub 与真实接口的版本同步规则（基于契约版本号、差异清单和回归触发） | 测试/协作 | P1 | AI planning agent | 已处理 |

## TODO-TASK-006 收口结果（mock/stub 与真实接口版本同步规则）

### 1. 统一版本口径

- 以后端 `docs/03_design/api-spec.md` 的契约版本为唯一基线；mock/stub 必须显式标注同一版本号。
- 每次接口字段、错误码、任务状态结构变更时，后端提交必须包含版本变更记录与影响范围说明。

### 2. 变更同步时序

- 第一步：后端更新契约与变更说明（接口路径、字段、错误结构、影响页面）。
- 第二步：前端在同一轮次更新 `shared` 类型、API client 与 mock fixture。
- 第三步：QA/契约泳道更新差异清单并执行契约回归。
- 任一步未完成时，不得标记“可联调”。

### 3. 差异清单与责任归属

- 差异清单由 QA/契约角色维护，至少包含：接口名、契约版本、mock 状态、真实接口状态、差异描述、责任人、截止时间、验证结果。
- 后端责任人：确保真实接口行为与契约一致，并提供回归证据。
- 前端责任人：确保 mock/stub 与 API client 同步，并提供页面集成验证证据。
- 协调角色：每个 DEV 收口前复核“差异清单清零”。

### 4. 强制回归触发条件

- 以下任一变更都会强制触发契约回归和页面冒烟：
- API 入参/出参字段变化。
- 错误码、错误结构、任务状态枚举变化。
- 权限可见性与资源归属判断变化。

### 5. 门禁约束

- `DEV-004` 到 `DEV-008` 期间，若存在“契约版本不一致”或“差异清单未关闭”项，则对应联调任务不得宣称完成。

## 阶段 4 收口要求

| 收口项 | 对应任务/阶段 | 最晚闭合时间 |
|---|---|---|
| `DEV-002` 首批核心表范围 | TASK-004、TASK-005 | 进入 `DEV-002` 前 |
| 契约冻结与 mock 同步规则 | TASK-021、TASK-025 | 进入 `DEV-004` 前 |
| 授权白名单与禁用边界 | TASK-008 至 TASK-015 | 进入 `DEV-004` 前 |
| 自动批改准确率与人工复核阈值 | TASK-013 | 进入 `DEV-006` 前 |
| 视频离线缓存与加密策略 | TASK-015 | 进入 `DEV-007` 前 |
| 运维基线与服务契约回归策略 | TASK-019 | 进入 `DEV-010` 前 |

## DEV-004 执行缓存待办（避免重复全量扫描）

说明：本清单用于汇总 `DEV-004` 的“已完成/未完成/待补证据”，后续可直接更新此处而不必全量扫描 `ai/` 与 `docs/`。

| ID | 工作项 | 来源 | 当前状态 | 下一步动作 |
|---|---|---|---|---|
| DEV4-TODO-001 | TASK-008：课程树与知识库接口（A/B） | implementation-plan.md、coding-notes.md | 已完成（有测试证据） | 将对应用例映射回 `DEV-004` 完成标准清单 |
| DEV4-TODO-002 | TASK-009：题库录入与审核入口（C/D + 客户端题目查询） | implementation-plan.md、coding-notes.md | 已完成（有测试证据） | 补充权限边界与异常路径回归汇总 |
| DEV4-TODO-003 | TASK-022：学生端学习/考试/结果页骨架与 mock 联调 | task-breakdown.md、coding-notes.md | 已完成（前端构建与 dev 冒烟） | 补充页面集成测试证据链接 |
| DEV4-TODO-004 | DEV-004 单元测试项：课程树排序、题目知识点绑定 | implementation-plan.md 第 9 节 | 已处理（计划与证据已同步） | 证据见 `coding-notes.md`，勾选见 `implementation-plan.md` |
| DEV4-TODO-005 | DEV-004 集成测试项：图文查询、题目录入与审核 | implementation-plan.md 第 9 节 | 已处理（计划与证据已同步） | 证据见 `coding-notes.md`，勾选见 `implementation-plan.md` |
| DEV4-TODO-006 | DEV-004 权限/异常/边界/回归/人工验证项 | implementation-plan.md 第 9 节 | 已处理（证据已归档） | 证据见 `docs/04_development/coding-notes.md`“DEV-004 收口追加验证（2026-04-30）” |
| DEV4-TODO-007 | DEV-004 完成标准 7 项（第 10 节） | implementation-plan.md 第 10 节 | 已处理（全部闭合） | 勾选状态与证据已回填 |
| DEV4-TODO-008 | DEV-004 验证证据要求 3 项（第 12 节） | implementation-plan.md 第 12 节 | 已处理（证据已补齐） | 见 `coding-notes.md`“DEV-004 收口追加验证（2026-04-30）” |
| DEV4-TODO-009 | 文档状态冲突收口：`context/stage-gates` 与 `coding-notes` 对 DEV-004 完成度不一致 | ai/context.md、ai/stage-gates.md、coding-notes.md | 已处理（口径已统一） | `context.md` 与 `stage-gates.md` 已同步到 DEV-004 收口状态 |
| DEV4-TODO-010 | P0 安全待办：数据库账号从 `root/123456` 收口到专用最小权限账号 | coding-notes.md TODO-CODE-005 | 已处理（本地已切换并验证） | 脚本见 `backend/ops/sql/dev-db-least-privilege.sql`，验证见 `coding-notes.md` |

## 真实前后端联调证据并行跟踪（新增）

说明：以下任务独立于“阶段是否完成”的文档勾选，强制以可复核证据闭环，避免出现“文档闭合但联调证据弱”。

| ID | 并行证据任务 | 对应阶段 | 证据标准 | 状态 |
|---|---|---|---|---|
| EVID-001 | 学生端学习页真实接口联调证据 | DEV-005 起并行，DEV-008 前闭合 | `VITE_USE_MOCK=false` 条件下，`/client/learning` 调用 `curriculum-nodes`、`content-assets`、`client/questions` 成功；保留请求/响应样例与页面截图 | 未处理 |
| EVID-002 | 学生端考试与结果页真实接口联调证据 | DEV-006 起并行，DEV-008 前闭合 | `/client/exam` 提交、任务状态更新、`/client/result` 展示链路真实可用；保留请求日志与页面截图 | 未处理 |
| EVID-003 | 管理端课程树/图文/题库/审核真实联调证据 | DEV-005 起并行，DEV-008 前闭合 | 管理端关键写操作与审核流转在真实后端成功；保留操作步骤、请求样例、返回结果 | 未处理 |
| EVID-004 | 契约差异清单持续清零证明 | DEV-005~DEV-008 | 每次接口变更同步更新 API client 与 mock；差异清单项必须有关闭时间、责任人、验证结果 | 未处理 |
| EVID-005 | 联调环境基线快照 | DEV-005~DEV-008 | 固定记录后端服务版本、前端提交版本、DB 版本（`D:\\mariadb10.6`）、环境变量摘要（脱敏） | 未处理 |

## MOD 粒度拆分建议（新增）

结论：需要继续拆分，但只拆 `DEV-005` 到 `DEV-008` 当前要动的 MOD，避免一次性全量重构。

| 新待办 ID | 原 MOD | 建议子任务粒度 |
|---|---|---|
| MOD5-SPLIT-001 | MOD-005（规则模板治理） | 模板状态流转、版本快照、回滚、权限边界四个独立子任务 |
| MOD5-SPLIT-002 | MOD-006（范围解析与组卷） | 范围解析、配比与平衡、降级提示、异步任务查询四个独立子任务 |
| MOD8-SPLIT-001 | MOD-012（前端双端联调） | 学生端、老师端、管理端、契约差异清单四条并行泳道 |
