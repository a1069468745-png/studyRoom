import { createRouter, createWebHistory } from "vue-router";

import HomeView from "../views/HomeView.vue";

export const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: "/",
      name: "client-home",
      component: HomeView,
      meta: {
        title: "Teacher / Student Workspace"
      }
    }
  ]
});

