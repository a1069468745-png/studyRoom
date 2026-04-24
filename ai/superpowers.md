# superpowers.md

## 1. 目标

本项目使用 Superpowers 风格的 Agent 工作流，要求 AI Agent 不直接进入编码，而是遵循：

1. Brainstorming：先追问，澄清目标、用户、约束和风险
2. Specification：形成可验证的规格说明
3. Design Review：分块确认设计
4. Writing Plans：生成可执行的任务计划
5. Test-Driven Development：先测试，再实现
6. Code Review：实现后进行规格一致性和代码质量审查
7. Verification：用证据证明完成，而不是口头声明完成

## 2. 总原则

Agent 必须遵守：

- 不允许在需求未澄清时直接写代码。
- 不允许在 specs.md 未更新时开始实现。
- 不允许在 decisions.md 未记录关键取舍时引入重要技术方案。
- 不允许跳过测试。
- 不允许只说“已完成”，必须提供验证证据。
- 不允许扩大需求范围。
- 不允许为了完成任务而引入不必要复杂度。
- 优先选择简单方案，遵守 YAGNI。
- 避免重复实现，遵守 DRY。
- 所有关键功能必须有验收标准。

## 3. 阶段与 Superpowers 对应关系

| 项目阶段     | Superpowers 风格能力        | 主要产物                                                  |
| ------------ | --------------------------- | --------------------------------------------------------- |
| 需求调研阶段 | Brainstorming               | research-plan.md, assumptions.md, pain-points.md          |
| 产品定义阶段 | Specification               | prd.md, user-stories.md, acceptance-criteria.md, specs.md |
| 方案设计阶段 | Design Review, Decisions    | architecture.md, api-spec.md, data-model.md, decisions.md |
| 技术开发阶段 | Writing Plans, TDD          | implementation-plan.md, task-breakdown.md, tests          |
| 测试验证阶段 | Verification, Debugging     | test-plan.md, regression-checklist.md, bug-reports.md     |
| 质量审计阶段 | Code Review, Release Review | quality-audit.md, release-checklist.md                    |

## 4. Agent 执行格式

每次处理任务时，Agent 必须按以下结构输出：

### 1. 我理解的目标

说明当前任务要解决什么问题。

### 2. 我需要确认的问题

列出必须澄清的问题。

### 3. 当前假设

如果信息不足，列出临时假设。

### 4. 推荐方案

给出 1-3 个方案，并说明优缺点。

### 5. 建议产出的文档

列出需要创建或更新的 Markdown 文件。

### 6. 下一步动作

说明是否可以进入下一阶段。