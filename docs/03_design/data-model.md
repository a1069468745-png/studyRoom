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
- `scope`：资源范围
- `audit_log`：操作审计

### 2.2 知识库

- `knowledge_node`：知识点树节点
- `content_asset`：图文内容资源
- `textbook_version`：教材版本
- `knowledge_relation`：知识点关联

### 2.3 题库

- `question`：题目主表
- `question_option`：选择题选项
- `question_answer`：标准答案/参考答案
- `question_analysis`：解析与步骤
- `question_knowledge`：题目-知识点映射
- `question_tag`：题目标签

### 2.4 组卷与考试

- `paper_rule`：组卷规则
- `paper_template`：规则模板版本
- `paper`：试卷
- `paper_item`：试卷题目
- `exam_plan`：考试计划
- `exam_session`：考试实例
- `exam_submission`：学生答卷
- `grading_record`：批改结果
- `review_task`：人工复核任务

### 2.5 分析与任务

- `analysis_report`：成绩分析
- `wrong_question`：错题记录
- `job_task`：异步任务
- `file_object`：文件/视频资源
- `resource_publish`：审核发布记录

## 3. 关键关系

- 一个题目必须至少关联一个知识点。
- 一个试卷包含多个试题。
- 一个考试计划可以生成多个考试实例。
- 一个考试实例对应多个答卷。
- 一个答卷可以对应多个批改记录。
- 一个分析报告由考试结果聚合生成。
- 一个视频资源必须有上传者、审核状态和发布记录。

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
- `exam_session` 必须有开始时间、结束时间、时长、迟到处理规则、交卷规则。
- `grading_record` 必须标记自动批改或人工批改。
- `analysis_report` 必须能追踪到原始考试实例。

## 6. 未决项

- 知识点树的最小层级是否固定为 `学段 -> 年级 -> 学科 -> 单元 -> 课 -> 知识点`，当前按该结构设计。
- 是否需要分区表或归档表，当前按服务独立 schema 的普通表处理。
- 视频讲解资源的存储大小上限待产品进一步细化。
