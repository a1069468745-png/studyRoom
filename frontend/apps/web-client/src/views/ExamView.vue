<script setup lang="ts">
import { computed, reactive } from "vue";

import { taskFixtures } from "@study-room/mock";
import { describeTask, toTaskTagType, useTaskShellStore } from "@study-room/shared";

const taskStore = useTaskShellStore();
const form = reactive({
  examId: "exam-demo-001",
  clientId: "web-client",
  answerPayload: "{\"answers\":[]}"
});

const submitTask = computed(() => taskStore.getTask("job-submit-demo"));

function submitExam(): void {
  taskStore.upsertTask({
    ...taskFixtures.paperGenerationRunning,
    taskId: "job-submit-demo",
    taskType: "EXAM_SUBMIT",
    status: "RUNNING",
    progress: 20,
    resultSummary: "答卷提交处理中"
  });
}
</script>

<template>
  <section class="exam">
    <section class="panel">
      <h3>考试提交骨架</h3>
      <el-form label-position="top">
        <el-form-item label="考试 ID">
          <el-input v-model="form.examId" />
        </el-form-item>
        <el-form-item label="客户端标识">
          <el-input v-model="form.clientId" />
        </el-form-item>
        <el-form-item label="答卷内容（JSON）">
          <el-input v-model="form.answerPayload" type="textarea" :rows="6" />
        </el-form-item>
      </el-form>
      <el-button type="primary" @click="submitExam">提交（Mock）</el-button>
    </section>

    <section class="panel">
      <h3>提交任务状态</h3>
      <el-empty v-if="!submitTask" description="尚未提交任务" />
      <div v-else class="task">
        <div class="task-head">
          <strong>{{ submitTask.taskType }}</strong>
          <el-tag :type="toTaskTagType(submitTask.status)">{{ submitTask.status }}</el-tag>
        </div>
        <p>{{ describeTask(submitTask) }}</p>
        <el-progress :percentage="submitTask.progress" :show-text="false" />
      </div>
    </section>
  </section>
</template>

<style scoped>
.exam {
  display: grid;
  grid-template-columns: 1.2fr 1fr;
  gap: 12px;
}

.panel {
  background: #fff;
  border: 1px solid #dcdfe6;
  padding: 12px;
}

.panel h3 {
  margin: 0 0 12px;
  font-size: 16px;
}

.task-head {
  display: flex;
  justify-content: space-between;
}

.task p {
  margin: 10px 0;
  color: #606266;
}
</style>
