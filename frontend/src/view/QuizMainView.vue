<template>
  <div class="flex flex-col shrink items-center p-3 w-full xl:w-6xl mx-auto">
    <LiquidGlass class="w-full m-5 xm:w-3xl p-8 flex flex-col gap-8">
      <div class="w-full max-w-6xl mx-auto">
      <!-- Header -->
      <LiquidGlass class="h-fit p-6" style="z-index: 10;">
        <div class="flex flex-col lg:flex-row justify-between items-start lg:items-center gap-4">
          <!-- Title -->
          <div class="flex-1">
            <h1 class="text-2xl pangolin-regular text-gray-800 mb-2">
              {{ gameState.title }}
            </h1>
            <div class="text-sm text-gray-600 font-medium">
              ID комнаты: <span class="font-mono text-blue-600">{{ roomId }}</span>
            </div>
          </div>

          <!-- Status & Controls -->
          <div class="flex flex-wrap items-center gap-3">
            <!-- Status Badges -->
            <div
                class="text-2xl text-nowrap px-4 py-2 border rounded-xl font-medium tektur-badge transition-all duration-300"
                :class="{
                'bg-blue-500/50 border-blue-300 text-blue-800': !gameState.gameStarted,
                'bg-green-400/50 border-green-300 text-green-800': gameState.gameStarted && !gameState.gameFinished,
                'bg-red-400/50 border-red-300 text-red-800': gameState.gameFinished
              }"
            >
              <span v-if="!gameState.gameStarted">Ожидание игроков</span>
              <span v-else-if="gameState.gameStarted && !gameState.gameFinished">
                Раунд {{ gameState.currentRound }} / {{ gameState.maxRounds }}
              </span>
              <span v-else>Игра завершена</span>
            </div>

            <!-- QR Code Button -->
            <div class="relative">
              <button
                  @mouseenter="QRCodeStateChanger(true)"
                  @mouseleave="QRCodeStateChanger(false)"
                  class="p-2 rounded-xl bg-gradient-to-r from-blue-500 to-blue-600 text-white hover:from-blue-600 hover:to-blue-700 transition-all duration-300 hover:shadow-lg"
              >
                <QrCode :size="28"/>
              </button>

              <!-- QR Code Popup -->
              <div
                  id="join-qr"
                  :class="qrCodeState.show ? 'absolute top-full left-12 mt-2 z-50 bg-white rounded-xl shadow-xl border' : 'hidden'"
              >
                <div class="text-sm font-medium text-gray-700 mb-1 text-center">Сканируйте для участия</div>
              </div>
            </div>
          </div>
        </div>
      </LiquidGlass>

      <!-- Main Content Area -->
      <LiquidGlass class="h-full relative min-h-[500px] p-6" style="z-index: 0;">
        <TransitionGroup name="list">
          <!-- Game Finished Screen -->
          <div
              v-if="gameState.gameFinished"
              key="game-finished"
              class="flex flex-col items-center justify-center h-full gap-5 p-3"
          >
            <div class="text-center text-2xl bg-gradient-to-l from-sky-700/80 to-green-300/50 p-4 border rounded-xl font-bold tektur-badge">
              Игра завершена
            </div>
            <div class="text-xl text-gray-600 text-center">
              Спасибо за участие!
            </div>
          </div>

          <!-- Active Round -->
          <div
              v-else-if="gameState.roundStarted"
              key="active-round"
              class="flex flex-col gap-5"
          >
            <QuestionComponent
                :text="gameState?.question?.text!"
                :image-id="gameState?.question?.imageId"
            />
            <AnswerComponent
                :options="gameState?.answers!"
                ref="answerComponentRef"
            />
          </div>

          <!-- Rating/Results -->
          <QuizRatingView
              v-else
              key="rating"
              :items="gameState.rating"
              @round-update-ended="quizRatingShowNextRoundButton()"
              ref="quizRatingRef"
          />
        </TransitionGroup>
      </LiquidGlass>
    </div>
      <Toaster :duration="5000" :expand="true" :visible-toasts="15" />
    </LiquidGlass>
  </div>
</template>

<script setup lang="ts">
import { useRoute } from "vue-router";
import QRCode from "qrcode"
import { inject, onMounted, provide, reactive, useTemplateRef, } from "vue";
import LiquidGlass from "@/component/LiquidGlass.vue";
import { SocketService } from "@/service/SocketService.ts";
import { LocalStorageApi } from "@/service/LocalStorageApi.ts";
import { SoundEngine } from "@/service/SoundEngine.ts";
import { QrCode } from "lucide-vue-next";
import QuestionComponent from "@/component/QuestionComponent.vue";
import type { Question } from "@/model/Question.ts";
import { type AnswerOption, getRandomAnswers } from "@/model/AnswerOption.ts";
import type { RatingItem } from "@/model/RatingItem.ts";
import AnswerComponent from "@/component/AnswerComponent.vue";
import QuizRatingView from "@/view/QuizRatingView.vue";

import 'vue-sonner/style.css'
import { Toaster, toast } from 'vue-sonner'

SoundEngine.initialize();

const route = useRoute();

const roomId = route.params.gameId as string;

