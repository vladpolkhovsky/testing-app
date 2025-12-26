<template>
  <div class="relative p-6 group">
    <!-- Main content -->
    <div
        class="relative bg-white/50 backdrop-blur-xl rounded-2xl border border-white/40 shadow-2xl shadow-black/5 overflow-hidden">
      <!-- Inner shine effect -->
      <div class="absolute top-0 left-0 right-0 h-px bg-gradient-to-r from-transparent via-white/50 to-transparent"/>

      <!-- Content slot -->
      <div class="p-6">
        <slot/>
      </div>

      <!-- Bottom glow -->
      <div
          class="absolute bottom-0 left-0 right-0 h-px bg-gradient-to-r from-blue-400/20 via-purple-400/20 to-pink-400/20"/>
    </div>
  </div>
</template>

<style scoped>
@keyframes gradient-x {
  0%, 100% {
    background-position: 0 50%;
  }
  50% {
    background-position: 100% 50%;
  }
}
</style>