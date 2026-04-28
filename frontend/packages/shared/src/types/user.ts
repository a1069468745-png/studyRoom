export type UserRole = "ADMIN" | "TEACHER" | "STUDENT";

export interface CurrentUserProfile {
  userId: string;
  displayName: string;
  role: UserRole;
  permissions: string[];
  scopeSummary: string;
}

