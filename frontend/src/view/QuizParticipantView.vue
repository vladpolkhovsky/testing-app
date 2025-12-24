<script setup lang="ts">
import LiquidGlass from "@/component/LiquidGlass.vue";
import UserAnswerComponent from "@/component/UserAnswerComponent.vue";
import {type AnswerOption, getRandomAnswers} from "@/model/AnswerOption.ts";
import {inject, onMounted, provide, ref, useTemplateRef} from "vue";
import type {LocalStorageApi} from "@/service/LocalStorageApi.ts";
import type {User} from "@/model/User.ts";
import {useRoute, useRouter} from "vue-router";
import {SocketService} from "@/service/SocketService.ts";
import type {QuizInitializationMessage, QuizRoundMessage, QuizShowNewQuestionMessage} from "@/model/stomp-messages.ts";
import type {Question} from "@/model/Question.ts";
import QuizRatingView from "@/view/QuizRatingView.vue";

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
  <div class="flex flex-col shrink items-center p-3 w-full xl:w-5xl gap-3 mx-auto">
    <LiquidGlass class="w-full text-center text-2xl">{{user?.username}}</LiquidGlass>
    <LiquidGlass class="w-full text-center">
      <UserAnswerComponent :options="answers" v-if="isRoundStarted" ref="userAnswerComponentRef"/>
      <div v-else class="text-3xl">
        Ожидание новых вопросов
      </div>
    </LiquidGlass>
  </div>
</template>

<style scoped>

</style>