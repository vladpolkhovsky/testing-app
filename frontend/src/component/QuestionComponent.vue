<script setup lang="ts">
import type {Question} from "@/model/Question.ts";

const props = defineProps<Question>();
</script>

<template>
  <div class="border-3 rounded-xl border-dashed">
    <h1 class="font-medium text-xl md:text-3xl px-3 mt-3 text-center">Вопрос</h1>
    <div class="flex gap-8 justify-between m-3 p-3">
      <p class="text-xl md:text-2xl xl:text-3xl font-medium">{{ text }}</p>
      <img class="rounded-xl max-h-64 max-w-52 md:max-h-128 md:max-w-64 lg:max-w-128 xl:max-w-192" v-if="imageId"
           :src="`/api/quiz/image/${imageId}`" alt="quiz image"/>
    </div>
  </div>
</template>

<style scoped>

</style>