const socketService = new SocketService(roomId);
provide("SocketService", socketService);

const localStorageApi = inject("LocalStorageApi") as LocalStorageApi;

const quizRatingRef = useTemplateRef<InstanceType<typeof QuizRatingView>>("quizRatingRef")
const answerComponentRef = useTemplateRef<InstanceType<typeof AnswerComponent>>("answerComponentRef")

interface QuizState {
  gameStarted: boolean;
  roundStarted: boolean;
  gameFinished: boolean;

  title: string;

  currentRound: number;
  maxRounds: number;

  questionId?: string;
  quizId: string;

  question?: Question;
  answers?: AnswerOption[];

  rating: RatingItem[];
}

const gameState = reactive<QuizState>({
  gameStarted: false,
  roundStarted: false,
  gameFinished: false,
  title: 'Загрузка...',
  currentRound: 0,
  maxRounds: 0,
  quizId: roomId,
  rating: []
});

(window as any).emulateGameStarted = () => {
  gameState.gameStarted = true;
  gameState.roundStarted = true;
  gameState.question = {
    text: Date().toString()
  }
  gameState.answers = getRandomAnswers();
};

(window as any).emulateUpdateAnswers = () => {
  gameState.roundStarted = true;
  gameState.answers = getRandomAnswers();
};

(window as any).emulateRoundFinished = () => {
  gameState.roundStarted = false;
};

onMounted(() => {
  socketService.setOnConnectCallback(() => {
    socketService.notifyConnectionSilent()
  });

  socketService.setOnQuizInitializationMessageCallback(message => {
    gameState.title = message.title;

    gameState.currentRound = message.currentRound;
    gameState.maxRounds = message.maxRounds;

    gameState.gameStarted = message.gameStarted;
    gameState.roundStarted = message.roundStarted;
    gameState.gameFinished = message.gameFinished;

    gameState.question = message.question;
    gameState.answers = message.answers;

    gameState.rating = message.ratingItems;
    gameState.rating.forEach(item => {
      if (quizRatingRef.value) quizRatingRef.value.addUser(item);
    });

    if (quizRatingRef.value && !gameState.gameFinished) quizRatingRef.value.showStartGameButton();
  });

  socketService.setOnQuizShowNewQuestionMessageCallback(message => {
    gameState.question = message.question;
    gameState.answers = message.answers;
  });

  socketService.setOnStartQuizQuestionCallback(message => {
    const duration = message.duration;
    gameState.gameStarted = true;
    gameState.roundStarted = true;
    gameState.currentRound = message.nextRound;

    if (answerComponentRef.value) {
      answerComponentRef.value.reset()
    }
  });

  socketService.setOnEndQuizQuestionCallback(message => {
    gameState.gameFinished = message.gameFinished;
    gameState.currentRound = message.nextRound;
    if (gameState.question) {
      gameState.question.text = message.textAlternative ?? gameState.question.text;
      gameState.question.imageId = message.imageAlternativeId ?? gameState.question.imageId;
    }
    if (answerComponentRef.value) {
      answerComponentRef.value.showOnlyCorrect()
    }
  });

  socketService.setOnQuizUpdateRatingMessageCallback(message => {
    gameState.roundStarted = false;
    setTimeout(() => {
      if (quizRatingRef.value) quizRatingRef.value.updateModel(message.ratingItems);
    }, 1700)
  });

  socketService.setOnQuizAnswerSavedMessageCallback(message => {
    toast.success(`${message.username} ответил на вопрос`, {
      style: {
        background: '#6ee7b7',
        width: 'fit-content',
        'font-size': '18px',
        'min-width': '150px'
      }
    });
  });

  socketService.activate();
});

const quizRatingShowNextRoundButton = () => {
  if (quizRatingRef.value && !gameState.gameFinished) quizRatingRef.value.showStartGameButton();
}

interface QrCodeState {
  show: boolean;
  location: string;
}

const qrCodeState = reactive<QrCodeState>({
  show: false,
  location: window.location + "/participant" as string,
});

const QRCodeStateChanger = (show: boolean) => {
  console.log("QRCodeStateChange", show)

  if (show) {
    QRCode.toCanvas(qrCodeState.location, { errorCorrectionLevel: 'H' }, function (err, canvas) {
      if (err) throw err;
      canvas.classList.add("border", "rounded-xl")
      document.getElementById("join-qr")?.replaceChildren();
      document.getElementById("join-qr")?.appendChild(canvas)
    })
  }

  qrCodeState.show = show;
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Pangolin&family=Tektur:wght@400..900&display=swap');

.tektur-badge {
  font-family: "Tektur", sans-serif;
  font-optical-sizing: auto;
  font-weight: bold;
  font-variation-settings: "wdth" 100;
}

.pangolin-regular {
  font-family: "Pangolin", cursive;
  font-weight: 400;
  font-style: normal;
}

.qr-position {
  top: 25px;
  right: 25px;
}

.list-move,
.list-enter-active,
.list-leave-active {
  transition: all 0.5s ease;
}

.list-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}

.list-leave-to {
  opacity: 0;
  transform: translateX(30px);
}

.list-leave-active {
  position: absolute;
}
</style>