import {createRouter, createWebHistory} from 'vue-router'
import QuizLoginView from "@/view/QuizLoginView.vue";
import QuizMainView from "@/view/QuizMainView.vue";
import QuizParticipantView from "@/view/QuizParticipantView.vue";
import QuizListView from "@/view/QuizListView.vue";
import QuizEditorView from "@/view/QuizEditorView.vue";
import QuizEnterView from "@/view/QuizEnterView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/login-user",
      name: "QuizEnterView",
      component: QuizEnterView
    },
    {
      path: "/editor/:quizId",
      name: "QuizEditorView",
      component: QuizEditorView
    },
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
      path: "/g/:gameId",
      name: "QuizMainView-short",
      component: QuizMainView
    },
    {
      path: "/",
      name: "QuizListView",
      component: QuizListView
    },
    {
      path: "/game/:gameId/p",
      name: "QuizParticipantView",
      component: QuizParticipantView
    },
    {
      path: "/g/:gameId/p",
      name: "QuizParticipantView-short",
      component: QuizParticipantView
    }
  ],
})

export default router
