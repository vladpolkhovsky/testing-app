<script setup lang="ts">

import LiquidGlass from "@/component/LiquidGlass.vue";
</script>

<template>
  <LiquidGlass class="inline-block md:flex flex-col items-center mx-auto my p-3 md:border md:rounded-md md:w-md md:m-auto shrink">
    <label for="username" class="w-full">
      <span class="text-sm font-medium text-gray-700">Введите ваше имя:</span>
      <input type="text" id="username" class="mt-2 p-1 w-full border rounded-md hover:shadow-md/50 focus:shadow-md/50 transition duration-150 text-sm font-medium" placeholder="Имя">
    </label>
    <button class="mt-2 p-1 w-full border rounded-md hover:bg-green-700 focus:bg-green-700 hover:text-white transition duration-150 text-sm font-medium">Сохранить</button>
  </LiquidGlass>
</template>

<style scoped>

</style>