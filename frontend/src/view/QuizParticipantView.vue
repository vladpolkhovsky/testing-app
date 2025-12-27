<!-- ParticipantView.vue -->
<script setup lang="ts">
import LiquidGlass from "@/component/LiquidGlass.vue";
import UserAnswerComponent from "@/component/UserAnswerComponent.vue";
import { type AnswerOption, getRandomAnswers } from "@/model/AnswerOption.ts";
import { inject, onMounted, provide, ref, useTemplateRef } from "vue";
import type { LocalStorageApi } from "@/service/LocalStorageApi.ts";
import type { User } from "@/model/User.ts";
import { useRoute, useRouter } from "vue-router";
import { SocketService } from "@/service/SocketService.ts";
import type {
  QuizInitializationMessage,
  QuizRoundMessage,
  QuizShowNewQuestionMessage
} from "@/model/stomp-messages.ts";
import type { Question } from "@/model/Question.ts";
import { User as UserIcon, Clock, Trophy } from "lucide-vue-next";

const localStorageApi = inject<LocalStorageApi>("LocalStorageApi");

let socketService: SocketService | null = null;
let isFirstCall = true;

const user = ref<User>();
user.value = localStorageApi?.getUserInformation();

const isRoundStarted = ref(false);
const answers = ref<AnswerOption[]>();
const userAnswerComponentRef = useTemplateRef<InstanceType<typeof UserAnswerComponent>>("userAnswerComponentRef")

let router = useRouter();
let route = useRoute();

let roomId = route.params.gameId as string;

socketService = new SocketService(roomId!!, user.value);
provide("SocketService", socketService);

onMounted(() => {

  if (!user.value) {
    router.push({
      name: "QuizLoginView",
      query: {
        hardRedirectTo: window.location.toString()
      }
    })
  }

  socketService.setOnQuizInitializationMessageCallback(handleInitMessage);
  socketService.setOnQuizShowNewQuestionMessageCallback(handleShowNewQuestionMessage);
  socketService.setOnEndQuizQuestionCallback(handleQuizRoundStopMessage);
  socketService.setOnStartQuizQuestionCallback(handleQuizRoundStartMessage);

  socketService.activate();
});

const handleInitMessage = (message: QuizInitializationMessage) => {
  if (!isFirstCall) {
    return;
  }

  isFirstCall = false;

  localStorageApi?.setAnswers(message.questionId, message.answers);

  answers.value = message.answers;

  isRoundStarted.value = message.roundStarted;
}

const handleShowNewQuestionMessage = (message: QuizShowNewQuestionMessage) => {
  localStorageApi?.setQuestion(message.questionId, message.question);
  localStorageApi?.setAnswers(message.questionId, message.answers);

  answers.value = message.answers;

  userAnswerComponentRef.value?.reset();
  userAnswerComponentRef.value?.updateOptions(message.answers);
}

const handleQuizRoundStartMessage = (message: QuizRoundMessage) => {
  const question: Question = localStorageApi?.getQuestion(message.questionId)!!;
  const answers: AnswerOption[] = localStorageApi?.getAnswers(message.questionId)!!;

  userAnswerComponentRef.value?.updateOptions(answers);

  isRoundStarted.value = true;
}

const handleQuizRoundStopMessage = (message: QuizRoundMessage) => {
  userAnswerComponentRef.value?.showOnlyCorrect();
}
</script>

<template>
  <div class="w-full max-w-2xl mx-auto mt-3 px-3">
    <!-- User Info Header - Mobile Optimized -->
    <LiquidGlass class="bg-white/70 w-full lg:max-w-lg mx-auto sm:p-8 rounded-2xl">
      <div class="flex flex-col gap-10">
        <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-3 sm:gap-4">
          <div class="flex items-center gap-3">
            <div class="flex-1 min-w-0">
              <h1 class="text-lg sm:text-xl md:text-2xl font-bold text-gray-800 truncate">{{ user?.username }}</h1>
              <p class="text-xs sm:text-sm text-gray-600">Участник викторины</p>
            </div>
          </div>
          <div class="text-xs sm:text-sm text-gray-500 font-mono bg-gray-100/50 px-2 py-1 rounded-lg">
            Комната: <span class="font-bold">{{ roomId }}</span>
          </div>
        </div>

        <!-- Main Content Area -->
        <div v-if="isRoundStarted" class="flex-1">
          <UserAnswerComponent :options="answers" ref="userAnswerComponentRef"/>
        </div>

        <!-- Waiting State -->
        <div class="flex flex-col items-center justify-center gap-4 sm:gap-5 md:gap-6" v-else>
          <div
              class="mx-auto w-12 h-12 sm:w-16 sm:h-16 md:w-20 md:h-20 rounded-full bg-gradient-to-r from-blue-500/20 to-purple-500/20 flex items-center justify-center">
            <Clock :size="32" class="sm:w-8 sm:h-8 md:w-10 md:h-10 text-blue-500"/>
          </div>

          <div class="text-center px-2">
            <h2 class="text-xl sm:text-2xl md:text-3xl font-bold text-gray-800 mb-1 sm:mb-2">Ожидание вопроса</h2>
            <p class="text-sm sm:text-base text-gray-600">Следующий вопрос скоро появится на экране</p>
          </div>
        </div>

        <!-- Connection Status -->
        <div class="flex items-center justify-center gap-2 text-xs sm:text-sm text-gray-500 px-2 py-1">
          <div class="w-2 h-2 rounded-full bg-green-500 animate-pulse"></div>
          <span class=" text-gray-500">Подключено к викторине</span>
        </div>
      </div>
    </LiquidGlass>
  </div>
</template>

<style scoped>
.safe-area-inset {
  padding-top: env(safe-area-inset-top);
  padding-bottom: env(safe-area-inset-bottom);
  padding-left: env(safe-area-inset-left);
  padding-right: env(safe-area-inset-right);
}

/* Better touch targets for mobile */
@media (max-width: 640px) {
  * {
    -webkit-tap-highlight-color: transparent;
  }

  button,
  [role="button"],
  .clickable {
    min-height: 44px;
    min-width: 44px;
  }
}
</style>