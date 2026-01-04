<template>
  <div class="w-full h-100vh p-3">
    <div class="grid grid-cols-3 gap-3 w-full h-full">
      <div class="col-span-2 border">
        <div class="flex flex-col items-start justify-center gap-5 p-3 h-full">
          <div class="flex justify-between items-center gap-3 ps-3 pe-3 w-full">
            <div class="inline-flex gap-3 items-center">
              <div class="tektur-badge text-xl me-3">Название теста</div>
              <div class="border p-2 tektur-badge text-md rounded-2xl border-green-700 bg-radial from-green-400 to-green-200 hover:to-green-300 transition-colors duration-3  00">Раунд 1 / 5</div>
            </div>
            <LucideQrCode :size="42" />
            <div>
              <ul class="flex flex-wrap text-sm font-medium text-center text-body">
                <li class="me-2">
                  <button class="inline-block p-3 rounded-t-base active tektur-badge text-xl border-b-4 bg-amber-200 rounded-tl-xl rounded-tr-xl">Игра</button>
                </li>
                <li class="me-2">
                  <button class="inline-block p-3 rounded-t-base active tektur-badge text-xl">Участники</button>
                </li>
              </ul>
            </div>
          </div>
          <div class="flex h-full w-full p-3 border">
            1
          </div>
        </div>
      </div>
      <div class="border flex flex-col items-start justify-center gap-5 p-3">
        <div>1</div>
        <div>1</div>
        <div>1</div>
        <div>1</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { LucideQrCode } from 'lucide-vue-next';


</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Pangolin&family=Tektur:wght@400..900&display=swap');

.tektur-badge {
  font-family: "Tektur", sans-serif;
  font-optical-sizing: auto;
  font-weight: bold;
  font-variation-settings: "wdth" 100;
}

.pangolin-regular {
  font-family: "Pangolin", cursive;
  font-weight: 400;
  font-style: normal;
}

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