# 工作流定义

## 1. 目的

定义本项目 6 个阶段的标准执行步骤，确保与 `ai/AGENTS.md`、`ai/superpowers.md`、`ai/stage-gates.md` 一致。

## 2. 全局规则

1. 每次进入新阶段，先声明阶段目标与本次边界。
2. 阶段产物未完成或门禁未满足，不得跳到下一阶段。
3. 关键决策先写 `ai/decisions.md`，再推进设计或实现。
4. 风险先写 `ai/risk-register.md`，并标注优先级与缓解动作。

## 3. 阶段流程

### 阶段 1：需求调研（Brainstorming）

1. 读取：`ai/question-bank.md`（阶段 1）与已有调研文档。
2. 澄清：目标用户、核心问题、场景、替代方案、约束。
3. 产出：`docs/01_research/*`、`docs/00_project/assumptions.md`、`ai/context.md`。
4. 门禁：满足 Gate 1 后进入阶段 2。

### 阶段 2：产品定义（Specification）

1. 读取：调研产物、`ai/specs.md`（现状）。
2. 澄清：MVP 范围、非目标范围、验收标准、指标。
3. 产出：`docs/02_product/*`、更新 `ai/specs.md`。
4. 门禁：满足 Gate 2 后进入阶段 3。

### 阶段 3：方案设计（Design Review + Decisions）

1. 读取：`ai/specs.md`、`ai/decisions.md`、`ai/risk-register.md`。
2. 澄清：架构、数据模型、API、权限模型、错误处理。
3. 决策：P0/P1 核心取舍写入 `ai/decisions.md`。
4. 产出：`docs/03_design/*`、更新 `ai/risk-register.md`。
5. 门禁：满足 Gate 3 后进入阶段 4。

### 阶段 4：技术开发（Writing Plans + TDD）

1. 读取：`ai/specs.md`、`ai/decisions.md`、设计文档。
2. 产出：实现计划与任务拆分；先测后实现，小步提交变更。
3. 同步：行为变更同步文档，关键取舍补充到 `ai/decisions.md`。
4. 门禁：满足 Gate 4 后进入阶段 5。

### 阶段 5：测试验证（Verification）

1. 建立测试计划与覆盖矩阵，对齐验收标准。
2. 执行核心路径、异常路径、回归测试。
3. 记录证据与缺陷分级，处理 P0/P1 问题。
4. 门禁：满足 Gate 5 后进入阶段 6。

### 阶段 6：质量审计（Code Review + Release Review）

1. 审查需求一致性、代码质量、安全、性能、发布准备。
2. 输出发布结论：`可以发布` / `有条件发布` / `暂缓发布`。
3. 门禁：满足 Gate 6 后可发布。
