<script setup lang="ts">
import type { AnswerOption } from "@/model/AnswerOption.ts";
import { inject, ref } from "vue";
import type { SocketService } from "@/service/SocketService.ts";
import { CircleCheckBig, CircleX, CheckCheck } from "lucide-vue-next"
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
  <LiquidGlass class="flex flex-col gap-6 p-6">
    <div class="grid grid-cols-1 gap-3 p-2">
      <div
          v-for="(option, index) in answerOptions"
          :key="option.id"
          @click="select(option.id)"
          :class="[
          'group relative overflow-hidden rounded-2xl transition-all duration-300 cursor-pointer',
          'hover:scale-[1.02] active:scale-[0.99]',
          {
            'ring-2 ring-offset-2 ring-blue-500 shadow-lg': selectedId === option.id && !isSent,
            'ring-2 ring-offset-2 ring-amber-500 shadow-lg': selectedId === option.id && isSent && !isShowOnlyCorrect,
            'ring-2 ring-offset-2 ring-green-500 shadow-lg': selectedId === option.id && isSent && isShowOnlyCorrect && option.correct,
            'ring-2 ring-offset-2 ring-rose-500 shadow-lg': selectedId === option.id && isSent && isShowOnlyCorrect && !option.correct,
            'hover:bg-white/5': selectedId !== option.id
          }
        ]"
      >
        <!-- Background effect -->
        <div
            :class="[
            'absolute inset-0 opacity-0 transition-opacity duration-300',
            {
              'bg-gradient-to-r from-blue-500/10 to-blue-400/5 opacity-100': selectedId === option.id && !isSent,
              'bg-gradient-to-r from-amber-500/10 to-amber-400/5 opacity-100': selectedId === option.id && isSent && !isShowOnlyCorrect,
              'bg-gradient-to-r from-green-500/10 to-green-400/5 opacity-100': selectedId === option.id && isSent && isShowOnlyCorrect && option.correct,
              'bg-gradient-to-r from-rose-500/10 to-rose-400/5 opacity-100': selectedId === option.id && isSent && isShowOnlyCorrect && !option.correct,
              'group-hover:bg-gradient-to-r group-hover:from-white/5 group-hover:to-transparent group-hover:opacity-100': selectedId !== option.id
            }
          ]"
        />

        <div class="relative flex items-center gap-4 p-5">
          <!-- Status icons -->
          <div class="relative z-10 flex-shrink-0">
            <div class="relative w-10 h-10 flex items-center justify-center">
              <!-- Selected but not sent -->
              <CircleCheckBig
                  v-if="selectedId === option.id && !isSent"
                  :size="28"
                  class="text-blue-500 animate-pulse"
              />

              <!-- Sent but not showing correct -->
              <CircleCheckBig
                  v-if="selectedId === option.id && isSent && !isShowOnlyCorrect"
                  :size="28"
                  class="text-amber-500"
              />

              <!-- Correct answer -->
              <CircleCheckBig
                  v-if="selectedId === option.id && isSent && isShowOnlyCorrect && option.correct"
                  :size="28"
                  class="text-green-500 animate-bounce"
              />

              <!-- Wrong answer -->
              <CircleX
                  v-if="selectedId === option.id && isSent && isShowOnlyCorrect && !option.correct"
                  :size="28"
                  class="text-rose-500 animate-shake"
              />

              <!-- Correct but not selected -->
              <CheckCheck
                  v-if="selectedId !== option.id && isShowOnlyCorrect && option.correct"
                  :size="28"
                  class="text-green-400"
              />

              <!-- Not selected, not sent -->
              <div
                  v-if="!selectedId && !isSent && !isShowOnlyCorrect"
                  :class="[
                  'w-8 h-8 rounded-full border-2 transition-all duration-300',
                  'border-gray-400 group-hover:border-blue-400',
                  'group-hover:scale-110'
                ]"
              />
            </div>
          </div>

          <!-- Option letter/number -->
          <div
              :class="[
              'flex-shrink-0 w-12 h-12 rounded-xl flex items-center justify-center text-2xl font-bold transition-all duration-300',
              'bg-gradient-to-br from-gray-100 to-gray-50 text-gray-800',
              'group-hover:from-blue-100 group-hover:to-blue-50',
              {
                'from-blue-100 to-blue-50 text-blue-700': selectedId === option.id && !isSent,
                'from-amber-100 to-amber-50 text-amber-700': selectedId === option.id && isSent && !isShowOnlyCorrect,
                'from-green-100 to-green-50 text-green-700': selectedId === option.id && isSent && isShowOnlyCorrect && option.correct,
                'from-rose-100 to-rose-50 text-rose-700': selectedId === option.id && isSent && isShowOnlyCorrect && !option.correct
              }
            ]"
          >
            {{ option.optionVariant }}
          </div>

          <!-- Option text -->
          <div class="flex-1">
            <p
                :class="[
                'text-lg md:text-xl font-medium transition-colors duration-300',
                'text-gray-800',
                {
                  'text-blue-700': selectedId === option.id && !isSent,
                  'text-amber-700': selectedId === option.id && isSent && !isShowOnlyCorrect,
                  'text-green-700': selectedId === option.id && isSent && isShowOnlyCorrect && option.correct,
                  'text-rose-700': selectedId === option.id && isSent && isShowOnlyCorrect && !option.correct
                }
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
        v-if="selectedId"
        @click="sent(selectedId)"
        :disabled="isSent"
        :class="[
        'relative overflow-hidden px-8 py-4 text-lg font-semibold text-white rounded-2xl',
        'transition-all duration-300 transform hover:scale-[1.02] active:scale-[0.98]',
        'disabled:opacity-50 disabled:cursor-not-allowed disabled:scale-100',
        'shadow-lg hover:shadow-xl',
        {
          'bg-gradient-to-r from-blue-500 to-blue-600 hover:from-blue-600 hover:to-blue-700': !isSent,
          'bg-gradient-to-r from-emerald-500 to-emerald-600': isSent && !isShowOnlyCorrect,
          'bg-gradient-to-r from-gray-500 to-gray-600': isSent && isShowOnlyCorrect
        }
      ]"
    >
      <!-- Button shine effect -->
      <div
          :class="[
          'absolute inset-0 -translate-x-full bg-gradient-to-r from-transparent via-white/30 to-transparent',
          'transition-transform duration-1000',
          {
            'group-hover:translate-x-full': !isSent
          }
        ]"
      />

      <span class="relative z-10 flex items-center justify-center gap-2">
        <svg
            v-if="isSent"
            class="w-5 h-5 animate-spin"
            fill="none"
            viewBox="0 0 24 24"
        >
          <circle
              class="opacity-25"
              cx="12"
              cy="12"
              r="10"
              stroke="currentColor"
              stroke-width="4"
          />
          <path
              class="opacity-75"
              fill="currentColor"
              d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
          />
        </svg>
        {{ isSent ? 'Ответ отправлен' : 'Подтвердить выбор' }}
      </span>
    </button>
  </LiquidGlass>
</template>

<style scoped>
@keyframes shake {
  0%, 100% {
    transform: translateX(0);
  }
  20%, 60% {
    transform: translateX(-4px);
  }
  40%, 80% {
    transform: translateX(4px);
  }
}

.animate-shake {
  animation: shake 0.5s ease-in-out;
}
</style>