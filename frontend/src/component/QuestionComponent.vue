<script setup lang="ts">
import type {Question} from "@/model/Question.ts";
import {ref} from "vue";

const question = ref<Question>();

const updateQuestion = (newQuestion: Question) => {
  question.value = newQuestion
};

defineExpose({
  updateQuestion
})
</script>

<template>
  <h1 class="font-medium text-2xl px-3 mt-3">Вопрос:</h1>
  <div class="flex gap-8 justify-between m-3">
    <p class="text-4xl text-justify">{{ question?.text }}</p>
    <img class="rounded-xl max-h-128 max-w-128" v-if="question?.imageId" :src="'/api/quiz/image/'+question.imageId">
  </div>
</template>

<style scoped>

</style>