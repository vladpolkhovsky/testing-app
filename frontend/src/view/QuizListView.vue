<script setup lang="ts">
import LiquidGlass from "@/component/LiquidGlass.vue";
import {LogOut, CirclePlay, SquarePen, FilePlusCorner, Trash2, Sparkles, LogIn} from "lucide-vue-next";
import {onMounted, ref} from "vue";
import type {User} from "@/model/User.ts";

interface QuizRegistry {
  id: number;
  title: string;
  questionCount: number;
  createdAt: string;
}

const registry = ref<QuizRegistry[]>([]);
const user = ref<User>();

onMounted(() => {
  fetch("/api/quiz/editor/list")
      .then(res => res.json())
      .then(data => data.map((item: any) => ({
        ...item,
        createdAt: new Date(item.createdAt).toLocaleDateString('ru-RU')
      } as QuizRegistry)))
      .then(data => registry.value = data);

  fetch("/api/quiz/user/iam")
      .then(res => new Promise((resolve) => {
        if (res.status === 200) resolve(res.json());
      }).then(userJson => user.value = userJson as User));
})
</script>

<template>
  <div class="flex flex-col shrink items-center p-3 w-full xl:w-6xl mx-auto">
    <LiquidGlass class="w-full xm:w-3xl p-8 flex flex-col gap-8">
      <div class="w-full max-w-6xl mx-auto">

        <!-- Login section -->
        <LiquidGlass class="justify-end">
          <div class="flex justify-end gap-5 items-center" v-if="user">
            <span class="text-xl font-bold text-gray-800">
              {{ user.username }}
            </span>
            <a href="/api/quiz/user/logout"
               class="flex items-center justify-center gap-2 px-4 py-3 bg-gradient-to-r from-red-500 to-red-600 text-white rounded-xl font-medium hover:from-red-600 hover:to-red-700 transition-all duration-300 hover:shadow-lg group/play">
              <LogOut :size="20" class="group-hover/play:scale-110 transition-transform duration-300"/>
              <span>Выйти</span>
            </a>
          </div>
          <div class="flex justify-end" v-else>
            <a href="/login-user"
               class="flex items-center justify-center gap-2 px-4 py-3 bg-gradient-to-r from-blue-500 to-blue-600 text-white rounded-xl font-medium hover:from-blue-600 hover:to-blue-700 transition-all duration-300 hover:shadow-lg group/play">
              <LogIn :size="20" class="group-hover/play:scale-110 transition-transform duration-300"/>
              <span>Войти</span>
            </a>
          </div>
        </LiquidGlass>

        <!-- Hero Section -->
        <LiquidGlass class="w-full xm:w-3xl flex flex-col gap-8">
          <div class="p-8 text-center">
            <h1 class="text-4xl lg:text-5xl font-bold bg-gradient-to-r from-blue-600 to-purple-600 bg-clip-text text-transparent mb-4">
              Панель управления квизами
            </h1>
            <p class="text-xl text-gray-600 mb-8 font-medium">Создавайте, редактируйте и запускайте интерактивные викторины</p>
            <a href="/api/quiz/editor/create"
               target="_blank"
               class="inline-flex items-center gap-3 px-8 py-4 bg-gradient-to-r from-green-500 to-emerald-600 text-white rounded-2xl font-bold text-lg hover:from-green-600 hover:to-emerald-700 transition-all duration-300 hover:shadow-xl hover:scale-105 group">
              <FilePlusCorner :size="28" class="group-hover:rotate-12 transition-transform duration-300"/>
              <span>Создать новый квиз</span>
              <Sparkles :size="20" class="group-hover:animate-bounce"/>
            </a>
          </div>
        </LiquidGlass>

        <!-- Quiz List -->
        <LiquidGlass class="p-6" v-if="registry.length > 0">
          <div class="mb-8 p-4">
            <h2 class="text-3xl font-bold text-gray-800 mb-2">Доступные квизы</h2>
            <p class="text-gray-600">Выберите квиз для редактирования или запуска</p>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-2 gap-6">
            <div
                v-for="elem in registry"
                :key="elem.id"
                class="group relative overflow-hidden rounded-2xl bg-gradient-to-br from-white to-gray-50 border border-gray-200 hover:border-blue-300 transition-all duration-500 hover:shadow-xl"
            >
              <!-- Background effect -->
              <div
                  class="absolute inset-0 bg-gradient-to-r from-blue-500/0 via-blue-500/5 to-purple-500/0 opacity-0 group-hover:opacity-100 transition-opacity duration-500"></div>

              <div class="relative p-6">
                <!-- Quiz Info -->
                <div class="mb-4">
                  <h3 class="text-xl font-bold text-gray-800 mb-2 group-hover:text-blue-600 transition-colors duration-300">
                    {{ elem.title }}
                  </h3>
                  <div class="flex items-center justify-between text-sm text-gray-500">
                  <span class="flex items-center gap-1">
                    <svg class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                      <path fill-rule="evenodd"
                            d="M10 18a8 8 0 100-16 8 8 0 000 16zm1-12a1 1 0 10-2 0v4a1 1 0 00.293.707l2.828 2.829a1 1 0 101.415-1.415L11 9.586V6z"
                            clip-rule="evenodd"/>
                    </svg>
                    Создан: {{ elem.createdAt }}
                  </span>
                    <span class="px-2 py-1 bg-blue-100 text-blue-700 rounded-full text-xs font-medium">
                    {{ elem.questionCount }} вопросов
                  </span>
                  </div>
                </div>

                <!-- Action Buttons -->
                <div class="flex items-center justify-between gap-2 pt-4 border-t border-gray-100">
                  <a
                      :href="`/api/quiz/editor/${elem.id}/start`"
                      target="_blank"
                      class="flex-1 flex items-center justify-center gap-2 px-4 py-3 bg-gradient-to-r from-blue-500 to-blue-600 text-white rounded-xl font-medium hover:from-blue-600 hover:to-blue-700 transition-all duration-300 hover:shadow-lg group/play"
                  >
                    <CirclePlay :size="20" class="group-hover/play:scale-110 transition-transform duration-300"/>
                    <span>Запустить</span>
                  </a>

                  <a
                      :href="`/editor/${elem.id}`"
                      target="_blank"
                      class="flex-1 flex items-center justify-center gap-2 px-4 py-3 bg-gradient-to-r from-amber-500 to-amber-600 text-white rounded-xl font-medium hover:from-amber-600 hover:to-amber-700 transition-all duration-300 hover:shadow-lg group/edit"
                  >
                    <SquarePen :size="20" class="group-hover/edit:rotate-12 transition-transform duration-300"/>
                    <span>Редактировать</span>
                  </a>

                  <a
                      :href="`/api/quiz/editor/${elem.id}/delete`"
                      target="_blank"
                      onclick="return confirm('Удалить этот квиз?')"
                      class="p-3 bg-gradient-to-r from-rose-50 to-pink-50 text-rose-600 rounded-xl hover:from-rose-100 hover:to-pink-100 hover:text-rose-700 transition-all duration-300 hover:shadow-lg group/delete"
                  >
                    <Trash2 :size="20" class="group-hover/delete:scale-110 transition-transform duration-300"/>
                  </a>
                </div>
              </div>
            </div>
          </div>

          <!-- Empty State -->
          <div
              v-if="registry.length === 0"
              class="text-center py-12"
          >
            <div class="w-24 h-24 mx-auto mb-6 text-gray-300">
              <svg fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                      d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
              </svg>
            </div>
            <h3 class="text-2xl font-bold text-gray-400 mb-2">Квизов пока нет</h3>
            <p class="text-gray-500 mb-6">Создайте свой первый квиз и начните игру!</p>
            <a
                href="/api/quiz/editor/create"
                target="_blank"
                class="inline-flex items-center gap-2 px-6 py-3 bg-gradient-to-r from-blue-500 to-blue-600 text-white rounded-xl font-medium hover:from-blue-600 hover:to-blue-700 transition-all duration-300"
            >
              <FilePlusCorner :size="20"/>
              Создать первый квиз
            </a>
          </div>
        </LiquidGlass>
      </div>
    </LiquidGlass>
  </div>
</template>

<style scoped>
@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.hover-float:hover {
  animation: float 2s ease-in-out infinite;
}
</style>