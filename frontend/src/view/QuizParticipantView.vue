<script setup lang="ts">
import LiquidGlass from "@/component/LiquidGlass.vue";
import UserAnswerComponent from "@/component/UserAnswerComponent.vue";
import {getRandomAnswers} from "@/model/AnswerOption.ts";
import {inject, onMounted, ref} from "vue";
import type {LocalStorageApi} from "@/service/LocalStorageApi.ts";
import type {User} from "@/model/User.ts";
import {useRouter} from "vue-router";

const localStorageApi = inject<LocalStorageApi>("LocalStorageApi")!!;
const user = ref<User>();
let router = useRouter();

onMounted(() => {
  let userInformation = localStorageApi.getUserInformation();
  if (!userInformation) {
    router.push({
      name: "QuizLoginView",
      query: {
        hardRedirectTo: window.location
      }
    })
  }
  user.value = userInformation;
})
</script>

<template>
  <div class="flex flex-col shrink items-center p-3 w-full xl:w-5xl gap-3 mx-auto">
    <LiquidGlass class="w-full text-center text-2xl">{{user?.username}}</LiquidGlass>
    <LiquidGlass class="w-full text-center">
      <UserAnswerComponent :options="getRandomAnswers()"/>
    </LiquidGlass>
  </div>
</template>

<style scoped>

</style>