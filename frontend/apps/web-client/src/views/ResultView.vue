<script setup lang="ts">
import { computed, ref } from "vue";

interface ResultItem {
  label: string;
  value: string;
}

const examId = ref("exam-demo-001");
const studentId = ref("student-demo-001");
const result = ref<ResultItem[]>([
  { label: "总分", value: "88" },
  { label: "客观题得分", value: "46" },
  { label: "主观题得分", value: "42" },
  { label: "状态", value: "已完成（Mock）" }
]);

const title = computed(() => `${examId.value} / ${studentId.value}`);
</script>

<template>
  <section class="result">
    <section class="panel">
      <h3>结果查询骨架</h3>
      <el-form inline>
        <el-form-item label="考试 ID">
          <el-input v-model="examId" />
        </el-form-item>
        <el-form-item label="学生 ID">
          <el-input v-model="studentId" />
        </el-form-item>
      </el-form>
      <el-alert type="info" :title="`当前上下文：${title}`" show-icon :closable="false" />
    </section>

    <section class="panel">
      <h3>成绩与解析摘要</h3>
      <el-descriptions :column="1" border>
        <el-descriptions-item v-for="item in result" :key="item.label" :label="item.label">
          {{ item.value }}
        </el-descriptions-item>
      </el-descriptions>
    </section>
  </section>
</template>

<style scoped>
.result {
  display: grid;
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
</style>
