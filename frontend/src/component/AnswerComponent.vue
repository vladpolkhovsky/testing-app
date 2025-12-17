<script setup lang="ts">
import type {AnswerOption} from "@/model/AnswerOption.ts";

const props = defineProps<{
  options: AnswerOption[]
}>();
</script>

<template>
  <div class="grid grid-cols-1 xl:grid-cols-2 gap-3 text-2xl">
    <div class="border rounded-xl px-3 py-2 flex gap-3 items-center bg-gradient-to-r from-sky-300/70 to-white/50 hover:bg-green-300 transition-all duration-200 ease-in-out" v-for="option in options">
      <span class="text-justify text-2xl rounded-md bg-fuchsia-900/10 px-2 py-1 text-fuchsia-900 inset-ring inset-ring-blue-500/20">{{option.optionVariant}}</span>
      <span>{{ option.optionText }}</span>
    </div>
  </div>
</template>

<style scoped>

</style>