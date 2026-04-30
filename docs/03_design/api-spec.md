# API 规格

## 文档信息

| 字段 | 内容 |
|---|---|
| 项目 | studyRomm |
| 阶段 | 方案设计阶段 |
| 状态 | 草案 |
| 版本 | v0.2 |
| 最后更新 | 2026-04-29 |
| 相关来源 | `docs/03_design/architecture.md`、`docs/03_design/data-model.md`、`ai/specs.md`、用户阶段 3 答复 |

## 1. API 设计原则

- 前端与管理端接口分层。
- 共享公共查询和通用分页规范。
- 所有写接口返回统一错误码。
- 所有异步任务返回任务 ID 和状态查询接口。
- 所有敏感操作写审计日志。
- 对外接口统一经由网关暴露，内部服务接口不直接暴露给前端。
- 权限校验统一按角色、资源归属、流程状态三层执行。

## 2. 接口分组

### 2.1 公共接口

- `GET /api/common/me`
- `GET /api/common/dictionaries`
- `GET /api/common/health`
- `GET /api/common/tasks/{taskId}`
- `POST /api/auth/logout`

### 2.2 客户端接口

- `GET /api/client/knowledge/nodes`
- `GET /api/client/knowledge/content-assets`
- `GET /api/client/questions`
- `POST /api/client/papers/rules`
- `POST /api/client/papers/generate`
- `GET /api/client/papers/{paperId}`
- `POST /api/client/exams/{examId}/submit`
- `GET /api/client/exams/{examId}/analysis`
- `GET /api/client/videos/{videoId}`

### 2.3 管理端接口

- `POST /api/admin/knowledge/nodes`
- `GET /api/admin/knowledge/content-assets`
- `POST /api/admin/knowledge/content-assets`
- `POST /api/admin/questions`
- `POST /api/admin/questions/{questionId}/review`
- `POST /api/admin/papers/templates`
- `GET /api/admin/papers/templates/{templateId}/preview`
- `POST /api/admin/resources/upload`
- `POST /api/admin/resources/{resourceId}/review`
- `POST /api/admin/exams/plans`
- `POST /api/admin/videos/generate`
- `GET /api/admin/videos/drafts/{draftId}`
- `POST /api/admin/videos/drafts/{draftId}/review`
- `POST /api/admin/videos/drafts/{draftId}/publish`
- `GET /api/admin/audit-logs`

## 3. 通用请求约定

### 3.1 分页

请求参数：

- `page`
- `pageSize`

返回字段：

- `total`
- `items`

### 3.2 错误结构

```json
{
  "code": "QUESTION_NOT_FOUND",
  "message": "question not found",
  "traceId": "trace-xxx"
}
```

### 3.3 异步任务

```json
{
  "taskId": "job_123",
  "status": "PENDING"
}
```

任务状态：

- `PENDING`
- `RUNNING`
- `SUCCESS`
- `FAILED`
- `CANCELLED`

任务查询返回至少包含：

- `taskId`
- `taskType`
- `status`
- `progress`
- `resultSummary`
- `errorCode`
- `errorMessage`
- `retryable`
- `traceId`

## 4. 关键接口说明

### 4.1 组卷

`POST /api/client/papers/generate`

输入：

- 年级
- 学科
- 教材版本
- 范围模式：`ALL_SUBJECT` / `UNIT_SET` / `CHAPTER_SET` / `NODE_RANGE` / `KNOWLEDGE_SET`
- 单元集合
- 章节集合
- 起始节点
- 截止节点
- 题型
- 难度
- 知识点集合
- 真题比例
- 跨年级开关

输出：

- `taskId`
- 预估题量
- 当前状态
- 解析后的课程范围摘要

### 4.2 考试提交

`POST /api/client/exams/{examId}/submit`

输入：

- 答案列表
- 提交时间
- 客户端标识

输出：

- 提交结果
- 批改任务状态

### 4.3 模板预览

`GET /api/admin/papers/templates/{templateId}/preview`

输出：

- 题量统计
- 难度分布
- 知识点覆盖率
- 真题占比
- 课程范围摘要
- 降级提示列表

### 4.4 视频草稿生成

`POST /api/admin/videos/generate`

输入：

- 题目 ID
- 答案/解析版本 ID
- 生成原因或备注

输出：

- `taskId`
- `draftId`
- 当前状态

### 4.5 视频审核

`POST /api/admin/videos/drafts/{draftId}/review`

