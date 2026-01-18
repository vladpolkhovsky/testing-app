<template>
  <LiquidGlass class="w-full h-100vh">
    <TransitionGroup name="list">
      <div key="header">
        <LiquidGlass key="header" v-if="state.context">
          <div v-if="canDisplayTimer" class="min-h-15 flex justify-between">
            <Timer :seconds="displayTimerSeconds" class="mx-auto" />
          </div>
          <div v-else
            class="flex justify-between items-center gap-3 ps-3 pe-3 w-full min-h-15"
          >
            <div class="inline-flex gap-3 items-center">
              <div
                class="group transition-all duration-150 hover:scale-105 hover:text-red-700"
              >
                <a href="/"><LucideHome :size="32" /></a>
              </div>
              <div class="tektur-badge text-xl me-3">
                {{ state.context.quiz.title }}
              </div>
              <div
                v-if="state.context.gameFinished"
                class="border p-2 tektur-badge text-md rounded-2xl border-blue-700 bg-radial from-blue-400 to-blue-400/50 hover:bg-blue-500 transition-all duration-150 hover:scale-105"
              >
                Игра заверешена
              </div>
              <div
                v-else-if="state.context.gameStarted"
                class="border p-2 tektur-badge text-md rounded-2xl border-green-700 bg-radial from-green-400 to-green-300/50 hover:bg-green-500 transition-all duration-150 hover:scale-105"
              >
                Раунд {{ state.context.currentRound }} /
                {{ state.context.maxRounds }}
              </div>
              <div
                v-else
                class="border p-2 tektur-badge text-md rounded-2xl border-red-700 bg-radial from-red-400 to-red-400/50 hover:bg-red-500 transition-all duration-150 hover:scale-105"
              >
                Ожидание начала игры
              </div>
            </div>
            <Timer
              :seconds="displayTimerSeconds"
              v-if="
                state.context && state.context.roundStarted && canDisplayTimer
              "
            />
            <div>
              <ul
                class="flex flex-wrap text-sm font-medium text-center text-body"
              >
                <li class="me-5">
                  <button
                    v-if="
                      selectedTab == 'game' &&
                      !state.context.roundStarted &&
                      !state.context.gameFinished &&
                      canDisplayNextRoundStart
                    "
                    @click="startRound()"
                    class="w-full tektur-badge text-2xl border rounded-2xl p-3 bg-linear-to-r from-blue-400/30 to-indigo-500/50 hover:bg-indigo-500 hover:scale-105 transition-all duration-200"
                  >
                    {{
                      state.context.gameStarted
                        ? "Следующий раунд"
                        : "Начать игру"
                    }}
                  </button>
                </li>
                <li v-for="tab in tabs" class="me-2">
                  <button
                    class="inline-block p-3 rounded-t-base tektur-badge text-xl transition-all duration-150 hover:scale-110"
                    :class="{
                      'border-b-4 bg-amber-200 rounded-tl-xl rounded-tr-xl':
                        selectedTab == tab.type,
                    }"
                    @click="selectedTab = tab.type"
                  >
                    {{ tab.title }}
                  </button>
                </li>
              </ul>
            </div>
          </div>
        </LiquidGlass>
        <div key="loading" class="flex justify-center" v-else>
          <Loader :size="72" class="spinner" />
        </div>
      </div>
      <TransitionGroup key="main" name="list" tag="div" v-if="state.context">
        <QuizNewRating
          key="rating"
          ref="ratingScreenRef"
          v-if="canDisplayRating && selectedTab == 'game' && currentQuestion"
          :participants="state.context.participants"
          :rating="state.showRating"
        />
        <QuizGameScreen
          :key="currentQuestion.id"
          ref="gameScreenRef"
          v-if="selectedTab == 'game' && currentQuestion"
          :question="currentQuestion"
        />
        <QuizParticipantTabView
          v-if="selectedTab == 'pariticipant' && state.context"
          @remove-participan="handleRemoveParticipant"
          :context="state.context"
        />
      </TransitionGroup>
    </TransitionGroup>
  </LiquidGlass>
</template>

<script setup lang="ts">
import LiquidGlass from "@/component/LiquidGlass.vue";
import { Loader, LucideHome } from "lucide-vue-next";
import { computed, reactive, ref, useTemplateRef, watch } from "vue";
import QuizGameScreen from "./game-screen/QuizGameScreen.vue";
import { useRoute } from "vue-router";
import { useFetch } from "@vueuse/core";
import type { QuizContextDto } from "@/model/quiz/QuizContextDto";
import type { QuizQuestionDto } from "@/model/quiz/QuizQuestionDto";
import { SocketService } from "@/service/SocketService";
import { toast } from "vue-sonner";
import QuizParticipantTabView from "./game-screen/QuizParticipantTabView.vue";
import QuizNewRating from "./game-screen/QuizNewRating.vue";
import Timer from "@/component/Timer.vue";
import type { User } from "@/model/User";

