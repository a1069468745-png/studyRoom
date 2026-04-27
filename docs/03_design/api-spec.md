# API 规格

## 文档信息

| 字段 | 内容 |
|---|---|
| 项目 | studyRomm |
| 阶段 | 方案设计阶段 |
| 状态 | 草案 |
| 版本 | v0.1 |
| 最后更新 | 2026-04-27 |
| 相关来源 | `docs/03_design/architecture.md`、`docs/03_design/data-model.md`、`ai/specs.md`、用户阶段 3 答复 |

## 1. API 设计原则

- 前端与管理端接口分层。
- 共享公共查询和通用分页规范。
- 所有写接口返回统一错误码。
- 所有异步任务返回任务 ID 和状态查询接口。
- 所有敏感操作写审计日志。
- 对外接口统一经由网关暴露，内部服务接口不直接暴露给前端。

## 2. 接口分组

### 2.1 公共接口

- `GET /api/common/me`
- `GET /api/common/dictionaries`
- `GET /api/common/health`

### 2.2 客户端接口

- `GET /api/client/knowledge/nodes`
- `GET /api/client/questions`
- `POST /api/client/papers/rules`
- `POST /api/client/papers/generate`
- `GET /api/client/papers/{paperId}`
- `POST /api/client/exams/{examId}/submit`
- `GET /api/client/exams/{examId}/analysis`

### 2.3 管理端接口

- `POST /api/admin/knowledge/nodes`
- `POST /api/admin/questions`
- `POST /api/admin/questions/{questionId}/review`
- `POST /api/admin/papers/templates`
- `POST /api/admin/resources/upload`
- `POST /api/admin/resources/{resourceId}/review`
- `POST /api/admin/exams/plans`
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

## 4. 关键接口说明

### 4.1 组卷

`POST /api/client/papers/generate`

输入：

- 年级
- 学科
- 题型
- 难度
- 知识点集合
- 真题比例
- 跨年级开关

输出：

- `taskId`
- 预估题量
- 当前状态

### 4.2 考试提交

`POST /api/client/exams/{examId}/submit`

输入：

- 答案列表
- 提交时间
- 客户端标识

输出：

- 提交结果
- 批改任务状态

### 4.3 审核

`POST /api/admin/questions/{questionId}/review`

输入：

- 审核结果
- 审核意见

输出：

- 审核状态

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

## 6. 幂等与重试

- 组卷任务需要支持幂等键。
- 考试提交需要防重复提交。
- 审核接口需要防重复点击。

## 7. 版本策略

- 对外路径统一保留 `v1` 语义，便于后续平滑升级。
- 后续如接口破坏性变化，再引入版本前缀。
