import { AxiosError, AxiosHeaders, type AxiosAdapter } from "axios";

import { createApiClient } from "./client";
import { ApiClientError } from "../types/error";
import { isTerminalTaskStatus, toTaskSnapshot } from "../utils/task";

function createSuccessAdapter(assertHeaders?: (headers: AxiosHeaders) => void): AxiosAdapter {
  return async (config) => {
    const headers = AxiosHeaders.from(config.headers ?? {});
    assertHeaders?.(headers);

    return {
      config,
      data: {
        userId: "teacher-001",
        displayName: "Lin Teacher",
        role: "TEACHER",
        permissions: ["paper:write", "exam:read"],
        scopeSummary: "Grade 9 mathematics"
      },
      headers: headers.toJSON(),
      status: 200,
      statusText: "OK"
    };
  };
}

function createForbiddenAdapter(): AxiosAdapter {
  return async (config) => {
    throw new AxiosError(
      "Request failed with status code 403",
      "ERR_BAD_REQUEST",
      config,
      undefined,
      {
        config,
        data: {
          code: "AUTH_FORBIDDEN",
          message: "You do not have access to this resource.",
          traceId: "trace-403"
        },
        headers: {},
        status: 403,
        statusText: "Forbidden"
      }
    );
  };
}

describe("StudyRoomApiClient", () => {
  it("injects auth, client and trace headers for shared requests", async () => {
    const client = createApiClient({
      adapter: createSuccessAdapter((headers) => {
        expect(headers.get("Authorization")).toBe("Bearer token-123");
        expect(headers.get("X-Client-Id")).toBe("web-client");
        expect(headers.get("X-Trace-Id")).toBe("trace-abc");
      }),
      baseURL: "http://localhost:8080",
      clientId: "web-client",
      getAccessToken: () => "token-123",
      getTraceId: () => "trace-abc"
    });

    const me = await client.getMe();

    expect(me.role).toBe("TEACHER");
    expect(me.permissions).toContain("paper:write");
  });

  it("normalizes unified backend error payloads into ApiClientError", async () => {
    const client = createApiClient({
      adapter: createForbiddenAdapter(),
      baseURL: "http://localhost:8080"
    });

    try {
      await client.getTask("job-forbidden");
      throw new Error("Expected getTask to throw.");
    } catch (error) {
      expect(error).toBeInstanceOf(ApiClientError);
      expect((error as ApiClientError).code).toBe("AUTH_FORBIDDEN");
      expect((error as ApiClientError).traceId).toBe("trace-403");
      expect((error as ApiClientError).status).toBe(403);
    }
  });

  it("fills task defaults and exposes terminal-state helpers", () => {
    const task = toTaskSnapshot({
      taskId: "job-paper-001",
      status: "RUNNING",
      taskType: "PAPER_GENERATION"
    });

    expect(task.progress).toBe(0);
    expect(task.retryable).toBe(false);
    expect(isTerminalTaskStatus(task.status)).toBe(false);
    expect(isTerminalTaskStatus("FAILED")).toBe(true);
  });
});

