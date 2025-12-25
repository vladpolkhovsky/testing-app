<script setup lang="ts">
import {provide} from "vue";
import {LocalStorageApi} from "@/service/LocalStorageApi.ts";
import ChristmasBackground from "@/component/ChristmasBackground.vue";

provide("LocalStorageApi", new LocalStorageApi());
</script>

<template>
<!--  <ChristmasBackground />-->
  <div class="min-h-screen flex relative z-0">
    <router-view />
  </div>
</template>

<style scoped>
.bg-moving {
  background-image: repeating-linear-gradient(-45deg, transparent, transparent 1rem, #ccc 1rem, #ccc 2rem);
  background-size: 200% 200%;
  animation: gradient 70s linear infinite;
}

@keyframes gradient {
  100% {
    background-position: 100% 100%;
  }
}
</style>
