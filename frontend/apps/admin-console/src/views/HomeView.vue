<script setup lang="ts">
import { computed, onMounted } from "vue";

import { useTaskShellStore, describeTask, toTaskTagType } from "@study-room/shared";
import { mockHandlers, taskFixtures, userFixtures } from "@study-room/mock";

const taskStore = useTaskShellStore();

onMounted(() => {
  taskStore.upsertTask(taskFixtures.videoDraftFailed);
});

const currentUser = userFixtures.admin;
const tasks = computed(() => taskStore.orderedTasks);
const handlerCount = mockHandlers.length;
</script>

<template>
  <section class="grid">
    <el-card class="hero" shadow="never">
      <template #header>
        <div class="card-header">
          <span>Admin Shell</span>
          <el-tag type="warning">Review and governance baseline</el-tag>
        </div>
      </template>
      <p class="hero__text">
        The admin console starts with route wiring, Element Plus layout, shared task handling and fixture-driven
        endpoints for review workflows such as curriculum, question audit and video draft publishing.
      </p>
      <el-space wrap>
        <el-tag>{{ currentUser.role }}</el-tag>
        <el-tag type="info">{{ handlerCount }} mock handlers</el-tag>
        <el-tag type="danger">Unified error payloads</el-tag>
      </el-space>
    </el-card>

    <el-row :gutter="20">
      <el-col :md="12" :xs="24">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>Admin Persona</span>
            </div>
          </template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="Name">{{ currentUser.displayName }}</el-descriptions-item>
            <el-descriptions-item label="Permissions">
              {{ currentUser.permissions.join(", ") }}
            </el-descriptions-item>
            <el-descriptions-item label="Scope">{{ currentUser.scopeSummary }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
      <el-col :md="12" :xs="24">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>Task Failure Example</span>
            </div>
          </template>
          <div class="task-list">
            <article v-for="task in tasks" :key="task.taskId" class="task-item">
              <div class="task-item__top">
                <strong>{{ task.taskType }}</strong>
                <el-tag :type="toTaskTagType(task.status)">{{ task.status }}</el-tag>
              </div>
              <p>{{ describeTask(task) }}</p>
              <el-alert
                v-if="task.errorCode"
                :closable="false"
                :title="`${task.errorCode} · retryable: ${task.retryable}`"
                type="error"
              />
            </article>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </section>
</template>

<style scoped>
.grid {
  display: grid;
  gap: 20px;
}

.hero {
  border: 1px solid rgba(217, 119, 6, 0.16);
}

.hero__text {
  margin: 0 0 16px;
  color: #4a5568;
  line-height: 1.6;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.task-list {
  display: grid;
  gap: 14px;
}

.task-item {
  padding: 14px;
  border-radius: 12px;
  background: #fff8ef;
}

.task-item__top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 8px;
}

.task-item p {
  margin: 0 0 12px;
  color: #4a5568;
  line-height: 1.5;
}
</style>
