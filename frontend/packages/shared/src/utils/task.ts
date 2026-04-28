import type { TaskSnapshot, TaskStatus } from "../types/task";

const terminalStatuses = new Set<TaskStatus>(["SUCCESS", "FAILED", "CANCELLED"]);

export function isTerminalTaskStatus(status: TaskStatus): boolean {
  return terminalStatuses.has(status);
}

export function toTaskSnapshot(
  task: Partial<TaskSnapshot> & Pick<TaskSnapshot, "taskId" | "status">
): TaskSnapshot {
  return {
    taskId: task.taskId,
    taskType: task.taskType ?? "GENERIC",
    status: task.status,
    progress: task.progress ?? 0,
    resultSummary: task.resultSummary ?? null,
    errorCode: task.errorCode ?? null,
    errorMessage: task.errorMessage ?? null,
    retryable: task.retryable ?? false,
    traceId: task.traceId ?? "trace-unavailable"
  };
}

export function toTaskTagType(status: TaskStatus): "info" | "warning" | "success" | "danger" {
  switch (status) {
    case "PENDING":
      return "info";
    case "RUNNING":
      return "warning";
    case "SUCCESS":
      return "success";
    case "FAILED":
    case "CANCELLED":
      return "danger";
  }
}

export function describeTask(task: TaskSnapshot): string {
  if (task.status === "FAILED" && task.errorMessage) {
    return task.errorMessage;
  }

  if (task.resultSummary) {
    return task.resultSummary;
  }

  if (isTerminalTaskStatus(task.status)) {
    return "Task completed without additional summary.";
  }

  return "Task is still in progress.";
}

