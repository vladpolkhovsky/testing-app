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
  <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-2 gap-6">
    <div
        v-for="(option, index) in options"
        :key="option.id"
        :class="[
        'group relative overflow-hidden rounded-2xl transition-all duration-300 p-6',
        'hover:shadow-lg hover:scale-[1.01]',
        isShowOnlyCorrect && option.correct
          ? 'bg-gradient-to-r from-green-50 to-emerald-50 border-2 border-green-200 ring-2 ring-green-500/20'
          : 'bg-gradient-to-r from-gray-50 to-gray-100 border border-gray-200'
      ]"
    >
      <!-- Correct answer glow effect -->
      <div
          v-if="isShowOnlyCorrect && option.correct"
          class="absolute inset-0 bg-gradient-to-r from-green-400/10 to-emerald-400/5 opacity-0 group-hover:opacity-100 transition-opacity duration-500"
      />

      <!-- Option indicator -->
      <div class="relative flex items-center gap-4">
        <!-- Status indicator -->
        <div class="relative flex-shrink-0">
          <div class="relative w-12 h-12 flex items-center justify-center">
            <CircleCheckBig
                v-if="isShowOnlyCorrect && option.correct"
                :size="28"
                class="text-green-500 animate-pulse"
            />
            <div
                v-else
                :class="[
                'w-10 h-10 rounded-full border-2 flex items-center justify-center',
                'transition-all duration-300 font-bold text-lg',
                isShowOnlyCorrect && option.correct
                  ? 'border-green-500 bg-green-100 text-green-700'
                  : 'border-gray-300 text-gray-700'
              ]"
            >
              {{ option.optionVariant }}
            </div>
          </div>
        </div>

        <!-- Option content -->
        <div class="flex-1">
          <p
              :class="[
              'text-xl font-medium leading-relaxed',
              isShowOnlyCorrect && option.correct
                ? 'text-emerald-700'
                : 'text-gray-700'
            ]"
          >
            {{ option.optionText }}
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
@keyframes gentle-pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.8; }
}

.animate-pulse {
  animation: gentle-pulse 2s ease-in-out infinite;
}
</style>