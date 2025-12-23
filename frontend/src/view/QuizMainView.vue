<script setup lang="ts">

import {useRoute, useRouter} from "vue-router";
import QRCode from "qrcode"
import {inject, onMounted, onUpdated, provide, ref, useTemplateRef} from "vue";
import LiquidGlass from "@/component/LiquidGlass.vue";
import QuizRatingView from "@/view/QuizRatingView.vue";
import QuizQuestionView from "@/view/QuizQuestionView.vue";
import {SocketService} from "@/service/SocketService.ts";
import type {
  QuizInitializationMessage,
  QuizNewRatingMessage,
  QuizRoundMessage,
  QuizShowNewQuestionMessage
} from "@/model/stomp-messages.ts";
import {LocalStorageApi} from "@/service/LocalStorageApi.ts";
import type {Question} from "@/model/Question.ts";
import type {AnswerOption} from "@/model/AnswerOption.ts";
import type {RatingItem} from "@/model/RatingItem.ts";
import {SoundEngine} from "@/service/SoundEngine.ts";

SoundEngine.initialize();

type State = "WAITING" | "QUESTION" | "QUESTION_RESULT" | "RATING" | "FINISH";

const route = useRoute();
let router = useRouter();

const roomId = route.params.gameId as string;
const state = ref<State>("WAITING" as State);
const title = ref<string>();

const question = ref<Question>();
const answers = ref<AnswerOption[]>();
const ratingItems = ref<RatingItem[]>();

const localStorageApi = inject("LocalStorageApi")!! as LocalStorageApi;

const quizQuestionViewRef = useTemplateRef<InstanceType<typeof QuizQuestionView>>("quizQuestionViewRef")
const quizRatingViewRef = useTemplateRef<InstanceType<typeof QuizRatingView>>("quizRatingViewRef")

function generateQrCode() {
  QRCode.toCanvas(window.location + "/participant" as string, {errorCorrectionLevel: 'H'}, function (err, canvas) {
    if (err) throw err;
    canvas.classList.add("rounded-xl", "w-fit");
    document.getElementById("join-qr")?.replaceChildren();
    document.getElementById("join-qr")?.appendChild(canvas)
  })
}

const socketService = new SocketService(roomId);

provide("SocketService", socketService);

onMounted(() => {
  SoundEngine.nextTrack(true);

  let userInformation = localStorageApi.getUserInformation();

  if (!userInformation) {
    router.push({
      name: "QuizLoginView",
      query: {
        hardRedirectTo: window.location.toString()
      }
    })
  }

  generateQrCode();

  socketService.setOnQuizInitializationMessageCallback(handleQuizInitializationMessage);
  socketService.setOnQuizShowNewQuestionMessageCallback(handleShowNewQuestionMessage);
  socketService.setOnStartQuizQuestionCallback(handleQuizRoundStartMessage);
  socketService.setOnEndQuizQuestionCallback(handleQuizRoundStopMessage);
  socketService.setOnQuizUpdateRatingMessageCallback(handleQuizUpdateRatingMessageCallback);

  socketService.setOnConnectCallback(() => { socketService.notifyConnectionSilent(); });
  socketService.activate();
});

onUpdated(() => {
  generateQrCode();
})

const handleQuizUpdateRatingMessageCallback = (message: QuizNewRatingMessage) => {
  state.value = "RATING";
  setTimeout(() => {
    quizRatingViewRef.value?.updateModel(message.ratingItems);
    setTimeout(() => {
      ratingItems.value = message.ratingItems;
    }, 3000);
  }, 2000);
}

