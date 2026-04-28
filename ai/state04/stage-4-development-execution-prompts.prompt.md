# Stage 4 Development Execution Prompts

## 文档信息

| 字段 | 内容 |
|---|---|
| 项目 | studyRomm |
| 阶段 | 技术开发阶段 |
| 状态 | 已确认 |
| 版本 | v0.1 |
| 最后更新 | 2026-04-28 |
| 负责人 | AI planning agent |
| 相关文档 | `AGENTS.md`、`ai/superpowers.md`、`ai/stage-gates.md`、`ai/specs.md`、`ai/decisions.md`、`ai/risk-register.md`、`ai/context.md`、`docs/03_design/*.md`、`docs/04_development/implementation-plan.md`、`docs/04_development/task-breakdown.md` |

## 用途

本文件用于在阶段 4 执行 `DEV-001` 到 `DEV-010` 时，为 Agent 提供统一的开发 Prompt。

本文件的核心约束是：

- 先检查门禁，再进入开发。
- 对应阶段的阻塞 TODO 和收口项未闭合时，不允许开始编码。
- 如果当前阶段不满足开发要求，必须优先补齐对应待办工作项，而不是停留在“等待”状态。
- 先测试计划，再实现，再验证，再更新文档。
- 只能实现当前 `DEV` 范围，不得扩 scope。

## 使用方式

1. 先阅读 `AGENTS.md`、`ai/superpowers.md`、`ai/stage-gates.md`、`ai/specs.md`、`ai/decisions.md`、`ai/risk-register.md`。
2. 再阅读 `docs/04_development/implementation-plan.md` 和 `docs/04_development/task-breakdown.md`。
3. 复制“通用执行 Prompt”。
4. 再追加目标 `DEV` 的专用 Prompt。
5. 如果门禁检查未通过，停止编码，只输出缺口、TODO 清理动作和收口建议。
6. 如果门禁检查未通过，优先执行与当前 `DEV` 直接相关的待办工作项补齐动作，例如补文档、补决策、补风险、补契约、补测试计划或补收口清单。

## 通用执行 Prompt