type TabState = "game" | "pariticipant";

const tabs: { type: TabState; title: string }[] = [
  {
    title: "Игра",
    type: "game",
  },
  {
    title: "Участники",
    type: "pariticipant",
  },
];

const selectedTab = ref<TabState>("game");

const route = useRoute();
const contextId = route.params.gameId as string;

const state = reactive<{
  loading: boolean;
  context?: QuizContextDto;
  showRating: Record<string, number>;
}>({
  loading: true,
  showRating: {},
});

const canDisplayNextRoundStart = ref(true);
const canDisplayRating = ref(true);
const canDisplayTimer = ref(false);
const displayTimerSeconds = ref(0);

const socketService: SocketService = new SocketService(contextId);

socketService.setOnStartGameCallback((message) => {
  state.context!.gameStarted = true;
});

socketService.setOnStartRoundCallback((message) => {
  state.context!.roundStarted = true;
});

socketService.setOnShowQuestionCallback((message) => {
  state.context!.currentQuestionId = message.questionId;
  state.context!.currentRound = message.currentRound;
  canDisplayNextRoundStart.value = false;
  canDisplayRating.value = false;
  canDisplayTimer.value = true;
  displayTimerSeconds.value = message.roundTime;
});

socketService.setOnShowQuestionAnswerCallback((message) => {
  gameScreenRef.value?.showCorrectAnswer();
  canDisplayTimer.value = false;
});

socketService.setOnStopGameCallback((message) => {
  state.context!.gameFinished = true;
});

socketService.setOnStopRoundCallback((message) => {
  state.context!.roundStarted = false;
});

socketService.setOnShowRatingCallback((message) => {
  state.showRating = message.rating;
  canDisplayRating.value = true;
});

socketService.setOnShowUpdatedRatingCallback((message) => {
  canDisplayNextRoundStart.value = true;
  state.context!.questionToUserToRating[state.context!.currentQuestionId] =
    message.rating;
  ratingScreenRef.value?.applyChanges(message.rating);
});

socketService.setOnUserConnectedCallbackCallback((message) => {
  state.context?.participants.push(message.user);
  toast.success(`${message.user.username} присоединился к игре!`, {
    style: {
      background: "#ffcc00",
      width: "fit-content",
      "font-size": "18px",
      "min-width": "150px",
    },
  });
});

socketService.setOnNotifySavedCallback((message) => {
  toast.success(message.text, {
    style: {
      background: "#6ee7b7",
      width: "fit-content",
      "font-size": "18px",
      "min-width": "150px",
    },
  });
});

const startRound = () => {
  socketService.startQuiz();
};

useFetch(`/api/quiz/editor/context/${contextId}`)
  .json<QuizContextDto>()
  .then((value) => {
    state.context = value.data.value!;
    state.showRating =
      value.data.value!.questionToUserToRating[
        value.data.value!.currentQuestionId
      ] ?? {};
    canDisplayRating.value =
      value.data.value!.gameFinished ||
      (!value.data.value!.roundStarted && value.data.value!.gameStarted);
    socketService.activate();
  });

const currentQuestion = computed<QuizQuestionDto | undefined>(() => {
  console.log("computed question", state.context);
  return state.context?.quiz.questions.filter(
    (q) => q.id == state.context?.currentQuestionId
  )[0];
});

const gameScreenRef =
  useTemplateRef<InstanceType<typeof QuizGameScreen>>("gameScreenRef");

const ratingScreenRef =
  useTemplateRef<InstanceType<typeof QuizNewRating>>("ratingScreenRef");

(window as any).updateCurrentQuestion = (id: string) => {
  state.context!.currentQuestionId = id;
};

(window as any).showCorrect = () => {
  gameScreenRef.value?.showCorrectAnswer();
};

const handleRemoveParticipant = (user: User) => {
  state.context!.participants = state.context!.participants.filter(
    (u) => u.userId != user.userId
  );
  toast.error("Удалён пользователь " + user.username);
};
</script>

<style scoped>
.list-move,
.list-enter-active,
.list-leave-active {
  transition: all 0.7s ease;
}

.list-enter-from {
  opacity: 0;
  transform: translateX(-300px);
}

.list-leave-to {
  opacity: 0;
  transform: translateX(300px);
}

.list-leave-active {
  position: absolute;
}

.spinner {
  animation: spin 2s ease-in-out infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
    scale: 1;
  }
  50% {
    transform: rotate(180deg);
    scale: 1.3;
  }
  to {
    transform: rotate(360deg);
    scale: 1;
  }
}
</style>
