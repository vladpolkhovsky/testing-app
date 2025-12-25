<script setup lang="ts">

import {useRoute} from "vue-router";
import QRCode from "qrcode"
import {inject, onMounted, provide, reactive, useTemplateRef,} from "vue";
import LiquidGlass from "@/component/LiquidGlass.vue";
import {SocketService} from "@/service/SocketService.ts";
import {LocalStorageApi} from "@/service/LocalStorageApi.ts";
import {SoundEngine} from "@/service/SoundEngine.ts";
import {QrCode} from "lucide-vue-next";
import QuestionComponent from "@/component/QuestionComponent.vue";
import type {Question} from "@/model/Question.ts";
import {type AnswerOption, getRandomAnswers} from "@/model/AnswerOption.ts";
import type {RatingItem} from "@/model/RatingItem.ts";
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
    QRCode.toCanvas(qrCodeState.location, {errorCorrectionLevel: 'H'}, function (err, canvas) {
      if (err) throw err;
      canvas.classList.add("border", "rounded-xl")
      document.getElementById("join-qr")?.replaceChildren();
      document.getElementById("join-qr")?.appendChild(canvas)
    })
  }

  qrCodeState.show = show;
}
</script>

<template>
  <div class="w-full p-3">
    <div class="w-full h-full xl:max-w-7xl xl:mx-auto flex flex-col gap-5">

      <LiquidGlass class="h-fit flex justify-between items-center" style="z-index: 10;">
        <div class="text-3xl pangolin-regular">{{ gameState.title }}</div>
        <div class="flex justify-between items-center gap-5">
          <div class="text-2xl text-nowrap px-3 py-1 bg-blue-500/50 border rounded-xl font-medium tektur-badge" v-if="!gameState.gameStarted">
            Ожидание игроков
          </div>
          <div class="text-2xl text-nowrap px-3 py-1 bg-green-400/50 border rounded-xl font-medium tektur-badge" v-if="gameState.gameStarted && !gameState.gameFinished">
            Раунд {{ gameState.currentRound }} / {{ gameState.maxRounds }}
          </div>
          <div class="text-2xl text-nowrap px-3 py-1 bg-red-400/50 border rounded-xl font-medium tektur-badge" v-if="gameState.gameFinished">
            Игра завершена
          </div>
          <div class="relative">
            <QrCode @mouseenter="QRCodeStateChanger(true)" @mouseleave="QRCodeStateChanger(false)" :size="32"/>
            <div id="join-qr" :class="qrCodeState.show ? 'absolute z-0 qr-position' : 'hidden'" style="z-index: 10; !important;"/>
          </div>
        </div>
      </LiquidGlass>

      <LiquidGlass class="h-full relative" style="z-index: 0;">
        <TransitionGroup name="list">
          <div class="flex flex-col gap-5 p-3" key="gameState.gameFinished" v-if="gameState.gameFinished">
            <div class="text-center text-3xl bg-gradient-to-l from-sky-700/80 to-green-300/50 p-3 border rounded-xl font-bold tektur-badge">Игра завершена</div>
          </div>
          <LiquidGlass class="flex flex-col gap-5" key="gameState.questionId" v-if="gameState.roundStarted">
            <QuestionComponent :text="gameState?.question?.text!" :image-id="gameState?.question?.imageId"/>
            <AnswerComponent :options="gameState?.answers!" ref="answerComponentRef"/>
          </LiquidGlass>
          <QuizRatingView key="rating" :items="gameState.rating" v-if="!gameState.roundStarted" @round-update-ended="quizRatingShowNextRoundButton()" ref="quizRatingRef"/>
        </TransitionGroup>
      </LiquidGlass>
    </div>
  </div>

  <Toaster :duration="5000" :expand="true" :visible-toasts="15" />
</template>

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

.list-move, /* apply transition to moving elements */
.list-enter-active,
.list-leave-active {
  transition: all 1.5s ease;
}

.list-enter-from {
  opacity: 0;
  transform: translateX(-1300px);
}

.list-leave-to {
  opacity: 0;
  transform: translateX(+1300px);
}

/* ensure leaving items are taken out of layout flow so that moving
   animations can be calculated correctly. */
.list-leave-active {
  position: absolute;
}

</style>