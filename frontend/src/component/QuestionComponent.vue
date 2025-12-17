<script setup lang="ts">
  const props = defineProps<{
    text: string,
    imageUrl?: string
  }>();
</script>

<template>
  <h1 class="font-medium text-2xl px-3 mt-3">Вопрос:</h1>
  <div class="flex gap-8 m-3">
    <p class="text-4xl text-justify">Lorem ipsum dolor sit amet consectetur adipiscing elit. Quisque faucibus ex sapien vitae pellentesque sem placerat.</p>
    <img class="rounded-xl max-h-128 max-w-96" v-if="imageUrl" :src="imageUrl" />
  </div>
</template>

<style scoped>

</style>