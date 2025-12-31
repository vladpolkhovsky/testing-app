<script setup lang="ts">
import type {Question} from "@/model/Question.ts";
import Timer from "@/component/Timer.vue";
import {ref, onMounted, onUnmounted, useTemplateRef} from 'vue';

defineProps<Question>();

const timerRef = useTemplateRef<InstanceType<typeof Timer>>("timerRef")

</script>

<template>
  <div
      class="relative overflow-hidden rounded-3xl bg-gradient-to-br from-white/80 to-gray-50/80 backdrop-blur-sm border border-white/40 shadow-2xl">
    <!-- Decorative elements -->
    <div class="absolute top-0 right-0 w-32 h-32 -translate-y-16 translate-x-16 bg-blue-400/10 rounded-full blur-2xl"/>
    <div
        class="absolute bottom-0 left-0 w-40 h-40 translate-y-20 -translate-x-20 bg-purple-400/10 rounded-full blur-2xl"/>

    <!-- Question content -->
    <div class="relative p-4 lg:p-8">
      <div class="flex flex-row flex-wrap gap-3 items-start">

        <div class="flex-1">
          <div class="relative p-1 mb-2 border-b border-gray-200/50 flex justify-between">
            <div class="flex items-center gap-3">
              <div class="w-3 h-3 rounded-full bg-blue-500 animate-pulse"/>
              <span
                  class="text-xl md:text-2xl font-bold bg-gradient-to-r from-blue-600 to-purple-600 bg-clip-text text-transparent">Вопрос</span>
            </div>
          </div>

          <p class="text-xl md:text-2xl lg:text-3xl font-semibold text-gray-800 leading-tight min-w-75">
            {{ text }}
          </p>
        </div>

        <!-- Image -->
        <div v-if="imageId" class="relative flex-shrink-0">

          <a :href="`/api/quiz/image/${imageId}`" target="_blank">
            <div class="relative overflow-hidden rounded-2xl shadow-2xl group">
              <!-- Image frame decoration -->
              <div
                  class="absolute -inset-1 bg-gradient-to-r from-blue-500/20 to-purple-500/20 rounded-2xl blur group-hover:blur-lg transition duration-500"/>

              <!-- Image -->
              <img :src="`/api/quiz/image/${imageId}`"
                   alt="Картинка вопроса"
                   class="relative min-w-75 min-h-100 max-w-full max-h-145 hover:max-h-175 hover:min-w-95 rounded-xl transform group-hover:scale-[1.02] transition duration-500"
                   loading="lazy"
              />

              <!-- Image overlay effect -->
              <div
                  class="absolute inset-0 bg-gradient-to-t from-black/10 to-transparent opacity-0 group-hover:opacity-100 transition duration-500 rounded-xl"/>
            </div>
          </a>
        </div>
      </div>
    </div>

    <!-- Bottom decoration -->
    <div class="px-6 pb-2">
      <div class="h-px bg-gradient-to-r from-transparent via-gray-300/50 to-transparent"/>
    </div>
  </div>
</template>