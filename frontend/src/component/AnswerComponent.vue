<script setup lang="ts">
import type {AnswerOption} from "@/model/AnswerOption.ts";
import {ref} from "vue";
import {CheckCheck, CircleCheckBig, CircleX} from "lucide-vue-next";

const answerOptions = ref<AnswerOption[]>();

const isShowOnlyCorrect = ref(false);

const showOnlyCorrect = () => {
  isShowOnlyCorrect.value = true;
}

const reset = () => {
  isShowOnlyCorrect.value = false;
}

defineProps<{
  options: AnswerOption[];
}>();

defineExpose({
  showOnlyCorrect,
  reset
})

</script>

<template>
  <div class="grid grid-cols-1 xl:grid-cols-2 gap-3 gap-y-5 text-2xl p-3">
    <div class="w-full flex gap-3 items-center" v-for="option in options">
      <div class="w-full flex flex-row justify-start items-center border-b-3 border-dashed mx-3 pb-2" >
        <CircleCheckBig :size="32" class="text-green-500 mr-5" v-if="isShowOnlyCorrect && option.correct" />
        <div class="p-3 pr-0 mr-5 text-3xl self-end font-bold">{{ option.optionVariant }}.</div>
        <div class="pl-0 p-3 text-xl xl:text-2xl self-end text-start font-medium">{{ option.optionText }}</div>
      </div>
    </div>
  </div>
</template>

<style scoped>
@import "@/main.css";

.item:hover {
  @apply bg-gradient-to-t from-emerald-500/50 to-transparent
}
</style>