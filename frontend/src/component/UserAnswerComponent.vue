<script setup lang="ts">
import type {AnswerOption} from "@/model/AnswerOption.ts";
import {inject, ref} from "vue";
import type {SocketService} from "@/service/SocketService.ts";
import {CircleCheckBig, CircleX, CheckCheck, Send} from "lucide-vue-next"
import LiquidGlass from "@/component/LiquidGlass.vue";

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
  <div class="flex flex-col gap-4">
    <!-- Answer options -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-2 gap-3 sm:gap-4 md:gap-6">
      <div
          v-for="(option, index) in answerOptions"
          :key="option.id"
          @click="select(option.id)"
          :class="[
          'relative overflow-hidden rounded-xl md:rounded-2xl transition-all duration-300 cursor-pointer active:scale-[0.98] border',
          'flex items-center justify-center',
          selectedId === option.id && !isSent
            ? 'border-blue-500 bg-blue-50/50 ring-2 md:ring-4 ring-blue-500/20 shadow-lg'
            : 'border-gray-400 hover:border-blue-300 hover:bg-blue-50/30',
          selectedId === option.id && isSent && !isShowOnlyCorrect
            ? 'border-amber-500 bg-amber-50/50 ring-2 md:ring-4 ring-amber-500/20'
            : '',
          selectedId === option.id && isSent && isShowOnlyCorrect && option.correct
            ? 'border-green-500 bg-green-50/50 ring-2 md:ring-4 ring-green-500/20'
            : '',
          selectedId === option.id && isSent && isShowOnlyCorrect && !option.correct
            ? 'border-rose-500 bg-rose-50/50 ring-2 md:ring-4 ring-rose-500/20'
            : ''
        ]"
      >
        <!-- Background gradient -->
        <div
            :class="[
            'absolute inset-0 opacity-100 transition-opacity duration-300 bg-gradient-to-r from-gray-50 to-gray-100',
            selectedId === option.id && isSent && !isShowOnlyCorrect ? 'bg-gradient-to-r from-amber-500/5 to-amber-400/10 opacity-100' : '',
            selectedId === option.id && isSent && isShowOnlyCorrect && option.correct ? 'bg-gradient-to-r from-green-500/5 to-green-400/10 opacity-100' : '',
            selectedId === option.id && isSent && isShowOnlyCorrect && !option.correct ? 'bg-gradient-to-r from-rose-500/5 to-rose-400/10 opacity-100' : '',
            selectedId !== option.id ? 'group-hover:bg-gradient-to-r group-hover:from-blue-500/5 group-hover:to-transparent group-hover:opacity-100' : ''
          ]"
        />

        <div class="relative flex items-center justify-center w-full gap-3 py-2 px-4">
          <!-- Status indicator -->
          <div class="relative flex-shrink-0">
            <div class="relative w-7 h-7 sm:w-8 sm:h-8 md:w-9 md:h-9 flex items-center justify-center">
              <!-- Not selected -->
              <div
                  v-if="selectedId !== option.id && (!isShowOnlyCorrect || !option.correct)"
                  :class="[
                  'w-6 h-6 sm:w-6 sm:h-6 md:w-7 md:h-7 rounded-full flex items-center justify-center font-bold text-base sm:text-lg md:text-xl',
                  'transition-all duration-300',
                  selectedId === option.id
                    ? 'border-2 border-blue-500 bg-blue-100 text-blue-700'
                    : 'bg-gradient-to-r from-indigo-500 to-purple-500 text-white shadow-sm'
                ]"
              >
                {{ option.optionVariant }}
              </div>

              <!-- Selected but not sent -->
              <div
                  v-if="selectedId === option.id && !isSent"
                  class="w-8 h-8 sm:w-9 sm:h-9 md:w-10 md:h-10 rounded-full bg-gradient-to-r from-blue-500 to-blue-600 flex items-center justify-center shadow-md"
              >
                <CircleCheckBig :size="20" class="sm:w-6 sm:h-6 md:w-7 md:h-7 text-white"/>
              </div>

              <!-- Sent (waiting) -->
              <CircleCheckBig
                  v-if="selectedId === option.id && isSent && !isShowOnlyCorrect"
                  :size="28"
                  class="sm:w-8 sm:h-8 md:w-9 md:h-9 text-amber-500 animate-pulse"
              />

              <!-- Correct answer -->
              <CircleCheckBig
                  v-if="selectedId === option.id && isSent && isShowOnlyCorrect && option.correct"
                  :size="28"
                  class="sm:w-8 sm:h-8 md:w-9 md:h-9 text-green-500 animate-bounce"
              />

              <!-- Wrong answer -->
              <CircleX
                  v-if="selectedId === option.id && isSent && isShowOnlyCorrect && !option.correct"
                  :size="28"
                  class="sm:w-8 sm:h-8 md:w-9 md:h-9 text-rose-500 animate-shake"
              />

              <!-- Correct but not selected -->
              <CheckCheck
                  v-if="selectedId !== option.id && isShowOnlyCorrect && option.correct"
                  :size="24"
                  class="sm:w-7 sm:h-7 md:w-8 md:h-8 text-green-400"
              />
            </div>
          </div>

          <!-- Option text -->
          <div class="flex-1 text-center md:text-left">
            <p
                :class="[
                'font-medium break-words text-center md:text-left',
                selectedId === option.id && !isSent ? 'text-blue-700' : '',
                selectedId === option.id && isSent && !isShowOnlyCorrect ? 'text-amber-700' : '',
                selectedId === option.id && isSent && isShowOnlyCorrect && option.correct ? 'text-green-700' : '',
                selectedId === option.id && isSent && isShowOnlyCorrect && !option.correct ? 'text-rose-700' : '',
                selectedId !== option.id ? 'text-gray-900' : ''
              ]"
            >
              {{ option.optionText }}
            </p>
          </div>
        </div>
      </div>
    </div>

    <!-- Submit button -->
    <button
        v-if="selectedId && !isShowOnlyCorrect"
        @click="sent(selectedId)"
        :disabled="isSent"
        :class="[
        'relative overflow-hidden px-4 sm:px-6 py-3 sm:py-3.5 rounded-xl sm:rounded-2xl font-bold text-sm sm:text-base md:text-lg',
        'transition-all duration-300 transform hover:scale-[1.02] active:scale-[0.98]',
        'disabled:opacity-70 disabled:cursor-not-allowed disabled:transform-none',
        'shadow-lg hover:shadow-xl mx-auto',
        'min-h-[44px] w-full sm:w-auto sm:min-w-[200px]',
        !isSent
          ? 'bg-gradient-to-r from-emerald-500 to-emerald-600 text-white hover:from-emerald-600 hover:to-emerald-700'
          : 'bg-gradient-to-r from-amber-500 to-amber-600 text-white'
      ]"
    >
      <!-- Shine effect -->
      <div
          v-if="!isSent"
          class="absolute inset-0 -translate-x-full bg-gradient-to-r from-transparent via-white/30 to-transparent group-hover:translate-x-full transition-transform duration-1000"
      />

      <span class="relative z-10 flex items-center justify-center gap-2 sm:gap-3">
        <Send v-if="!isSent" :size="18" class="sm:w-5 sm:h-5 md:w-6 md:h-6"/>
        <svg
            v-if="isSent"
            class="w-4 h-4 sm:w-5 sm:h-5 animate-spin"
            fill="none"
            viewBox="0 0 24 24"
        >
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"/>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"/>
        </svg>
        <span class="truncate">
          {{ isSent ? 'Ответ отправлен' : 'Отправить ответ' }}
        </span>
      </span>
    </button>

    <!-- Waiting message -->
    <div
        v-if="isShowOnlyCorrect"
        class="text-center px-4 py-3 rounded-xl sm:rounded-2xl bg-gradient-to-r from-gray-50 to-gray-100 border border-gray-400"
    >
      <p class="text-sm sm:text-base text-gray-600 font-medium">Ожидайте следующий вопрос</p>
    </div>
  </div>
</template>

<style scoped>
@keyframes shake {
  0%, 100% { transform: translateX(0); }
  20%, 60% { transform: translateX(-3px); }
  40%, 80% { transform: translateX(3px); }
}

.animate-shake {
  animation: shake 0.5s ease-in-out;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-5px); }
}

.animate-bounce {
  animation: bounce 0.5s ease-in-out;
}

@media (max-width: 768px) {
  .grid-cols-1 > div {
    text-align: center;
  }

  .grid-cols-1 > div > div {
    justify-content: center;
  }

  .grid-cols-1 > div p {
    text-align: center;
  }
}

@media (max-width: 480px) {
  .grid-cols-1 > div {
    padding: 0.75rem;
  }

  .grid-cols-1 > div > div {
    gap: 0.75rem;
  }

  p {
    font-size: 0.875rem;
    line-height: 1.25rem;
  }
}

.flex.items-center {
  align-items: center;
}

.grid > div {
  min-height: 64px;
}

@media (min-width: 768px) {
  .grid > div {
    min-height: 72px;
  }
}
</style>