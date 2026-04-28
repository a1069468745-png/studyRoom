export const errorFixtures = {
  forbidden: {
    code: "AUTH_FORBIDDEN",
    message: "You do not have access to this resource.",
    traceId: "trace-forbidden"
  },
  validation: {
    code: "VALIDATION_FAILED",
    message: "One or more request fields are invalid.",
    traceId: "trace-validation"
  },
  taskFailed: {
    code: "TASK_FAILED",
    message: "The background task failed. Check task details for retry guidance.",
    traceId: "trace-task-failed"
  }
} as const;

