import axios, {
  AxiosHeaders,
  type AxiosAdapter,
  type AxiosInstance,
  type AxiosRequestConfig
} from "axios";

import type { TaskSnapshot, TaskSubmission } from "../types/task";
import { createApiError, isApiErrorPayload, type ApiClientError } from "../types/error";
import type { CurrentUserProfile } from "../types/user";

export interface ApiClientOptions {
  baseURL: string;
  clientId?: string;
  timeoutMs?: number;
  adapter?: AxiosAdapter;
  getAccessToken?: () => string | undefined;
  getTraceId?: () => string | undefined;
}

export class StudyRoomApiClient {
  private readonly http: AxiosInstance;

  constructor(options: ApiClientOptions) {
    this.http = axios.create({
      adapter: options.adapter,
      baseURL: options.baseURL,
      timeout: options.timeoutMs ?? 10000
    });

    this.http.interceptors.request.use((config) => {
      const headers = AxiosHeaders.from(config.headers ?? {});
      const accessToken = options.getAccessToken?.();
      const traceId = options.getTraceId?.();

      if (accessToken) {
        headers.set("Authorization", `Bearer ${accessToken}`);
      }

      headers.set("X-Client-Id", options.clientId ?? "frontend-shell");

      if (traceId) {
        headers.set("X-Trace-Id", traceId);
      }

      return {
        ...config,
        headers
      };
    });

    this.http.interceptors.response.use(
      (response) => response,
      (error) => Promise.reject(normalizeApiError(error))
    );
  }

  async request<T>(config: AxiosRequestConfig): Promise<T> {
    const response = await this.http.request<T>(config);
    return response.data;
  }

  getMe(): Promise<CurrentUserProfile> {
    return this.request({
      method: "GET",
      url: "/api/common/me"
    });
  }

  getTask(taskId: string): Promise<TaskSnapshot> {
    return this.request({
      method: "GET",
      url: `/api/common/tasks/${taskId}`
    });
  }

  generateVideoDraft(payload: Record<string, unknown>): Promise<TaskSubmission & { draftId?: string }> {
    return this.request({
      method: "POST",
      url: "/api/admin/videos/generate",
      data: payload
    });
  }
}

export function createApiClient(options: ApiClientOptions): StudyRoomApiClient {
  return new StudyRoomApiClient(options);
}

export function normalizeApiError(error: unknown): ApiClientError {
  if (axios.isAxiosError(error)) {
    const payload = error.response?.data;
    if (isApiErrorPayload(payload)) {
      return createApiError(payload, error.response?.status);
    }

    return createApiError(
      {
        code: "HTTP_REQUEST_FAILED",
        message: error.message || "Request failed.",
        traceId: "trace-unavailable",
        details: typeof payload === "object" && payload ? (payload as Record<string, unknown>) : null
      },
      error.response?.status
    );
  }

  if (error instanceof Error) {
    return createApiError({
      code: "UNKNOWN_CLIENT_ERROR",
      message: error.message,
      traceId: "trace-unavailable"
    });
  }

  return createApiError({
    code: "UNKNOWN_CLIENT_ERROR",
    message: "Unknown client error.",
    traceId: "trace-unavailable"
  });
}

