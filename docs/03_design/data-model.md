# 数据模型设计

## 文档信息

| 字段 | 内容 |
|---|---|
| 项目 | studyRomm |
| 阶段 | 方案设计阶段 |
| 状态 | 草案 |
| 版本 | v0.1 |
| 最后更新 | 2026-04-27 |
| 相关来源 | `docs/03_design/architecture.md`、`ai/specs.md`、`ai/decisions.md`、`docs/02_product/acceptance-criteria.md` |

## 1. 设计原则

- 逻辑上按服务拆分数据所有权。
- 物理上可先使用同一 MySQL 集群，但按服务独立 schema / database 管理。
- 统一使用软删除和审计字段。
- 服务之间不建立跨库外键，只保留业务主键和关联字段。
- 支持后续独立数据库拆分时保留稳定主键。

## 2. 核心实体

### 2.1 用户与权限

- `user`：用户账号
- `role`：角色
- `user_role`：用户角色关系
- `class_room`：班级
- `class_membership`：班级成员关系（老师/学生）
- `teaching_assignment`：老师教学授权范围
- `resource_owner_scope`：资源归属范围
- `audit_log`：操作审计

### 2.2 知识库

- `curriculum_node`：课程树节点，包含单元/章节/知识点等有序层级
- `knowledge_node`：知识点节点，可与 `curriculum_node` 合并实现或作为知识点视图
- `content_asset`：图文内容资源
- `textbook_version`：教材版本
- `knowledge_relation`：知识点关联

### 2.3 题库

- `question`：题目主表
- `question_option`：选择题选项
- `question_answer`：标准答案/参考答案
- `question_analysis`：解析与步骤
- `question_knowledge`：题目-知识点映射
- `question_curriculum_node`：题目-课程树节点冗余映射，用于按单元/章节快速筛题
- `question_tag`：题目标签

### 2.4 组卷与考试

- `paper_rule`：组卷规则
- `paper_template`：规则模板版本
- `paper_template_version`：模板版本快照
- `paper_template_publish_log`：模板发布/停用/回滚记录
- `exam_scope`：考试或组卷范围，支持节点区间或节点集合
- `paper`：试卷
- `paper_item`：试卷题目
- `exam_plan`：考试计划
- `exam_plan_target`：考试目标范围（班级/学生）
- `exam_session`：考试实例
- `exam_submission`：学生答卷
- `grading_record`：批改结果
- `review_task`：人工复核任务

### 2.5 分析与任务

- `analysis_report`：成绩分析
- `wrong_question`：错题记录
- `job_task`：异步任务
- `file_object`：文件/视频资源
- `video_draft`：视频草稿
- `video_review_record`：视频审核记录
- `video_publish_record`：视频发布记录

## 3. 关键关系

- 一个题目必须至少关联一个知识点。
- 一个知识点必须可追溯到所属教材版本与课程树位置。
- 一个单元必须有明确排序，用于表达教学进度范围。
- 一个试卷包含多个试题。
- 一个考试计划可以生成多个考试实例。
- 一个考试实例对应多个答卷。
- 一个答卷可以对应多个批改记录。
- 一个分析报告由考试结果聚合生成。
- 一个考试计划必须至少关联一个 `exam_plan_target`。
- 一个老师必须通过 `teaching_assignment` 或资源归属才能访问班级、考试和分析数据。
- 一个视频草稿必须关联题目或解析来源、草稿状态、审核记录和发布记录。
- 历史试卷必须能追溯到生成时使用的 `paper_template_version`。
- 一个 `exam_scope` 必须绑定教材版本，并以 `NODE_RANGE`、`UNIT_SET`、`CHAPTER_SET` 或 `KNOWLEDGE_SET` 方式明确保存。

## 4. 软删除与审计

### 4.1 软删除字段

建议所有核心表保留：

- `is_deleted`
- `deleted_at`
- `deleted_by`

### 4.2 审计字段

建议所有核心表保留：

- `created_at`
- `created_by`
- `updated_at`
- `updated_by`

## 5. 重要字段约束

- `question` 必须有题型、难度、来源、审核状态。
- `curriculum_node` 必须有节点类型、父节点、排序号、教材版本和是否启用状态。
- `exam_session` 必须有开始时间、结束时间、时长、迟到处理规则、交卷规则。
- `grading_record` 必须标记自动批改或人工批改。
- `analysis_report` 必须能追踪到原始考试实例。
- `paper_template` 必须有生命周期状态：`DRAFT`、`PUBLISHED`、`DISABLED`、`ARCHIVED`。
- `paper_template_version` 必须记录版本号、规则快照、创建人和生效状态。
- `paper_rule` 必须记录范围模式：`ALL_SUBJECT`、`UNIT_SET`、`CHAPTER_SET`、`NODE_RANGE`、`KNOWLEDGE_SET`。
- `exam_scope` 必须记录范围类型、起止节点或节点集合。
- `job_task` 必须记录任务类型、状态、幂等键、结果摘要、失败原因、是否可重试。
- `video_draft` 必须记录来源题目/解析版本、草稿状态、可见范围和文件对象引用。

## 6. 补充说明

- 课程树首版默认结构为 `学段 -> 年级 -> 学科 -> 单元 -> 章节 -> 知识点`，后续如教材需要可在兼容前提下扩展更多层级。
- 是否需要分区表或归档表，当前按服务独立 schema 的普通表处理。
- 视频讲解资源的存储大小上限待产品进一步细化，但不影响首版最小验收闭环。
