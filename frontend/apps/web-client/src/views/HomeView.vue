<script setup lang="ts">
import { computed, onMounted } from "vue";

import { useTaskShellStore, describeTask, toTaskTagType } from "@study-room/shared";
import { taskFixtures, userFixtures } from "@study-room/mock";

const taskStore = useTaskShellStore();

onMounted(() => {
  taskStore.upsertTask(taskFixtures.paperGenerationRunning);
  taskStore.upsertTask(taskFixtures.analysisReady);
});

const currentUser = userFixtures.teacher;
const secondaryUser = userFixtures.student;
const tasks = computed(() => taskStore.orderedTasks);
</script>

<template>
  <section class="grid">
    <el-card class="hero" shadow="never">
      <template #header>
        <div class="card-header">
          <span>Client Shell</span>
          <el-tag type="success">Ready for mock-first development</el-tag>
        </div>
      </template>
      <p class="hero__text">
        This workspace gives teachers and students a shared Vue shell, router baseline, task-state store and
        contract-friendly API layer before real backend integration starts.
      </p>
      <el-space wrap>
        <el-tag>{{ currentUser.role }}</el-tag>
        <el-tag type="info">{{ secondaryUser.role }}</el-tag>
        <el-tag type="warning">Gateway-first API access</el-tag>
      </el-space>
    </el-card>

    <el-row :gutter="20">
      <el-col :md="12" :xs="24">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>Mock Personas</span>
            </div>
          </template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="Teacher">{{ currentUser.displayName }}</el-descriptions-item>
            <el-descriptions-item label="Scope">{{ currentUser.scopeSummary }}</el-descriptions-item>
            <el-descriptions-item label="Student">{{ secondaryUser.displayName }}</el-descriptions-item>
            <el-descriptions-item label="Student Scope">{{ secondaryUser.scopeSummary }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
      <el-col :md="12" :xs="24">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>Task Store Snapshot</span>
            </div>
          </template>
          <div class="task-list">
            <article v-for="task in tasks" :key="task.taskId" class="task-item">
              <div class="task-item__top">
                <strong>{{ task.taskType }}</strong>
                <el-tag :type="toTaskTagType(task.status)">{{ task.status }}</el-tag>
              </div>
              <p>{{ describeTask(task) }}</p>
              <el-progress :percentage="task.progress" :show-text="false" />
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
  border: 1px solid rgba(47, 107, 255, 0.12);
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
  background: #f7faff;
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

