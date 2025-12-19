<script setup lang="ts">
import type {AnswerOption} from "@/model/AnswerOption.ts";
import {ref} from "vue";

const props = defineProps<{
  options?: AnswerOption[]
}>();

const answerOptions = ref<AnswerOption[]>(props.options);

const updateAnswerOptions = (options: AnswerOption[]) => {
  isSent.value = false;
  selectedId.value = undefined;
  answerOptions.value = options;
}

const showOnlyCorrect = () => {
  answerOptions.value = answerOptions.value?.filter(t => t.isCorrect)
}

defineExpose({
  updateAnswerOptions,
  showOnlyCorrect
});

const selectedId = ref<string>();
const isSent = ref<boolean>(false);

const sent = (id: string) => {
  isSent.value = true;
}

const select = (id: string) => {
  if (isSent.value) {
    return;
  }
  if (selectedId.value == id) {
    selectedId.value = undefined;
    return;
  }
  selectedId.value = id;
}

</script>

<template>
  <div class="flex flex-col gap-5">
    <div class="grid grid-cols-1 gap-2 gap-y-5 text-md xl:text-2xl p-3">
      <div :class="['flex flex-row justify-start items-center border-b-3 border-dashed mx-3 pb-2', {
      'selected': option.id == selectedId,
      'item': !isSent
    }]" @click="select(option.id)" v-for="option in answerOptions">
        <div class="p-3 pr-0 mr-5 text-2xl self-end">{{ option.optionVariant }}.</div>
        <div class="pl-0 p-3 text-xl xl:text-xl self-end text-start">{{ option.optionText }}</div>
      </div>
    </div>
    <button v-if="selectedId" :class="['px-8 text-md xl:text-2xl py-3 text-white rounded-full  backdrop-blur', {
      'bg-emerald-900/80': !isSent,
      'bg-gray-900/80': isSent
    }]" @click="sent(selectedId)">
      {{ isSent ? 'Отправлено!' : 'Выбрать' }}
    </button>
  </div>

</template>

<style scoped>
@import "@/main.css";

.item:hover, .selected {
  @apply bg-gradient-to-t from-emerald-500/50 to-transparent
}

@keyframes rotate {
  100% {
    transform: rotate(1turn);
  }
}

</style>