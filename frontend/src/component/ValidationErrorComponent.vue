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
  <span :class="cn('block text-sm font-medium text-rose-700 mt-2', className)">
    {{ validationError }}
  </span>
</template>