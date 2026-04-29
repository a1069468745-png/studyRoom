# 项目上下文

## 文档信息

| 字段 | 内容 |
|---|---|
| 项目 | studyRomm |
| 阶段 | 技术开发阶段 |
| 状态 | 已更新 |
| 版本 | v0.5 |
| 最后更新 | 2026-04-29 |
| 相关来源 | `AGENTS.md`、`ai/rules.md`、`ai/superpowers.md`、`ai/question-bank.md`、`ai/stage-gates.md`、`ai/templates.md`、`ai/specs.md`、`ai/decisions.md`、`docs/02_product/*.md`、`docs/03_design/*.md`、`docs/04_development/*.md` |

## 当前理解

当前项目是一个面向学生、老师和管理员的内部知识库与考试系统，目标是覆盖教材版本下的课程树、题库、组卷、考试、批改、分析和视频解析最小闭环。

当前处于阶段 4：技术开发阶段。阶段 4 的执行口径已调整为前后端并行，不再采用“后端全部完成后再统一做前端联调”的串行方式。当前已完成 `DEV-001`、`DEV-002`、`DEV-003` 的基线实现与验证，并已进入 `DEV-004`，完成 `DEV-004-A`（课程树与教材版本接口）。

## 已确认事实

- 目标用户是学生、老师、管理员。
- 这是内部工具。
- 当前替代方案是纯手工。
- 首版优先覆盖中学语数英数理化。
- 首版教材版本先覆盖人教版，其他版本后置但支持人工导入。
- 默认知识结构采用教材版本下的课程树，结构为“学段 -> 年级 -> 学科 -> 单元 -> 章节 -> 知识点”。
- 题目必须至少关联 1 个知识点。
- 规则模板必须支持草稿、发布、停用、归档和版本追溯。
- 视频解析首版只验收“草稿生成 + 人工审核 + 学生播放”的最小闭环。
- 权限模型采用“角色 + 资源归属 + 流程状态”三层判定。
- 技术主栈采用 Java 21 + Spring Boot 3 + Spring Cloud Alibaba 2023.x。
- 前端主栈已确认采用 Vue + TypeScript + Vite + Vue Router + Pinia + Axios + Element Plus。
- 数据所有权按服务拆分，逻辑上先按服务独立 schema/database 管理。
- 异步任务首版采用数据库任务表 + 后台 worker，不引入 MQ。
- 当前阶段 4 的权威规划文档位于 `docs/04_development/`。
- 当前阶段 4 的统一执行 Prompt 位于 `ai/state04/stage-4-development-execution-prompts.prompt.md`，用于约束每个 `DEV` 的开发准入检查。
- 当前仓库的项目执行约束文件位于根目录 `AGENTS.md`。
- 当前仓库与会话环境未定义项目级 `agent-teams` 机制，阶段 4 默认采用多个 agent 人工编排协作。

## 当前阶段结论

- 阶段 4 规划文档已按 `ai/state04/stage-4-development-planning.prompt.md` 重构。
- 当前应以 `docs/04_development/implementation-plan.md` 作为阶段 4 权威口径。
- 当前阶段 4 的执行方式已收敛为“后端泳道 + 前端泳道 + 契约治理泳道”并行推进。
- 仓库中即使已存在 `backend/` 目录或早期实现，也只代表“已有实现现状”，不自动代表阶段门禁结论。
- `DEV-001`、`DEV-002`、`DEV-003` 已完成当前轮次收口验证；`DEV-004` 已开始并完成 `DEV-004-A`。

## 待确认事项

- 自动批改分题型准确率和人工复核触发阈值。
- 视频离线缓存是否进入首版以及加密策略。
- 第三方模型/视频生成能力的首版接入方式。
- 微服务注册中心、配置中心、链路追踪和运维基线的最终落地方式。

## 阶段 4 收口清单

- 进入 `DEV-001` 前：用户已明确批准开始真实实现，当前已进入实施。
- 进入 `DEV-002` 前：DEV-002 的首批核心表范围与初始化建库边界已明确，可进入实施。
- 进入 `DEV-004` 前：授权白名单与合规边界已闭合（D-042）。
- 进入 `DEV-006` 前：明确自动批改质量阈值与人工复核触发条件。
- 进入 `DEV-007` 前：明确视频离线缓存与加密策略。
- 进入 `DEV-010` 前：明确微服务运维基线与契约回归策略。

## Stage 4 Planning Addendum

- 当前已进入阶段 4：技术开发阶段。
- 当前阶段 4 的主要任务已从“输出完整开发规划”切换到 `DEV-001` 基座实现与 `DEV-002` 建库基座落地。
- `DEV-001` 范围已确认：首批只规划并准备 `gateway`、`auth-service`、`knowledge-service` 与公共基座。
- 前端自 `DEV-001` 起即可基于 API 契约和 mock/stub 并行推进，`DEV-008` 只负责真实接口闭环收口。
- 每个 `DEV` 在进入真实开发前，都必须先完成阻塞 TODO 和收口要求检查；未闭合时只能继续收口，不得编码。
- 如果当前 `DEV` 不满足开发要求，当前回合应优先补齐直接相关的待办工作项、文档缺口和门禁缺口，而不是只停留在状态说明。
- 当前阶段 4 已完成 `DEV-001`、`DEV-002`、`DEV-003` 收口验证，并已进入 `DEV-004`。
- `DEV-001` 后端基座已补齐统一任务状态契约与最小测试基座，并已再次执行后端聚合 `mvn test` 通过。
- `DEV-001` 前端工作区已完成依赖安装、`build`、`vitest` 和开发服务器冒烟验证，可作为后续阶段的前端基座复用。
- `DEV-002` 的首批核心表范围已收敛为“首批核心主表 + Flyway 基座”，对应决策见 D-040。
- `DEV-002` 已在 `auth-service`、`knowledge-service` 中引入 Flyway、JDBC 与 MySQL/MariaDB 接入配置，并新增 `V1` 基线迁移脚本。
- 数据库执行口径已统一为 MariaDB（MySQL 协议兼容）；开发、联调、测试与迁移验证不再使用 H2。
- 本机当前可使用 `root/123456` 跑通 MariaDB，后续安全收口需切换为专用应用账号（建议 `studyromm_app`）并收敛权限。
- `DEV-002` 当前已验证 `auth_service`、`knowledge_service`、`question_service`、`exam_service`、`job_service` 五组 schema / table 初始化与基础约束可通过后端聚合测试。
- 当前 `frontend/packages/shared/tsconfig.json` 存在本地 `ignoreDeprecations` 配置，导致 `typecheck` 失败；该问题不阻塞 `DEV-002` 的数据建模与建库准入，但应在前端后续回归中修复。
- `DEV-004-A` 已落地 `GET /api/common/dictionaries`、`GET /api/common/dictionaries/textbook-versions`、`GET /api/common/dictionaries/curriculum-nodes`，并通过 `knowledge-service` 集成测试。
