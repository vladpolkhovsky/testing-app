<script setup lang="ts">

import {useRoute} from "vue-router";
import QRCode from "qrcode"
import {inject, onMounted, onUpdated, provide, ref, useTemplateRef} from "vue";
import LiquidGlass from "@/component/LiquidGlass.vue";
import QuizRatingView from "@/view/QuizRatingView.vue";
import QuizQuestionView from "@/view/QuizQuestionView.vue";
import {SocketService} from "@/service/SocketService.ts";
import type {QuizInitializationMessage, QuizRoundMessage, QuizShowNewQuestionMessage} from "@/model/stomp-messages.ts";
import {LocalStorageApi} from "@/service/LocalStorageApi.ts";
import type {Question} from "@/model/Question.ts";
import type {AnswerOption} from "@/model/AnswerOption.ts";
import type {RatingItem} from "@/model/RatingItem.ts";

type State = "WAITING" | "SHOW-QUESTION" | "SHOW-RATING" | "RESULTS";

const route = useRoute();
const roomId = route.params.gameId as string;
const state = ref<State>("WAITING" as State);

const question = ref<Question>();
const answers = ref<AnswerOption[]>();
const ratingItems = ref<RatingItem[]>();

const sockerService = new SocketService(roomId);

provide("SocketService", sockerService);
const localStorageApi = inject("LocalStorageApi") as LocalStorageApi;

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

onMounted(() => {
  generateQrCode();
  sockerService.registerMessageHandler<QuizInitializationMessage>("INIT_MESSAGE", handleQuizInitializationMessage)
  sockerService.registerMessageHandler<QuizShowNewQuestionMessage>("NEW_QUESTION", handleShowNewQuestionMessage)
  sockerService.registerMessageHandler<QuizRoundMessage>("START_ROUND", handleQuizRoundStartMessage)
  sockerService.registerMessageHandler<QuizRoundMessage>("STOP_ROUND", handleQuizRoundStopMessage)
  // sockerService.connect();
});

onUpdated(() => {
  generateQrCode();
})

const handleQuizInitializationMessage = (message: QuizInitializationMessage) => {
  localStorageApi?.setQuestion(message.questionId, message.question);
  localStorageApi?.setAnswers(message.questionId, message.answers);

  question.value = message.question;
  answers.value = message.answers;
  ratingItems.value = message.ratingItems;

  if (message.gameStarted) {
    state.value = "SHOW-QUESTION";
    quizQuestionViewRef.value?.updateQuestion(message.question);
    quizQuestionViewRef.value?.updateOptions(message.answers);
  } else {
    message.ratingItems.forEach(item => quizRatingViewRef.value?.addUser(item));
  }
}

const handleShowNewQuestionMessage = (message: QuizShowNewQuestionMessage) => {
  localStorageApi?.setQuestion(message.questionId, message.question);
  localStorageApi?.setAnswers(message.questionId, message.answers);

  question.value = message.question;
  answers.value = message.answers;

  quizQuestionViewRef.value?.updateQuestion(message.question);
  quizQuestionViewRef.value?.updateOptions(message.answers);

  state.value = "SHOW-RATING";
}

const handleQuizRoundStartMessage = (message: QuizRoundMessage) => {
  const question: Question = localStorageApi?.getQuestion(message.questionId)!!;
  const answers: AnswerOption[] = localStorageApi?.getAnswers(message.questionId)!!;

  quizQuestionViewRef.value?.updateQuestion(question);
  quizQuestionViewRef.value?.updateOptions(answers);

  state.value = "SHOW-QUESTION"
}

const handleQuizRoundStopMessage = (message: QuizRoundMessage) => {
  state.value = "RESULTS"
  quizQuestionViewRef.value?.showCorrect();
}

window.handleQuizRoundStartMessage = () => {
  handleQuizRoundStartMessage({
    quizId: "quizId",
    questionId: "questionId",
    type: "STOP_ROUND",
    duration: 45
  });
}

window.handleQuizRoundStopMessage = () => {
  handleQuizRoundStopMessage({
    quizId: "quizId",
    questionId: "questionId",
    type: "STOP_ROUND",
    duration: 45
  });
}

window.handleQuizInitializationMessage = () => {
  handleQuizInitializationMessage({
    question: {
      text: "Пример текста вопроса"
    },
    answers: [{
      id: "id-1",
      isCorrect: true,
      optionText: "Ответ 1",
      optionVariant: "A",
    },{
      id: "id-2",
      isCorrect: false,
      optionText: "Ответ 2",
      optionVariant: "B",
    }],
    ratingItems: [{
      rating: 0,
      userId: "userId-1",
      username: "userId-1"
    }],
    gameStarted: false,
    quizId: "quizId",
    questionId: "questionId",
    nextQuestionId: "nextQuestionId",
  } as QuizInitializationMessage)
}

window.handleShowNewQuestionMessage = () => {
  handleShowNewQuestionMessage({
    question: {
      text: "Пример текста вопроса"
    },
    answers: [{
      id: "id-1",
      isCorrect: true,
      optionText: "Ответ 1",
      optionVariant: "A",
    }],
    questionId: "questionId",
    nextQuestionId: "nextQuestionId"
  } as QuizShowNewQuestionMessage);
}

</script>

<template>
  <div class="flex flex-col shrink items-center p-3 w-full xl:w-5xl gap-3 mx-auto">
    <div class="flex items-center justify-between xl:justify-center w-full gap-3">
      <LiquidGlass class="text-3xl font-medium grid grid-cols-1 gap-3">
        <div class="w-fit inline-block p-3">Lorem ipsum dolor sit amet consectetur adipiscing elit.</div>
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
        <QuizQuestionView class="w-full" v-if="state == 'SHOW-QUESTION' || state == 'RESULTS'" :init-question="question" :init-options="answers" ref="quizQuestionViewRef"/>
        <QuizRatingView class="w-full" v-if="state == 'SHOW-RATING' || state == 'WAITING' || state == 'RESULTS'" :items="ratingItems" ref="quizRatingViewRef"/>
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