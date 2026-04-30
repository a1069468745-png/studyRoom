import { createRouter, createWebHistory } from "vue-router";

import ExamView from "../views/ExamView.vue";
import LearningView from "../views/LearningView.vue";
import ResultView from "../views/ResultView.vue";

export const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: "/",
      redirect: "/client/learning"
    },
    {
      path: "/client/learning",
      name: "client-learning",
      component: LearningView,
      meta: {
        title: "学习链路联调"
      }
    },
    {
      path: "/client/exam",
      name: "client-exam",
      component: ExamView,
      meta: {
        title: "考试页骨架"
      }
    },
    {
      path: "/client/result",
      name: "client-result",
      component: ResultView,
      meta: {
        title: "结果页骨架"
      }
    }
  ]
});
