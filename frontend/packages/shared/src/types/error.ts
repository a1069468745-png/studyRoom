export interface ApiErrorPayload {
  code: string;
  message: string;
  traceId: string;
  details?: Record<string, unknown> | null;
}

export class ApiClientError extends Error {
  readonly code: string;
  readonly traceId: string;
  readonly status?: number;
  readonly details?: Record<string, unknown> | null;

  constructor(payload: ApiErrorPayload, status?: number) {
    super(payload.message);
    this.name = "ApiClientError";
    this.code = payload.code;
    this.traceId = payload.traceId;
    this.status = status;
    this.details = payload.details;
  }
}

export function createApiError(
  payload: Partial<ApiErrorPayload> & Pick<ApiErrorPayload, "code" | "message">,
  status?: number
): ApiClientError {
  return new ApiClientError(
    {
      code: payload.code,
      message: payload.message,
      traceId: payload.traceId ?? "trace-unavailable",
      details: payload.details ?? null
    },
    status
  );
}

export function isApiErrorPayload(value: unknown): value is ApiErrorPayload {
  if (!value || typeof value !== "object") {
    return false;
  }

  const candidate = value as Partial<ApiErrorPayload>;
  return (
    typeof candidate.code === "string" &&
    typeof candidate.message === "string" &&
    typeof candidate.traceId === "string"
  );
}

