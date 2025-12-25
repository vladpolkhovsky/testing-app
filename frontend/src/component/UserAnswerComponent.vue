<script setup lang="ts">
import type {AnswerOption} from "@/model/AnswerOption.ts";
import {inject, ref} from "vue";
import type {SocketService} from "@/service/SocketService.ts";
import {CircleCheckBig, CircleX, CheckCheck} from "lucide-vue-next"

const props = defineProps<{
  options?: AnswerOption[]
}>();

const answerOptions = ref<AnswerOption[]>(props.options ?? []);
const socketService = inject<SocketService>("SocketService");

const updateOptions = (options: AnswerOption[]) => {
  isSent.value = false;
  selectedId.value = undefined;
  answerOptions.value = options;
}

const showOnlyCorrect = () => {
  isShowOnlyCorrect.value = true;
}

const reset = () => {
  isShowOnlyCorrect.value = false;
  selectedId.value = undefined;
}

defineExpose({
  updateOptions,
  showOnlyCorrect,
  reset
});


const selectedId = ref<string>();
const isSent = ref<boolean>(false);
const isShowOnlyCorrect = ref<boolean>(false);

const sent = (id: string) => {
  isSent.value = true;
  socketService?.sendAnswer(id);
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
      <div class="w-full flex gap-3 items-center" @click="select(option.id)" v-for="option in answerOptions">
        <div class="w-full flex flex-row justify-start items-center border-b-3 border-dashed mx-3 pb-2" >
          <CircleX :size="32" class="text-red-700 mr-5" v-if="selectedId == option.id && isSent && isShowOnlyCorrect && !option.correct" />
          <CircleCheckBig :size="32" class="text-green-500 mr-5" v-if="selectedId == option.id && isSent && isShowOnlyCorrect && option.correct" />
          <CircleCheckBig :size="32" class="text-amber-500 mr-5" v-if="selectedId == option.id && isSent && !isShowOnlyCorrect" />
          <CircleCheckBig :size="32" class="text-blue-500 mr-5" v-if="selectedId == option.id && !isSent" />
          <CheckCheck :size="32" class="text-green-500 mr-5" v-if="selectedId != option.id && isShowOnlyCorrect && option.correct" />
          <div class="p-3 pr-0 mr-5 text-2xl self-end font-bold">{{ option.optionVariant }}.</div>
          <div class="pl-0 p-3 text-xl md:text-2xl lg:text-3xl self-end text-start font-medium">{{ option.optionText }}</div>
        </div>
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