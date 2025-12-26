<script setup lang="ts">
import type { Question } from "@/model/Question.ts";

defineProps<Question>();
</script>

<template>
  <div class="relative overflow-hidden rounded-3xl bg-gradient-to-br from-white/80 to-gray-50/80 backdrop-blur-sm border border-white/40 shadow-2xl">
    <!-- Decorative elements -->
    <div class="absolute top-0 right-0 w-32 h-32 -translate-y-16 translate-x-16 bg-blue-400/10 rounded-full blur-2xl"/>
    <div class="absolute bottom-0 left-0 w-40 h-40 translate-y-20 -translate-x-20 bg-purple-400/10 rounded-full blur-2xl"/>

    <!-- Question header -->
    <div class="relative p-6 border-b border-gray-200/50">
      <div class="flex items-center gap-3">
        <div class="w-3 h-3 rounded-full bg-blue-500 animate-pulse"/>
        <h1 class="text-2xl md:text-3xl font-bold bg-gradient-to-r from-blue-600 to-purple-600 bg-clip-text text-transparent">
          Вопрос
        </h1>
      </div>
    </div>

    <!-- Question content -->
    <div class="relative p-6 md:p-8">
      <div class="flex flex-col lg:flex-row gap-8 items-start">
        <!-- Question text -->
        <div class="flex-1">
          <p class="text-2xl md:text-3xl lg:text-4xl font-semibold text-gray-800 leading-tight">
            {{ text }}
          </p>
        </div>

        <!-- Image -->
        <div v-if="imageId" class="relative flex-shrink-0">
          <div class="relative overflow-hidden rounded-2xl shadow-2xl group">
            <!-- Image frame decoration -->
            <div class="absolute -inset-1 bg-gradient-to-r from-blue-500/20 to-purple-500/20 rounded-2xl blur group-hover:blur-lg transition duration-500"/>

            <!-- Image -->
            <img
                :src="`/api/quiz/image/${imageId}`"
                alt="Вопрос изображение"
                class="relative w-full max-w-xs md:max-w-sm lg:max-w-md rounded-xl transform group-hover:scale-[1.02] transition duration-500"
                loading="lazy"
            />

            <!-- Image overlay effect -->
            <div class="absolute inset-0 bg-gradient-to-t from-black/10 to-transparent opacity-0 group-hover:opacity-100 transition duration-500 rounded-xl"/>
          </div>
        </div>
      </div>
    </div>

    <!-- Bottom decoration -->
    <div class="px-6 pb-6">
      <div class="h-px bg-gradient-to-r from-transparent via-gray-300/50 to-transparent"/>
    </div>
  </div>
</template>