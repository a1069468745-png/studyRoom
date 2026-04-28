<script setup lang="ts">
import { computed } from "vue";
import { RouterLink, RouterView, useRoute } from "vue-router";

const route = useRoute();
const pageTitle = computed(() => route.meta.title ?? "Admin Console");
</script>

<template>
  <el-container class="shell">
    <el-header class="shell__header">
      <div>
        <p class="shell__eyebrow">DEV-001 Frontend Workspace</p>
        <h1>{{ pageTitle }}</h1>
      </div>
      <nav class="shell__nav">
        <RouterLink to="/">Home</RouterLink>
      </nav>
    </el-header>
    <el-main class="shell__main">
      <RouterView />
    </el-main>
  </el-container>
</template>

<style scoped>
.shell {
  min-height: 100vh;
  background:
    radial-gradient(circle at top right, rgba(230, 162, 60, 0.2), transparent 32%),
    linear-gradient(180deg, #fff9f1 0%, #fffdf8 100%);
}

.shell__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px 32px;
}

.shell__eyebrow {
  margin: 0 0 8px;
  color: #d97706;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.14em;
  text-transform: uppercase;
}

.shell__header h1 {
  margin: 0;
  font-size: 28px;
}

.shell__nav a {
  color: #1f2d3d;
  font-weight: 600;
  text-decoration: none;
}

.shell__main {
  padding: 0 32px 32px;
}
</style>

