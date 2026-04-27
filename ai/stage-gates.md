# 阶段门禁判断

## 1. 目的

本文件定义项目每个阶段的进入条件和退出条件。

Agent 不得跳过阶段门禁。
如果门禁条件未满足，必须停下来说明缺口，而不是继续执行。

## 当前状态

- Gate 1 已完成。
- Gate 2 已完成。
- 需求调研阶段和产品定义阶段产物已生成并更新。
- 当前可以进入方案设计阶段。

## Gate 1：需求调研 -> 产品定义

### 必须满足

- 已明确目标用户
- 已明确核心问题
- 已明确主要使用场景
- 已明确当前替代方案
- 已明确主要痛点
- 已记录关键假设
- 已记录待验证问题

### 必须存在的文档

- `docs/01_research/research-plan.md`
- `docs/01_research/pain-points.md`
- `docs/00_project/assumptions.md`

### 不允许进入下一阶段的情况

- 用户是谁不清楚
- 问题是否真实存在不清楚
- 目标和方案混在一起
- 需求里充满未经标记的假设

## Gate 2：产品定义 -> 方案设计

### 必须满足

- MVP 范围清晰
- 非目标范围清晰
- 每个功能都有验收标准
- 每个用户故事有角色、目标、收益
- 指标体系清晰
- `specs.md` 已更新

### 必须存在的文档

- `docs/02_product/prd.md`
- `docs/02_product/user-stories.md`
- `docs/02_product/acceptance-criteria.md`
- `docs/02_product/metrics.md`
- `ai/specs.md`

### 不允许进入下一阶段的情况

- 功能范围持续膨胀
- 没有验收标准
- 没有明确不做什么
- `PRD` 和 `specs.md` 不一致

## Gate 3：方案设计 -> 技术开发

### 必须满足

- 架构方案清晰
- 数据模型清晰
- API 规格清晰
- 权限模型清晰
- 错误处理策略清晰
- 关键技术决策已记录
- 主要风险已登记

### 必须存在的文档

- `docs/03_design/architecture.md`
- `docs/03_design/data-model.md`
- `docs/03_design/api-spec.md`
- `ai/decisions.md`
- `ai/risk-register.md`

### 不允许进入下一阶段的情况

- 没有数据模型就开始写代码
- 没有 API 规格就开始写前后端
- 没有 decisions.md 就引入重大技术选型
- 没有说明风险就进入实现

## Gate 4：技术开发 -> 测试验证

### 必须满足

- 已完成计划中的开发任务
- 已编写测试
- 测试已运行
- 代码符合 `specs.md`
- 文档已同步更新
- 关键变更已记录到 `decisions.md`

### 必须存在的文档

- `docs/04_development/implementation-plan.md`
- `docs/04_development/task-breakdown.md`
- 测试文件
- 更新后的 `ai/specs.md`

### 不允许进入下一阶段的情况

- 只实现代码，没有测试
- 只口头说完成，没有验证结果
- 改了接口但没更新 `api-spec.md`
- 改了数据结构但没更新 `data-model.md`

## Gate 5：测试验证 -> 质量审计

### 必须满足

- 核心路径测试通过
- 异常路径测试通过
- 回归测试通过
- P0/P1 缺陷已解决
- 已知问题已分级
- 无法自动化的部分已有人工验证步骤

### 必须存在的文档

- `docs/05_testing/test-plan.md`
- `docs/05_testing/test-cases.md`
- `docs/05_testing/regression-checklist.md`
- `docs/05_testing/verification-report.md`

### 不允许进入下一阶段的情况

- 测试覆盖和验收标准不对应
- 阻塞问题未解决
- 没有验证证据
- bug 修复没有回归测试

## Gate 6：质量审计 -> 发布

### 必须满足

- 需求一致性审查完成
- 代码质量审查完成
- 安全审查完成
- 性能审查完成
- 发布清单完成
- 回滚方案清晰
- 发布结论清晰

### 必须存在的文档

- `docs/06_quality/quality-audit.md`
- `docs/06_quality/security-review.md`
- `docs/06_quality/performance-review.md`
- `docs/06_quality/release-checklist.md`
- `docs/06_quality/release-decision.md`

### 发布结论

只能是以下三种之一：

- 可以发布
- 有条件发布
- 暂缓发布
