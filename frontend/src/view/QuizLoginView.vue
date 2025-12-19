<script setup lang="ts">

import LiquidGlass from "@/component/LiquidGlass.vue";
import {useRoute, useRouter} from "vue-router";
import {inject, onMounted, ref} from "vue";
import type {LocalStorageApi} from "@/service/LocalStorageApi.ts";

const route = useRoute();
const router = useRouter();
const username = ref<string>("");
const localStorageApi = inject<LocalStorageApi>("LocalStorageApi")!!;

function onLogin() {
  localStorageApi.setUserInformation({
    userId: (+ new Date()).toString(),
    username: username.value!!
  });

  if (route?.query?.hardRedirectTo) {
    window.location.replace(route?.query?.hardRedirectTo as string);
  }
}

</script>

<template>
  <LiquidGlass
      class="inline-block md:flex flex-col items-center mx-auto my p-3 md:border md:rounded-md md:w-md md:m-auto shrink">
    <form @submit.prevent="onLogin()">
      <label for="username" class="w-full">
        <span class="text-sm font-medium text-gray-700">Введите ваше имя:</span>
        <input type="text" required id="username"
               class="mt-2 p-1 w-full border rounded-md hover:shadow-md/50 focus:shadow-md/50 transition duration-150 text-sm font-medium"
               v-model="username"
               placeholder="Имя">
      </label>
      <button type="submit"
              class="mt-2 p-1 w-full border rounded-md hover:bg-green-700 focus:bg-green-700 hover:text-white transition duration-150 text-sm font-medium">
        Сохранить
      </button>
    </form>
  </LiquidGlass>
</template>

<style scoped>

</style>