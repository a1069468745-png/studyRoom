export type TaskStatus = "PENDING" | "RUNNING" | "SUCCESS" | "FAILED" | "CANCELLED";

export interface TaskSubmission {
  taskId: string;
  status: TaskStatus;
}

export interface TaskSnapshot {
  taskId: string;
  taskType: string;
  status: TaskStatus;
  progress: number;
  resultSummary: string | null;
  errorCode: string | null;
  errorMessage: string | null;
  retryable: boolean;
  traceId: string;
}