```md
# 请使用 Superpowers 风格执行当前阶段 4 开发任务

你现在处于 `studyRomm` 项目的【阶段 4：技术开发阶段】。

你的目标不是直接开始写代码，而是先确认当前 `DEV` 阶段是否满足进入开发的门禁；只有在阻塞 TODO、阶段收口项、测试计划和文档依赖都闭合后，才可以进入实现。

## 你必须遵守的规则

1. 先读取：
   - `AGENTS.md`
   - `ai/superpowers.md`
   - `ai/stage-gates.md`
   - `ai/specs.md`
   - `ai/decisions.md`
   - `ai/risk-register.md`
   - `ai/context.md`
   - `docs/03_design/*.md`
   - `docs/04_development/implementation-plan.md`
   - `docs/04_development/task-breakdown.md`
2. 先检查当前 `DEV` 的阻塞 TODO、阶段收口项、依赖阶段完成情况。
3. 若任一阻塞项未闭合：
   - 不允许编码
   - 不允许伪造默认值
   - 不允许跳过测试计划
   - 必须优先补齐当前阶段直接相关的待办工作项
   - 只允许输出“缺口清单、影响分析、待办补齐动作、需更新文档”
4. 若门禁通过：
   - 先输出实现计划
   - 先输出测试计划
   - 再进行小步实现
   - 完成后给出验证证据
5. 行为变更必须同步更新文档；关键取舍必须写入 `ai/decisions.md`；风险或未闭合项必须写入 `ai/risk-register.md`。

## 门禁检查格式

请先输出：

### 当前阶段

说明当前处于阶段 4 的哪个 `DEV`。

### 我理解的目标

说明本次开发目标。

### 需要确认的问题

只列真正阻塞的问题；如果没有，明确写“无新增阻塞问题”。

### 我的假设

只写已被文档允许的临时假设。

### 阻塞 TODO 检查

| 检查项 | 来源 | 当前状态 | 是否阻塞 | 处理结论 |
|---|---|---|---|---|
|  |  | 已闭合 / 未闭合 | 是 / 否 | 继续开发 / 停止开发 |

### 收口要求检查

| 收口项 | 最晚闭合时间 | 当前状态 | 是否满足进入开发 |
|---|---|---|---|
|  |  | 已闭合 / 未闭合 | 是 / 否 |

### 开发准入结论

结论只能是以下两种之一：

- 可以进入当前 DEV 开发
- 不可以进入当前 DEV 开发

如果结论为“不可以进入当前 DEV 开发”，立即停止编码，改为输出：

1. 缺口清单
2. 缺口影响
3. 待办补齐优先级
4. 立即执行的补齐动作
5. 建议先更新的文档
6. 建议下一步

如果结论为“可以进入当前 DEV 开发”，继续输出：

1. 实现范围
2. 非目标范围
3. 测试计划
4. 实现步骤
5. 文档更新项
6. 验证证据要求
```

## DEV-001 Prompt

```md
在通过通用执行 Prompt 的门禁检查后，执行 `DEV-001：后端微服务工程基座`。

### 当前目标

建立首批后端工程基座与前端并行起步基座，包括：

- 后端 Maven 多模块父工程
- `gateway`、`auth-service`、`knowledge-service`
- `service-common`、`web-common`、`security-common`、`testing-common`
- 统一 `code/message/traceId`
- 健康检查接口
- 前端壳层、路由骨架、API client、错误结构和任务状态 mock 基座

### 必查阻塞项

| 检查项 | 来源 |
|---|---|
| 用户是否明确批准开始真实实现 | `docs/04_development/implementation-plan.md` 7.5 |
| `DEV-001` 范围是否仍限定为首批三服务与公共基座 | `ai/decisions.md` D-032 |
| 前端主栈是否仍为 Vue 技术栈 | `ai/decisions.md` D-037 |
| 当前是否仍处于“规划收口”而非自动编码状态 | `ai/context.md` |

### 本阶段只做什么

- 工程结构、公共模块、首批服务骨架
- 最小健康检查、错误结构、traceId、测试基座
- 前端工程壳层、路由骨架、API client、mock/stub 基座

### 本阶段不做什么

- 不落业务 schema
- 不落真实权限归属规则
- 不扩展到全部微服务
- 不做完整业务页面

### 必须先定义的测试

- 聚合工程构建测试
- 服务启动与健康检查测试
- 错误结构与 traceId 单元测试
- 前端构建冒烟测试
- API client 与错误拦截集成测试

### 必须更新的文档

- `docs/04_development/coding-notes.md`
- 如有新取舍，更新 `ai/decisions.md`
- 如有新风险，更新 `ai/risk-register.md`
```

## DEV-002 Prompt

```md
在通过通用执行 Prompt 的门禁检查后，执行 `DEV-002：数据模型与建库基座`。

### 当前目标

建立首批服务 schema、Flyway 基座、初始化建库规则和核心数据约束。

### 必查阻塞项

| 检查项 | 来源 |
|---|---|
| `DEV-002` 首批核心表范围是否已确认 | `docs/04_development/task-breakdown.md` `TODO-TASK-001` |
| `DEV-001` 是否已完成并可复用基座 | `docs/04_development/implementation-plan.md` |
| 服务独立 schema/database 约束是否保持不变 | `ai/context.md`、`ai/decisions.md` D-021 |

### 本阶段只做什么

- Flyway 初始化目录、规则、基线 SQL
- 用户/角色、课程树、题目、考试、任务、审计首批主表
- 关键唯一约束、外键替代策略和索引基线

### 本阶段不做什么

- 不一次性建全量业务表
- 不做历史数据迁移
- 不做复杂查询优化

### 必须先定义的测试

- 从空库初始化测试
- 核心约束测试
- 迁移幂等测试
- 首批 schema 冒烟测试

### 必须更新的文档

- `docs/03_design/data-model.md` 如表结构收敛
- `docs/04_development/coding-notes.md`
- `ai/risk-register.md` 复核 R-031、R-032
```

## DEV-003 Prompt

```md
在通过通用执行 Prompt 的门禁检查后，执行 `DEV-003：认证与权限基础能力`。

### 当前目标

落地用户认证、服务身份、角色 + 资源归属 + 流程状态三层权限判定，以及审计基线。

### 必查阻塞项

| 检查项 | 来源 |
|---|---|
| `DEV-002` 核心表是否已可支撑认证与资源归属 | `docs/04_development/implementation-plan.md` |
| 权限模型是否仍以三层判定为准 | `ai/decisions.md` D-025 |
| 前端路由守卫仅作体验层约束是否已明确 | `docs/03_design/security-design.md` |

### 本阶段只做什么

- 登录认证入口
- 服务间身份传递
- 资源归属校验基线
- 审计留痕基线

### 本阶段不做什么

- 不做细粒度运营后台扩展权限
- 不把前端路由守卫当成真实授权

### 必须先定义的测试

- 登录成功/失败测试
- 角色与资源归属测试
- 审计记录测试
- 越权访问冒烟测试

### 必须更新的文档

- `docs/03_design/security-design.md` 如有安全收敛
- `docs/04_development/coding-notes.md`
- `ai/risk-register.md` 复核 R-028
```

## DEV-004 Prompt

```md
在通过通用执行 Prompt 的门禁检查后，执行 `DEV-004：知识库与题库最小闭环`。

### 当前目标

完成课程树、知识点图文内容、题目与审核流的最小后端闭环，并同步支撑学生端页面骨架联调。

### 必查阻塞项

| 检查项 | 来源 |
|---|---|
| 教材/题库/历史真题授权白名单与禁用边界是否已闭合 | `docs/04_development/task-breakdown.md` `TODO-TASK-002` |
| mock/stub 与真实接口版本同步规则是否已明确 | `docs/04_development/task-breakdown.md` `TODO-TASK-006` |
| 题目至少关联 1 个知识点约束是否保持不变 | `ai/specs.md`、`ai/decisions.md` D-010 |

### 本阶段只做什么

- 教材版本、课程树、知识点、图文内容接口
- 题目、答案、解析、审核入口
- 学生端知识库/考试相关页面骨架对接 mock

### 本阶段不做什么

- 不做历史真题全量治理
- 不做复杂推荐和高级检索

### 必须先定义的测试

- 课程树查询测试
- 知识点图文管理测试
- 题目入库与知识点关联校验测试
- 审核流集成测试
- 前端页面集成测试

### 必须更新的文档

- `docs/03_design/api-spec.md` 如接口字段调整
- `docs/03_design/data-model.md` 如实体边界调整
- `docs/04_development/coding-notes.md`
- `ai/risk-register.md` 复核 R-001、R-005、R-018、R-034
```

## DEV-005 Prompt

```md
在通过通用执行 Prompt 的门禁检查后，执行 `DEV-005：规则模板与组卷后端`。

### 当前目标

完成规则模板生命周期、组卷范围解析、混排与降级提示、试卷生成任务，并支撑老师端页面骨架联调。

### 必查阻塞项

| 检查项 | 来源 |
|---|---|
| `DEV-004` 契约冻结与 mock 同步规则是否持续有效 | `docs/04_development/task-breakdown.md` |
| 课程范围五种模式是否保持不变 | `ai/decisions.md` D-030 |
| 模板四态与版本追溯是否保持不变 | `ai/decisions.md` D-027 |

### 本阶段只做什么

- 模板草稿、发布、停用、归档
- 范围解析、混排降级、任务状态查询
- 老师端模板、组卷预览、任务查询页面骨架

### 本阶段不做什么

- 不引入复杂规则引擎
- 不引入 MQ
- 不做高级批量运营能力

### 必须先定义的测试

- 模板状态流转测试
- 范围解析规则测试
- 混排降级测试
- 任务状态与重试信息测试
- 老师端页面集成测试

### 必须更新的文档

- `docs/03_design/api-spec.md`
- `docs/04_development/coding-notes.md`
- `ai/risk-register.md` 复核 R-014、R-019、R-022、R-034
```

## DEV-006 Prompt

```md
在通过通用执行 Prompt 的门禁检查后，执行 `DEV-006：考试与批改主链路`。

### 当前目标

完成考试计划、考试实例、答卷提交、客观题判分、主观题参考答案与步骤解析、复核入口。

### 必查阻塞项

| 检查项 | 来源 |
|---|---|
| 自动批改分题型准确率目标是否已闭合 | `docs/04_development/task-breakdown.md` `TODO-TASK-003` |
| 人工复核触发条件是否已闭合 | `docs/04_development/task-breakdown.md` `TODO-TASK-003` |
| 公共任务状态契约是否保持不变 | `ai/decisions.md` D-026 |

### 本阶段只做什么

- 考试计划、考试实例、交卷
- 客观题自动判分
- 主观题参考答案与步骤展示
- 复核任务入口

### 本阶段不做什么

- 不承诺未定义准确率的主观题全自动判分
- 不扩展到防作弊和智能监考

### 必须先定义的测试

- 交卷测试
- 客观题判分测试
- 主观题步骤输出测试
- 复核触发测试
- 边界题型测试

### 必须更新的文档

- `ai/specs.md` 如验收口径被明确补全
- `docs/03_design/api-spec.md` 如批改返回结构收敛
- `docs/04_development/coding-notes.md`
- `ai/risk-register.md` 复核 R-003、R-011
```

## DEV-007 Prompt

```md
在通过通用执行 Prompt 的门禁检查后，执行 `DEV-007：分析与视频解析闭环`。

### 当前目标

完成成绩分析、错题沉淀、视频草稿生成、人工审核发布和授权播放最小闭环，并支撑管理员端页面骨架联调。

### 必查阻塞项

| 检查项 | 来源 |
|---|---|
| 视频离线缓存是否进入首版及加密策略是否闭合 | `docs/04_development/task-breakdown.md` `TODO-TASK-004` |
| 第三方模型/视频生成能力的首版接入方式是否明确 | `ai/context.md`、`ai/risk-register.md` |
| 未审核视频仅管理员与审核人可见约束是否保持不变 | `ai/decisions.md` D-013 |

### 本阶段只做什么

- 成绩分析、错题记录
- 视频草稿生成、审核、发布、播放授权
- 管理端课程树、题库、视频审核页面骨架

### 本阶段不做什么

- 不做字幕、配音、高清等增强能力
- 不做离线缓存增强，除非已闭合并获批

### 必须先定义的测试

- 分析聚合测试
- 视频状态流转测试
- 审核发布集成测试
- 授权播放与越权测试
- 管理端页面集成测试

### 必须更新的文档

- `docs/03_design/security-design.md` 如资源安全策略收敛
- `docs/03_design/api-spec.md`
- `docs/04_development/coding-notes.md`
- `ai/risk-register.md` 复核 R-012、R-020、R-023、R-027
```

## DEV-008 Prompt

```md
在通过通用执行 Prompt 的门禁检查后，执行 `DEV-008：前端闭环收口与端到端验收`。

### 当前目标

汇合前后端并行成果，完成学生、老师、管理员三角色主链路真实联调、契约差异关闭和端到端验收。

### 必查阻塞项

| 检查项 | 来源 |
|---|---|
| 联调差异清单与统一任务板是否已建立 | `ai/risk-register.md` R-035 TODO |
| mock/stub 与真实接口差异是否已完成逐项收敛 | `docs/04_development/task-breakdown.md` `TASK-025` |
| `DEV-004` 到 `DEV-007` 的可联调接口是否达到冻结版本 | `docs/04_development/implementation-plan.md` |

### 本阶段只做什么

- 学生端真实联调
- 老师端真实联调
- 管理端真实联调
- 契约审计、差异关闭、E2E 验收

### 本阶段不做什么

- 不新增超出主链路的新页面
- 不重写已经冻结的业务范围

### 必须先定义的测试

- 三角色 E2E 测试
- 路由与数据隔离测试
- 任务失败与空数据页面测试
- 契约回归测试

### 必须更新的文档

- `docs/03_design/api-spec.md` 如联调后字段细化
- `docs/04_development/coding-notes.md`
- `ai/risk-register.md` 复核 R-034、R-035
```

## DEV-009 Prompt

```md
在通过通用执行 Prompt 的门禁检查后，执行 `DEV-009：安全、异常与回归补强`。

### 当前目标

补齐越权、异常、幂等、任务失败、审计一致性和跨阶段回归验证。

### 必查阻塞项

| 检查项 | 来源 |
|---|---|
| 异步任务容量、重试、超时策略是否已闭合 | `ai/risk-register.md` TODO |
| 资源归属初始化、变更审批和巡检机制是否已闭合 | `ai/risk-register.md` TODO |
| `DEV-008` 三角色主链路是否已完成联调 | `docs/04_development/implementation-plan.md` |

### 本阶段只做什么

- 越权回归
- 统一错误结构与异常路径补强
- 幂等、重试、审计一致性补强

### 本阶段不做什么

- 不引入全新业务功能

### 必须先定义的测试

- 越权访问题库/考试/视频测试
- 幂等与重复提交测试
- 任务失败信息测试
- 审计链路回归测试
- 全主链路异常回归测试

### 必须更新的文档

- `docs/03_design/security-design.md`
- `docs/03_design/api-spec.md`
- `docs/04_development/coding-notes.md`
- `ai/risk-register.md` 复核 R-022、R-025、R-026、R-028
```

## DEV-010 Prompt

```md
在通过通用执行 Prompt 的门禁检查后，执行 `DEV-010：可观测性与发布准备`。

### 当前目标

完成指标、日志、审计、发布清单、回滚策略和阶段 5 交接资料。

### 必查阻塞项

| 检查项 | 来源 |
|---|---|
| 运维基线、TLS/密钥管理、服务契约回归策略是否已闭合 | `docs/04_development/task-breakdown.md` `TODO-TASK-005` |
| 本地化部署演进里程碑、RPO/RTO、备份窗口是否已明确 | `ai/risk-register.md` TODO |
| 核心链路最终回归范围是否已确定 | `docs/04_development/test-strategy.md` |

### 本阶段只做什么

- 关键观测字段与日志基线
- 发布与回滚清单
- 已知问题分级
- 阶段 5 交接资料

### 本阶段不做什么

- 不扩展新业务能力
- 不把阶段 5 测试验证提前写成“已完成”

### 必须先定义的测试

- 审计查询权限测试
- 观测字段完整性测试
- 核心链路最终回归
- 发布清单人工抽检

### 必须更新的文档

- `docs/04_development/coding-notes.md`
- `ai/context.md`
- `ai/risk-register.md`
- 阶段 5 所需交接文档
```

## 阶段 4 统一阻塞 TODO 索引

| TODO | 说明 | 最晚闭合时间 | 阻塞的 DEV |
|---|---|---|---|
| `TODO-TASK-001` | `DEV-002` 首批核心表范围 | 进入 `DEV-002` 前 | `DEV-002` |
| `TODO-TASK-002` | 授权白名单与禁用边界 | 进入 `DEV-004` 前 | `DEV-004` 至 `DEV-007` |
| `TODO-TASK-003` | 自动批改准确率与人工复核阈值 | 进入 `DEV-006` 前 | `DEV-006` |
| `TODO-TASK-004` | 视频离线缓存与加密策略 | 进入 `DEV-007` 前 | `DEV-007` |
| `TODO-TASK-005` | 运维基线、TLS/密钥管理、契约回归策略 | 进入 `DEV-010` 前 | `DEV-010` |
| `TODO-TASK-006` | 前端 mock/stub 与真实接口同步规则 | 进入 `DEV-004` 前 | `DEV-004`、`DEV-005`、`DEV-008` |

## 输出要求

每次使用本文件中的 Prompt 时，最终回复必须包含：

1. 当前阶段
2. 我理解的目标
3. 需要确认的问题
4. 我的假设
5. 阻塞 TODO 检查
6. 收口要求检查
7. 开发准入结论
8. 如果准入通过：实现计划、测试计划、文档更新项、验证方式
9. 如果准入不通过：缺口清单、影响分析、建议先更新的文档、建议下一步

当准入不通过时，还必须满足：

- 不只说明“不能开发”，而是给出当前应先做的待办补齐顺序。
- 优先补齐与当前 `DEV` 直接相关、且能解除门禁的待办工作项。
- 若可在当前回合补齐文档、决策、风险、契约或测试计划，应直接补齐，不要仅口头建议。

## 禁止事项

- 未通过门禁时开始编码
- 把未闭合 TODO 当成默认已确认
- 不写测试计划直接实现
- 不更新文档直接宣称完成
- 以“已有代码”替代门禁检查
- 擅自扩大当前 `DEV` 范围
- 门禁不通过时只汇报问题、不推进待办补齐
