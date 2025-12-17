import {createRouter, createWebHistory} from 'vue-router'
import QuizLoginView from "@/view/QuizLoginView.vue";
import QuizMainView from "@/view/QuizMainView.vue";
import QuizParticipantView from "@/view/QuizParticipantView.vue";

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
      path: "/game/:gameId/participant",
      name: "QuizParticipantView",
      component: QuizParticipantView
    }
  ],
})

export default router
