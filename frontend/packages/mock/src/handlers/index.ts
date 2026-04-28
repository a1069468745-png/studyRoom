import { errorFixtures } from "../fixtures/errors";
import { taskFixtures } from "../fixtures/tasks";
import { userFixtures } from "../fixtures/users";

export type MockMethod = "GET" | "POST";

export interface MockHandler {
  method: MockMethod;
  path: string;
  status: number;
  body: unknown;
}

export const mockHandlers: MockHandler[] = [
  {
    method: "GET",
    path: "/api/common/me",
    status: 200,
    body: userFixtures.teacher
  },
  {
    method: "GET",
    path: "/api/common/tasks/job-paper-001",
    status: 200,
    body: taskFixtures.paperGenerationRunning
  },
  {
    method: "GET",
    path: "/api/common/tasks/job-analysis-001",
    status: 200,
    body: taskFixtures.analysisReady
  },
  {
    method: "GET",
    path: "/api/common/tasks/job-video-001",
    status: 200,
    body: taskFixtures.videoDraftFailed
  },
  {
    method: "POST",
    path: "/api/admin/videos/generate",
    status: 202,
    body: {
      taskId: "job-video-001",
      status: "PENDING",
      draftId: "draft-video-001"
    }
  },
  {
    method: "GET",
    path: "/api/admin/forbidden",
    status: 403,
    body: errorFixtures.forbidden
  }
];

export function resolveMockHandler(method: MockMethod, path: string): MockHandler {
  const handler = mockHandlers.find((entry) => entry.method === method && entry.path === path);

  if (handler) {
    return handler;
  }

  return {
    method,
    path,
    status: 404,
    body: {
      code: "RESOURCE_NOT_FOUND",
      message: `No mock handler defined for ${method} ${path}.`,
      traceId: "trace-mock-404"
    }
  };
}

