<script setup lang="ts">

import LiquidGlass from "@/component/LiquidGlass.vue";
import {CirclePlay, SquarePen, FilePlusCorner, Trash2} from "lucide-vue-next";
import {onMounted, ref} from "vue";

interface QuizRegistry {
  id: number;
  title: string;
}

const registry = ref<QuizRegistry[]>([]);

onMounted(() => {
  fetch("/api/quiz/editor/list")
      .then(res => res.json())
      .then(data => data as QuizRegistry[])
      .then(data => registry.value = data);
})
</script>

<template>
  <div class="flex flex-col shrink items-center p-3 w-full xl:w-5xl gap-3 mx-auto">
    <LiquidGlass class="w-full m-3 xm:w-3xl p-5">
      <LiquidGlass>
        <a href="/api/quiz/editor/create" target="_blank"
           class="block flex gap-3 items-center justify-center border p-3 rounded-xl text-2xl mb-3 text-center bg-green-100 hover:bg-green-600/30 transition-all duration-300">
          <FilePlusCorner :size="32"/>
          <div>Создать новый квиз!</div>
        </a>
        <div class="mt-3 p-5 flex flex-col justify-start gap-3">
          <div class="text-2xl mt-3 mb-3">Выбор теста</div>
          <div class="flex items-center gap-5" v-for="elem in registry">
            <a :href="`/api/quiz/editor/${elem.id}/start`" target="_blank">
              <CirclePlay :size="64" class="p-3 shadow rounded-xl hover:bg-emerald-700/30 translate duration-300"/>
            </a>
            <a :href="`/editor/${elem.id}`" target="_blank">
              <SquarePen :size="64" class="p-3 shadow rounded-xl hover:bg-amber-700/30 translate duration-300"/>
            </a>
            <a :href="`/api/quiz/editor/${elem.id}/delete`" target="_blank">
              <Trash2 :size="64" class="p-3 shadow rounded-xl hover:bg-amber-700/30 translate duration-300"/>
            </a>
            <div class="text-2xl flex flex-col justify-start p-3">
              <div>{{ elem.title }}</div>
              <div class="text-sm text-sh">{{ elem.id }}</div>
            </div>
          </div>
        </div>
      </LiquidGlass>
    </LiquidGlass>
  </div>
</template>

<style scoped>

</style>