输入：

- 审核结果
- 审核意见

输出：

- 草稿状态
- 审核记录 ID

### 4.6 题目审核

`POST /api/admin/questions/{questionId}/review`

输入：

- 审核结果
- 审核意见

输出：

- 审核状态

### 4.7 课程树查询

`GET /api/common/dictionaries/curriculum-nodes`

请求参数：

- 教材版本
- 学科
- 节点类型
- 父节点 ID

输出：

- 节点列表
- 节点类型
- 排序号
- 父子关系

### 4.8 当前身份摘要

`GET /api/common/me`

输出至少包含：

- `actorType`
- `actorId`
- `username`
- `displayName`
- `serviceName`
- `roleCodes`
- `authorizationSummary`

`authorizationSummary` 当前已落地字段：

- `platformAdmin`
- `classRoomIds`
- `membershipRoles`
- `teachingSubjectCodes`
- `teachingAssignmentClassRoomIds`
- `ownedResourceTypes`

### 4.9 资源访问校验

`GET /api/auth/access-check`

请求参数：

- `resourceType`
- `resourceId`

成功返回：

- `resourceType`
- `resourceId`
- `allowed=true`

失败返回：

- `AUTH_FORBIDDEN`

说明：

- 当前用于 `DEV-003` 的资源归属校验与审计留痕基线验证。

### 4.10 图文内容接口（DEV-004-B）

`GET /api/admin/knowledge/content-assets`

请求参数（可选）：

- `curriculumNodeId`
- `reviewStatus`
- `publishStatus`

输出：

- 图文内容列表（`assetId`、`curriculumNodeId`、`assetType`、`title`、`bodyMarkdown`、`reviewStatus`、`publishStatus`）

`POST /api/admin/knowledge/content-assets`

输入：

- `curriculumNodeId`
- `assetType`
- `title`
- `bodyMarkdown`

输出：

- `assetId`
- `reviewStatus`（默认 `DRAFT`）
- `publishStatus`（默认 `UNPUBLISHED`）

`GET /api/client/knowledge/content-assets`

请求参数（可选）：

- `curriculumNodeId`

输出：

- 仅返回 `reviewStatus=APPROVED` 且 `publishStatus=PUBLISHED` 的图文内容列表

### 4.11 题目审核接口（DEV-004-D）

`POST /api/admin/questions/{questionId}/review`

输入：

- `reviewStatus`（仅允许 `APPROVED` / `REJECTED`）
- `reviewComment`（可选）

输出：

- `questionId`
- `reviewStatus`

约束：

- 仅 `DRAFT` 状态题目可进入审核流转；
- 非法状态流转返回 `BUSINESS_RULE_VIOLATION`；
- 题目不存在返回 `RESOURCE_NOT_FOUND`。

### 4.12 客户端题目查询接口（DEV-004 下一子项）

`GET /api/client/questions`

请求参数（可选）：

- `subjectCode`
- `gradeCode`
- `questionType`

输出：

- 题目列表（`questionId`、`questionType`、`difficultyLevel`、`stemMarkdown`、`gradeCode`、`subjectCode`）

约束：

- 仅返回 `reviewStatus=APPROVED` 且未删除题目；
- 支持按学科、年级、题型过滤。

## 5. 统一错误码

- `AUTH_UNAUTHORIZED`
- `AUTH_FORBIDDEN`
- `VALIDATION_FAILED`
- `RESOURCE_NOT_FOUND`
- `TASK_FAILED`
- `BUSINESS_RULE_VIOLATION`
- `FILE_UPLOAD_FAILED`
- `EXAM_OUT_OF_WINDOW`
- `REVIEW_REQUIRED`
- `IDEMPOTENCY_CONFLICT`
- `TASK_NOT_FOUND`
- `VIDEO_DRAFT_NOT_VISIBLE`
- `INVALID_SCOPE_RANGE`
- `CURRICULUM_NODE_NOT_FOUND`

## 6. 幂等与重试

- 组卷任务需要支持幂等键，按请求摘要 + 发起人生成。
- 课程范围选择必须参与幂等键计算。
- 考试提交需要防重复提交，按 `examId + studentId` 控制。
- 视频草稿生成按题目 + 解析版本摘要防止重复生成。
- 审核接口需要防重复点击。

## 7. 版本策略

- 对外路径统一保留 `v1` 语义，便于后续平滑升级。
- 后续如接口破坏性变化，再引入版本前缀。
