<script setup lang="ts">
</script>

<template>
  <div class="bg-moving min-h-screen flex">
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
