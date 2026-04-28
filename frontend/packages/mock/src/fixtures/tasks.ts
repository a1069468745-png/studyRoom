import { toTaskSnapshot } from "@study-room/shared";

export const taskFixtures = {
  paperGenerationRunning: toTaskSnapshot({
    taskId: "job-paper-001",
    taskType: "PAPER_GENERATION",
    status: "RUNNING",
    progress: 62,
    resultSummary: "Generating paper preview for Grade 9 mathematics.",
    traceId: "trace-paper-001"
  }),
  analysisReady: toTaskSnapshot({
    taskId: "job-analysis-001",
    taskType: "EXAM_ANALYSIS",
    status: "SUCCESS",
    progress: 100,
    resultSummary: "Analysis snapshot is ready for review.",
    traceId: "trace-analysis-001"
  }),
  videoDraftFailed: toTaskSnapshot({
    taskId: "job-video-001",
    taskType: "VIDEO_DRAFT",
    status: "FAILED",
    progress: 100,
    errorCode: "TASK_FAILED",
    errorMessage: "Video draft generation is unavailable in the current mock environment.",
    retryable: true,
    traceId: "trace-video-001"
  })
} as const;

