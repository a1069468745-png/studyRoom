<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";

import { createApiClient, type ClientQuestionItem, type ContentAssetItem, type CurriculumNodeDictionaryItem } from "@study-room/shared";
import { learningFixtures } from "@study-room/mock";

interface TreeNode {
  label: string;
  nodeId: string;
  children?: TreeNode[];
}

const route = useRoute();
const router = useRouter();
const api = createApiClient({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? "",
  clientId: "web-client-learning"
});
const useMock = (import.meta.env.VITE_USE_MOCK ?? "true") === "true";

const loadingNodes = ref(false);
const loadingAssets = ref(false);
const loadingQuestions = ref(false);
const treeData = ref<TreeNode[]>([]);
const assets = ref<ContentAssetItem[]>([]);
const questions = ref<ClientQuestionItem[]>([]);
const errorMessage = ref("");

const filters = reactive({
  textbookVersionId: "",
  subjectCode: "",
  gradeCode: "",
  questionType: "",
  selectedNodeId: ""
});

const questionTypeOptions = ["CHOICE", "FILL", "SOLUTION"];

const nodeCount = computed(() => treeData.value.length);

function buildTree(nodes: CurriculumNodeDictionaryItem[]): TreeNode[] {
  const childrenMap = new Map<string, CurriculumNodeDictionaryItem[]>();
  const roots: CurriculumNodeDictionaryItem[] = [];
  for (const node of nodes) {
    if (node.parentNodeId) {
      const list = childrenMap.get(node.parentNodeId) ?? [];
      list.push(node);
      childrenMap.set(node.parentNodeId, list);
    } else {
      roots.push(node);
    }
  }
  const convert = (node: CurriculumNodeDictionaryItem): TreeNode => {
    const children = (childrenMap.get(node.nodeId) ?? [])
      .sort((a, b) => a.sortOrder - b.sortOrder)
      .map(convert);
    return {
      label: `${node.nodeName} (${node.nodeType})`,
      nodeId: node.nodeId,
      ...(children.length > 0 ? { children } : {})
    };
  };
  return roots.sort((a, b) => a.sortOrder - b.sortOrder).map(convert);
}

async function loadNodes(): Promise<void> {
  loadingNodes.value = true;
  errorMessage.value = "";
  try {
    const result = useMock
      ? learningFixtures.nodes
      : await api.getCurriculumNodes({
          textbookVersionId: filters.textbookVersionId || undefined,
          subjectCode: filters.subjectCode || undefined
        });
    treeData.value = buildTree(result);
    if (!filters.selectedNodeId && result.length > 0) {
      filters.selectedNodeId = result[0].nodeId;
    }
  } catch (error) {
    errorMessage.value = error instanceof Error ? error.message : "课程树加载失败";
  } finally {
    loadingNodes.value = false;
  }
}

async function loadAssets(): Promise<void> {
  loadingAssets.value = true;
  try {
    const result = useMock
      ? learningFixtures.assets.filter((asset) => !filters.selectedNodeId || asset.curriculumNodeId === filters.selectedNodeId)
      : await api.getClientContentAssets({
          curriculumNodeId: filters.selectedNodeId || undefined
        });
    assets.value = result;
  } finally {
    loadingAssets.value = false;
  }
}

async function loadQuestions(): Promise<void> {
  loadingQuestions.value = true;
  try {
    const result = useMock
      ? learningFixtures.questions.filter(
          (item) =>
            (!filters.subjectCode || item.subjectCode === filters.subjectCode) &&
            (!filters.gradeCode || item.gradeCode === filters.gradeCode) &&
            (!filters.questionType || item.questionType === filters.questionType)
        )
      : await api.getClientQuestions({
          subjectCode: filters.subjectCode || undefined,
          gradeCode: filters.gradeCode || undefined,
          questionType: filters.questionType || undefined
        });
    questions.value = result;
  } finally {
    loadingQuestions.value = false;
  }
}

async function reloadAll(): Promise<void> {
  await loadNodes();
  await Promise.all([loadAssets(), loadQuestions()]);
}

function syncQuery(): void {
  router.replace({
    query: {
      textbookVersionId: filters.textbookVersionId || undefined,
      subjectCode: filters.subjectCode || undefined,
      gradeCode: filters.gradeCode || undefined,
      questionType: filters.questionType || undefined,
      nodeId: filters.selectedNodeId || undefined
    }
  });
}

