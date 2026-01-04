<template>
  <div class="w-full h-100vh p-3">
    <div class="grid grid-cols-3 gap-3 w-full h-full">
      <div class="col-span-2 border">
        <div class="flex flex-col items-start justify-baseline gap-5 p-3 h-full">
          <div class="flex justify-between items-center gap-3 ps-3 pe-3 w-full">
            <div class="inline-flex gap-3 items-center">
              <div class="group transition-all duration-150 hover:scale-105 hover:text-red-700">
                <LucideHome :size="32" />
              </div>
              <div class="tektur-badge text-xl me-3">Название теста</div>
              <div
                class="border p-2 tektur-badge text-md rounded-2xl border-green-700 bg-radial from-green-400 to-green-300/50 hover:bg-green-500 transition-all duration-150 hover:scale-105">
                Раунд 1 / 5</div>
            </div>
            <div>
              <ul class="flex flex-wrap text-sm font-medium text-center text-body">
                <li class="me-2">
                  <button
                    class="inline-block p-3 rounded-t-base active tektur-badge text-xl border-b-4 bg-amber-200 rounded-tl-xl rounded-tr-xl transition-all duration-150 hover:scale-105">Игра</button>
                </li>
                <li class="me-2">
                  <button class="inline-block p-3 rounded-t-base active tektur-badge text-xl transition-all duration-150 hover:scale-105">Участники</button>
                </li>
              </ul>
            </div>
          </div>
          <div class="relarive flex justify-around h-full w-full ps-3 pe-3">
            <QuizQuestion question="13131231312 123 123 123 123 123123 123 123 123"
              image-url="https://cdn.fstoppers.com/styles/full/s3/media/2020/12/21/nando-vertical-horizontal-11.jpg" />
          </div>
        </div>
      </div>
      <QuizAnswer />
    </div>
  </div>
</template>

<script setup lang="ts">
import QuizAnswer from '@/component/QuizAnswer.vue';
import QuizQuestion from '@/component/QuizQuestion.vue';
import { LucideHome, LucideHop, LucideQrCode } from 'lucide-vue-next';


</script>

<style scoped>
.list-move,
.list-enter-active,
.list-leave-active {
  transition: all 0.5s ease;
}

.list-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}

.list-leave-to {
  opacity: 0;
  transform: translateX(30px);
}

.list-leave-active {
  position: absolute;
}
</style>