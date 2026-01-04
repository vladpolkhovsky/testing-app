<template>
  <div class="relative p-6">
    <!-- Main content -->
    <div class="p-3">
      <slot />
    </div>
  </div>
</template>

<style scoped>
@keyframes gradient-x {

  0%,
  100% {
    background-position: 0 50%;
  }

  50% {
    background-position: 100% 50%;
  }
}
</style>