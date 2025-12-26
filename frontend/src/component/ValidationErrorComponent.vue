<script setup lang="ts">
import { ref } from "vue";
import { cn } from 'vue-sonner/src/packages/hooks.ts'

const props = defineProps<{
  errorText: string;
  className?: string;
}>();

const validationError = ref<string>(props.errorText ?? '');

</script>

<template>
  <div :class="cn('flex items-center gap-2 mt-2 animate-shake-x', className)" role="alert">
    <svg class="w-4 h-4 text-rose-500 flex-shrink-0" fill="currentColor" viewBox="0 0 20 20">
      <path fill-rule="evenodd"
            d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z"
            clip-rule="evenodd"/>
    </svg>
    <span class="text-sm font-medium text-rose-700">
      {{ validationError }}
    </span>
  </div>
</template>

<style>
@keyframes shake-x {
  0%, 100% {
    transform: translateX(0);
  }
  20%, 60% {
    transform: translateX(-2px);
  }
  40%, 80% {
    transform: translateX(2px);
  }
}

.animate-shake-x {
  animation: shake-x 0.5s ease-in-out;
}
</style>