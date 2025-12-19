import {createRouter, createWebHistory} from 'vue-router'
import QuizLoginView from "@/view/QuizLoginView.vue";
import QuizMainView from "@/view/QuizMainView.vue";
import QuizParticipantView from "@/view/QuizParticipantView.vue";
import QuizListView from "@/view/QuizListView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/login",
      name: "QuizLoginView",
      component: QuizLoginView
    },
    {
      path: "/game/:gameId",
      name: "QuizMainView",
      component: QuizMainView
    },
    {
      path: "/",
      name: "QuizListView",
      component: QuizListView
    },
    {
      path: "/game/:gameId/participant",
      name: "QuizParticipantView",
      component: QuizParticipantView
    }
  ],
})

export default router
