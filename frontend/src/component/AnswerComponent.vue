<script setup lang="ts">
import type {AnswerOption} from "@/model/AnswerOption.ts";
import {ref} from "vue";

const answerOptions = ref<AnswerOption[]>();

const updateAnswerOptions = (options: AnswerOption[]) => {
  answerOptions.value = options;
}

const showOnlyCorrect = () => {
  answerOptions.value = answerOptions.value?.filter(t => t.isCorrect)
}

defineExpose({
  updateAnswerOptions,
  showOnlyCorrect
});

</script>

<template>
  <div class="grid grid-cols-1 xl:grid-cols-2 gap-3 gap-y-5 text-2xl p-3">
    <div class="item flex flex-row justify-start items-center border-b-3 border-dashed mx-3 pb-2" v-for="option in answerOptions">
      <div class="mr-5 text-5xl self-end font-italic">{{option.optionVariant}}.</div>
      <div class="text-3xl self-end">{{option.optionText}}</div>
    </div>
  </div>
</template>

<style scoped>
@import "@/main.css";

.item:hover {
  @apply bg-gradient-to-t from-emerald-500/50 to-transparent
}
</style>