# 技术开发任务拆解

## 文档信息

| 字段 | 内容 |
|---|---|
| 项目 | studyRomm |
| 阶段 | 技术开发阶段 |
| 状态 | 已确认 |
| 版本 | v0.3 |
| 最后更新 | 2026-04-28 |
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
| TODO-TASK-002 | 明确教材/题库/历史真题的授权白名单与禁用边界 | 合规 | P0 | 用户 | 未处理 |
| TODO-TASK-003 | 明确自动批改分题型准确率目标与人工复核触发条件 | 产品/质量 | P0 | 用户 | 未处理 |
| TODO-TASK-004 | 明确视频离线缓存是否进入首版及对应加密策略 | 产品/安全 | P1 | 用户 | 未处理 |
| TODO-TASK-005 | 明确微服务运维基线、TLS/密钥管理与服务契约回归策略 | 架构/运维 | P1 | AI planning agent | 未处理 |
| TODO-TASK-006 | 明确前端 mock/stub 与真实接口的版本同步规则 | 测试/协作 | P1 | AI planning agent | 未处理 |

## 阶段 4 收口要求

| 收口项 | 对应任务/阶段 | 最晚闭合时间 |
|---|---|---|
| `DEV-002` 首批核心表范围 | TASK-004、TASK-005 | 进入 `DEV-002` 前 |
| 契约冻结与 mock 同步规则 | TASK-021、TASK-025 | 进入 `DEV-004` 前 |
| 授权白名单与禁用边界 | TASK-008 至 TASK-015 | 进入 `DEV-004` 前 |
| 自动批改准确率与人工复核阈值 | TASK-013 | 进入 `DEV-006` 前 |
| 视频离线缓存与加密策略 | TASK-015 | 进入 `DEV-007` 前 |
| 运维基线与服务契约回归策略 | TASK-019 | 进入 `DEV-010` 前 |