function applyQuery(): void {
  filters.textbookVersionId = String(route.query.textbookVersionId ?? "");
  filters.subjectCode = String(route.query.subjectCode ?? "");
  filters.gradeCode = String(route.query.gradeCode ?? "");
  filters.questionType = String(route.query.questionType ?? "");
  filters.selectedNodeId = String(route.query.nodeId ?? "");
}

function handleNodeClick(data: TreeNode): void {
  filters.selectedNodeId = data.nodeId;
}

watch(
  () => [filters.textbookVersionId, filters.subjectCode, filters.gradeCode, filters.questionType],
  async () => {
    syncQuery();
    await reloadAll();
  }
);

watch(
  () => filters.selectedNodeId,
  async () => {
    syncQuery();
    await loadAssets();
  }
);

onMounted(async () => {
  applyQuery();
  if (!filters.subjectCode) {
    filters.subjectCode = "MATH";
  }
  if (!filters.gradeCode) {
    filters.gradeCode = "GRADE_9";
  }
  await reloadAll();
});
</script>

<template>
  <section class="learning">
    <el-card shadow="never">
      <div class="filters">
        <el-input v-model="filters.textbookVersionId" placeholder="教材版本 ID" clearable />
        <el-select v-model="filters.subjectCode" placeholder="学科" clearable>
          <el-option label="数学" value="MATH" />
          <el-option label="语文" value="CHINESE" />
          <el-option label="英语" value="ENGLISH" />
        </el-select>
        <el-select v-model="filters.gradeCode" placeholder="年级" clearable>
          <el-option label="九年级" value="GRADE_9" />
          <el-option label="八年级" value="GRADE_8" />
        </el-select>
        <el-select v-model="filters.questionType" placeholder="题型" clearable>
          <el-option v-for="item in questionTypeOptions" :key="item" :label="item" :value="item" />
        </el-select>
        <el-button type="primary" @click="reloadAll">刷新</el-button>
      </div>
    </el-card>

    <el-alert v-if="errorMessage" :title="errorMessage" type="error" show-icon />

    <div class="panels">
      <section class="panel">
        <h3>课程树</h3>
        <el-skeleton v-if="loadingNodes" :rows="6" animated />
        <el-tree
          v-else
          :data="treeData"
          node-key="nodeId"
          :expand-on-click-node="false"
          @node-click="handleNodeClick"
        />
        <p class="meta">根节点数：{{ nodeCount }}</p>
      </section>

      <section class="panel">
        <h3>图文内容</h3>
        <el-skeleton v-if="loadingAssets" :rows="8" animated />
        <el-empty v-else-if="assets.length === 0" description="暂无图文内容" />
        <div v-else class="list">
          <article v-for="asset in assets" :key="asset.assetId" class="item">
            <h4>{{ asset.title }}</h4>
            <p>{{ asset.bodyMarkdown }}</p>
          </article>
        </div>
      </section>

      <section class="panel">
        <h3>题目列表</h3>
        <el-skeleton v-if="loadingQuestions" :rows="8" animated />
        <el-empty v-else-if="questions.length === 0" description="暂无题目" />
        <div v-else class="list">
          <article v-for="question in questions" :key="question.questionId" class="item">
            <div class="item-head">
              <strong>{{ question.questionType }}</strong>
              <el-tag size="small">{{ question.difficultyLevel }}</el-tag>
            </div>
            <p>{{ question.stemMarkdown }}</p>
          </article>
        </div>
      </section>
    </div>
  </section>
</template>

<style scoped>
.learning {
  display: grid;
  gap: 12px;
}

.filters {
  display: grid;
  grid-template-columns: 180px 140px 140px 140px 88px;
  gap: 8px;
}

.panels {
  display: grid;
  grid-template-columns: 1fr 1.6fr 1.4fr;
  gap: 12px;
  min-height: 560px;
}

.panel {
  background: #fff;
  border: 1px solid #dcdfe6;
  padding: 12px;
}

.panel h3 {
  margin: 0 0 10px;
  font-size: 16px;
}

.meta {
  margin: 10px 0 0;
  color: #909399;
  font-size: 12px;
}

.list {
  display: grid;
  gap: 8px;
}

.item {
  border: 1px solid #ebeef5;
  padding: 10px;
}

.item h4,
.item p {
  margin: 0;
}

.item p {
  margin-top: 6px;
  color: #606266;
}

.item-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
