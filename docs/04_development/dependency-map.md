# 技术开发依赖图

## 文档信息

| 字段 | 内容 |
|---|---|
| 项目 | studyRomm |
| 阶段 | 技术开发阶段 |
| 状态 | 已确认 |
| 版本 | v0.2 |
| 最后更新 | 2026-04-27 |
| 负责人 | AI planning agent |
| 相关文档 | docs/04_development/implementation-plan.md |

## 模块依赖

| 模块 | 直接依赖 |
|---|---|
| 工程与运行基座 | 无 |
| 用户与权限 | 工程与运行基座、数据库迁移 |
| 课程树与知识库 | 工程与运行基座、用户与权限 |
| 题库与审核 | 课程树与知识库、用户与权限 |
| 规则模板治理 | 题库与审核、用户与权限 |
| 组卷与混排 | 规则模板治理、题库与审核、课程树与知识库、任务框架 |
| 考试组织与提交 | 组卷与混排、用户与权限 |
| 批改与步骤解析 | 考试组织与提交、题库与审核 |
| 分析与错题 | 考试组织与提交、批改与步骤解析、任务框架 |
| 视频解析闭环 | 题库与审核、批改与步骤解析、用户与权限、文件资源、任务框架 |
| 前端双端联调 | 上述全部后端能力 |
| 安全与异常加固 | 上述全部后端能力 |
| 可观测与发布准备 | 上述全部能力 |

## 服务依赖建议

| 服务 | 主要职责 | 关键依赖 |
|---|---|---|
| gateway | 统一入口、路由、前置鉴权 | auth-service |
| auth-service | 认证、角色、授权、资源归属基础 | MySQL、公共模块 |
| knowledge-service | 教材版本、课程树、知识点、图文内容 | auth-service、MySQL |
| question-service | 题目、答案、解析、审核 | knowledge-service、auth-service |
| paper-service | 模板治理、范围解析、组卷任务 | question-service、job-service |
| exam-service | 考试计划、考试实例、提交 | paper-service、auth-service |
| grading-service | 判分、复核、步骤解析 | exam-service、question-service |
| analytics-service | 分析、错题、趋势 | exam-service、grading-service、job-service |
| file-service | 文件、图片、视频资源 | MinIO、auth-service |
| audit-service | 审计日志与查询 | 全部服务 |
| job-service | 异步任务状态、重试、失败原因 | 全部异步能力 |

## 开发阶段依赖

| 阶段 | 主要依赖 |
|---|---|
| DEV-001 | 无 |
| DEV-002 | DEV-001 |
| DEV-003 | DEV-001、DEV-002 |
| DEV-004 | DEV-003 |
| DEV-005 | DEV-004 |
| DEV-006 | DEV-005 |
| DEV-007 | DEV-006 |
| DEV-008 | DEV-004 至 DEV-007 |
| DEV-009 | DEV-008 |
| DEV-010 | DEV-009 |

## 外部依赖与阻塞判断

| 外部依赖 | 用途 | 是否阻塞首批规划 | 备注 |
|---|---|---|---|
| MySQL 8 | 业务数据存储 | 否 | DEV-002 前必须明确 |
| Redis | 缓存与幂等 | 否 | 可在后续阶段逐步引入 |
| MinIO | 文件和视频对象 | 否 | 视频闭环前需明确 |
| Nacos | 注册与配置 | 否 | 开发态可临时替代 |
| 第三方模型/视频生成能力 | 视频草稿生成 | 否 | DEV-007 前可先 mock |
| HTTPS/TLS 与密钥管理 | 传输安全和部署安全 | 否 | DEV-010 与真实部署前必须明确 |

## 与仓库现状的关系

- 现有 `backend/` 目录如果已存在，只能说明仓库里已有早期实现尝试。
- 本文件只描述规划依赖，不把现有代码目录视作阶段自动完成的依据。
