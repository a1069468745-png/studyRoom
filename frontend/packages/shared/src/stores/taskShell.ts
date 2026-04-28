import { defineStore } from "pinia";

import type { TaskSnapshot } from "../types/task";
import { toTaskSnapshot } from "../utils/task";

export const useTaskShellStore = defineStore("task-shell", {
  state: () => ({
    tasks: {} as Record<string, TaskSnapshot>
  }),
  getters: {
    orderedTasks: (state) => Object.values(state.tasks).sort((left, right) => left.taskId.localeCompare(right.taskId))
  },
  actions: {
    upsertTask(task: Partial<TaskSnapshot> & Pick<TaskSnapshot, "taskId" | "status">) {
      this.tasks[task.taskId] = toTaskSnapshot(task);
    },
    clearTask(taskId: string) {
      delete this.tasks[taskId];
    }
  }
});

