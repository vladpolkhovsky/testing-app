<script setup lang="ts">
import { ref } from "vue";

const props = defineProps<{
  errorText: string;
}>();

const validationError = ref<string>(props.errorText ?? '');

</script>

<template>
  <span class="block text-sm font-medium text-red-700 mt-2">
    {{ validationError }}
  </span>
</template>