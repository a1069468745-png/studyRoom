import type { CurrentUserProfile } from "@study-room/shared";

export const userFixtures: Record<"teacher" | "student" | "admin", CurrentUserProfile> = {
  teacher: {
    userId: "teacher-001",
    displayName: "Lin Teacher",
    role: "TEACHER",
    permissions: ["paper:write", "exam:read", "analysis:read"],
    scopeSummary: "Grade 9 mathematics / class groups A-B"
  },
  student: {
    userId: "student-001",
    displayName: "Chen Student",
    role: "STUDENT",
    permissions: ["exam:write", "video:read"],
    scopeSummary: "Grade 9 mathematics / class group A"
  },
  admin: {
    userId: "admin-001",
    displayName: "Platform Admin",
    role: "ADMIN",
    permissions: ["knowledge:manage", "video:review", "audit:read"],
    scopeSummary: "Global admin scope"
  }
};