const handleQuizInitializationMessage = (message: QuizInitializationMessage) => {
  localStorageApi?.setQuestion(message.questionId, message.question);
  localStorageApi?.setAnswers(message.questionId, message.answers);

  title.value = message.title;
  question.value = message.question;
  answers.value = message.answers;
  ratingItems.value = message.ratingItems;

  if (!message.gameStarted) {
    state.value = "WAITING";

    quizRatingViewRef.value?.clearUsers();
    ratingItems.value.forEach((item) => {
      quizRatingViewRef.value?.addUser(item);
    });

    return;
  }

  if (message.gameFinished) {
    state.value = "FINISH"
    return;
  }

  quizRatingViewRef.value?.clearUsers();
  ratingItems.value.forEach((item) => {
    quizRatingViewRef.value?.addUser(item);
  });

  state.value = message.roundStarted ? "QUESTION" : "RATING";
}

const handleShowNewQuestionMessage = (message: QuizShowNewQuestionMessage) => {
  localStorageApi?.setQuestion(message.questionId, message.question);
  localStorageApi?.setAnswers(message.questionId, message.answers);

  question.value = message.question;
  answers.value = message.answers;

  quizQuestionViewRef.value?.updateQuestion(message.question);
  quizQuestionViewRef.value?.updateOptions(message.answers);

  state.value = "QUESTION";

  SoundEngine.nextTrack();
}

const handleQuizRoundStartMessage = (message: QuizRoundMessage) => {
  const question: Question = localStorageApi?.getQuestion(message.questionId)!!;
  const answers: AnswerOption[] = localStorageApi?.getAnswers(message.questionId)!!;

  quizQuestionViewRef.value?.updateQuestion(question);
  quizQuestionViewRef.value?.updateOptions(answers);

  state.value = "QUESTION"
}

const handleQuizRoundStopMessage = (message: QuizRoundMessage) => {
  state.value = "QUESTION_RESULT"

  quizQuestionViewRef.value?.showCorrect();
  quizQuestionViewRef.value?.updateQuestion({
    text: message?.textAlternative ?? question.value!.text,
    imageId: message?.imageAlternativeId ?? question.value?.imageId
  });

  SoundEngine.nextTrack();
}

const isShowQuestion = ():boolean => {
  return state.value == "QUESTION_RESULT"
    || state.value == "QUESTION";
}

const isShowRating = ():boolean => {
  return state.value == "WAITING"
    || state.value == "RATING"
    || state.value == "FINISH";
}

</script>

<template>
  <div class="flex flex-col shrink items-center p-3 w-full xl:w-5xl gap-3 mx-auto">
    <div class="flex items-center justify-between xl:justify-center w-full gap-3">
      <LiquidGlass class="w-full text-3xl font-medium grid grid-cols-1 gap-3">
        <div class="w-fit inline-block p-3">{{title}}</div>
        <div
            class="m-3 text-nowrap text-xl rounded-md bg-blue-500/10 px-2 py-1 text-blue-500 inset-ring inset-ring-blue-500/20 text-center">
          {{ state == 'WAITING' ? 'Ожидание участников' : 'Игра' }}
        </div>
      </LiquidGlass>
      <LiquidGlass class="flex flex-col items-center min-w-fit">
        <div id="join-qr" class="rounded-2xl"/>
      </LiquidGlass>
    </div>
    <div class="relative grid grid-cols-1 gap-3 mt-3 w-full xl:w-5xl">
      <TransitionGroup name="quiz" class="overflow-hidden">
        <QuizQuestionView class="w-full" v-if="isShowQuestion()" :init-question="question" :init-options="answers" ref="quizQuestionViewRef"/>
        <QuizRatingView class="w-full" v-if="isShowRating()" :items="ratingItems" ref="quizRatingViewRef"/>
      </TransitionGroup>
    </div>
  </div>
</template>

<style scoped>
.quiz-enter-active,
.quiz-leave-active {
  position: relative;
  transition: all 1s ease-in-out;
}

.quiz-enter-from {
  transform: translateX(200%);
  opacity: 0.5;
}

.quiz-enter-to {
  transform: translateX(0);
  opacity: 1;
}

.quiz-leave-from {
  transform: translateX(0);
  opacity: 1;
}

.quiz-leave-to {
  transform: translateX(-200%);
  opacity: 0.5;
}
</style>