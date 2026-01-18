<script setup lang="ts">
import { onMounted, onUnmounted } from 'vue'
import type { ParticleConfig } from '@/utils/particleTypes'
import { useParticleAnimation } from '@/composables/useParticleAnimation'

interface Props {
  config?: Partial<ParticleConfig>
  treeImagePath?: string
}

const props = withDefaults(defineProps<Props>(), {
  treeImagePath: '/dasha.png'
})

const {
  canvasRef,
  start,
  stop,
  handleResize
} = useParticleAnimation({
  config: props.config,
  treeImagePath: props.treeImagePath
})

onMounted(() => {
  start()
  window.addEventListener('resize', handleResize)

  onUnmounted(() => {
    stop()
    window.removeEventListener('resize', handleResize)
  })
})
</script>

<template>
  <div class="christmas-background">
    <canvas ref="canvasRef" class="animation-canvas" />
    <div class="twinkles"></div>
    <div class="gradient-overlay"></div>
  </div>
</template>

<style scoped>
.christmas-background {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
      180deg,
      #0a0e2a 0%,
      #1a1035 30%,
      #2d1b69 70%,
      #4a2c92 100%
  );
  overflow: hidden;
  z-index: -1;
}

.animation-canvas {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

/* Static background twinkles */
.twinkles {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image:
      radial-gradient(1px 1px at 50px 100px, #fff 50%, transparent 50%),
      radial-gradient(1px 1px at 150px 200px, #fff 50%, transparent 50%),
      radial-gradient(1px 1px at 250px 150px, #fff 50%, transparent 50%),
      radial-gradient(1px 1px at 350px 250px, #fff 50%, transparent 50%),
      radial-gradient(1px 1px at 450px 50px, #fff 50%, transparent 50%);
  animation: twinkleAnim 4s ease-in-out infinite;
  opacity: 0.3;
}

.gradient-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(
      to bottom,
      rgba(10, 14, 42, 0.3) 0%,
      transparent 20%,
      transparent 80%,
      rgba(74, 44, 146, 0.3) 100%
  );
  pointer-events: none;
}

@keyframes twinkleAnim {
  0%, 100% {
    opacity: 0.2;
  }
  50% {
    opacity: 0.5;
  }
}
</style>