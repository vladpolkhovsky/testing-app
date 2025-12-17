<script setup lang="ts">

import {useRoute} from "vue-router";
import QRCode from "qrcode"
import {onMounted, onUpdated} from "vue";
import LiquidGlass from "@/component/LiquidGlass.vue";
import QuestionComponent from "@/component/QuestionComponent.vue";
import AnswerComponent from "@/component/AnswerComponent.vue";
import {getRandomAnswers} from "@/model/AnswerOption.ts";
import QuizRating from "@/view/QuizRating.vue";
import {getRandomRating} from "@/model/RatingItem.ts";

let route = useRoute();
console.log(route.params.gameId)

function generateQrCode() {
  QRCode.toCanvas("http://localhost:5173/game/123" as string, {errorCorrectionLevel: 'H'}, function (err, canvas) {
    if (err) throw err;
    canvas.classList.add("rounded-xl", "w-fit");
    document.getElementById("join-qr")?.replaceChildren();
    document.getElementById("join-qr")?.appendChild(canvas)
  })
}

onMounted(() => {
  generateQrCode();
});

onUpdated(() => {
  generateQrCode();
})

</script>

<template>
  <div class="flex flex-col shrink items-center p-3 w-full xl:w-5xl gap-3 mx-auto">
    <div class="flex items-center justify-between xl:justify-center w-full gap-3">
      <LiquidGlass class="text-3xl font-medium grid grid-cols-1 gap-3">
        <div class="w-fit inline-block p-3">Lorem ipsum dolor sit amet consectetur adipiscing elit.</div>
        <div class="m-3 text-nowrap text-xl rounded-md bg-blue-500/10 px-2 py-1 text-blue-500 inset-ring inset-ring-blue-500/20 text-center">Ожидание участников</div>
      </LiquidGlass>
      <LiquidGlass class="flex flex-col items-center min-w-fit">
        <div id="join-qr" class="rounded-2xl"/>
      </LiquidGlass>
    </div>
    <div class="grid grid-cols-1 gap-3 mt-3 w-full xl:w-5xl">
      <LiquidGlass class="rounded-xl">
        <QuestionComponent text="" image-url="https://www.google.com/earth/outreach/images/stories_wwf_elephant.jpg"/>
      </LiquidGlass>
      <LiquidGlass class="rounded-xl">
        <AnswerComponent :options="getRandomAnswers()"/>
      </LiquidGlass>
      <LiquidGlass class="rounded-xl">
        <QuizRating :items="getRandomRating()" />
      </LiquidGlass>
    </div>
  </div>
</template>

<style scoped>

</style>