<script setup lang="ts">

import {type AnswerOption, getRandomAnswers} from "@/model/AnswerOption.ts";
import LiquidGlass from "@/component/LiquidGlass.vue";
import QuestionComponent from "@/component/QuestionComponent.vue";
import AnswerComponent from "@/component/AnswerComponent.vue";
import type {Question} from "@/model/Question.ts";
import {onMounted, useTemplateRef} from "vue";

const props = defineProps<{
  initQuestion?: Question,
  initOptions?: AnswerOption[],
}>();

const quizQuestionComponentRef = useTemplateRef<InstanceType<typeof QuestionComponent>>("quizQuestionComponentRef")
const quizAnswerComponentRef = useTemplateRef<InstanceType<typeof AnswerComponent>>("quizAnswerComponentRef")

const updateQuestion = (question: Question) => {
  quizQuestionComponentRef.value?.updateQuestion(question);
}

const updateOptions = (options: AnswerOption[]) => {
  quizAnswerComponentRef.value?.updateAnswerOptions(options);
}

const showCorrect = () => {
  quizAnswerComponentRef.value?.showOnlyCorrect();
}

onMounted(() => {
  if (props.initQuestion) {
    updateQuestion(props.initQuestion)
  }
  if (props.initOptions) {
    updateOptions(props.initOptions)
  }
})

defineExpose({
  updateQuestion,
  updateOptions,
  showCorrect
})

</script>

<template>
  <div class="flex flex-col gap-5">
    <LiquidGlass class="rounded-xl">
      <QuestionComponent ref="quizQuestionComponentRef"/>
    </LiquidGlass>
    <LiquidGlass class="rounded-xl" key="rating">
      <AnswerComponent ref="quizAnswerComponentRef"/>
    </LiquidGlass>
  </div>
</template>

<style scoped>

</style>