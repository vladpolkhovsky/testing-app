<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

interface Tree {
  id: number
  left: number
  startTop: number
  delay: number
  duration: number
  size: number
  rotation: number
  horizontalDrift: number
  styleVariant: number
  speed: number
}

const TREE_COUNT = 8
const MAX_TREES = 20

const trees = ref<Tree[]>([])
let nextId = 0
let animationFrameId: number | null = null
let lastTime = 0

// Generate initial trees
const generateInitialTrees = () => {
  for (let i = 0; i < TREE_COUNT; i++) {
    trees.value.push(createTree(true))
  }
}

const createTree = (isInitial: boolean = false): Tree => {
  return {
    id: nextId++,
    left: Math.random() * 95, // Leave 5% margin
    startTop: isInitial ? Math.random() * -300 - 100 : -150,
    delay: isInitial ? Math.random() * 3 : 0,
    duration: Math.random() * 10 + 15,
    size: Math.random() * 0.7 + 0.4,
    rotation: Math.random() * 360,
    horizontalDrift: (Math.random() - 0.5) * 1.5,
    styleVariant: Math.floor(Math.random() * 3),
    speed: Math.random() * 0.8 + 0.6
  }
}

// Smooth animation using requestAnimationFrame
const animateTrees = (timestamp: number) => {
  if (!lastTime) lastTime = timestamp
  const delta = timestamp - lastTime

  trees.value = trees.value.map(tree => {
    const progress = delta / 1000
    const rotationSpeed = tree.speed * 0.5

    return {
      ...tree,
      startTop: tree.startTop + tree.speed * 5,
      rotation: (tree.rotation + rotationSpeed) % 360,
      left: tree.left + tree.horizontalDrift * progress
    }
  }).filter(tree => {
    // Remove trees that are out of view
    return tree.startTop < window.innerHeight + 200
  })

  lastTime = timestamp
  animationFrameId = requestAnimationFrame(animateTrees)
}

// Add new trees periodically
let addInterval: NodeJS.Timeout

onMounted(() => {
  generateInitialTrees()
  animationFrameId = requestAnimationFrame(animateTrees)

  addInterval = setInterval(() => {
    if (trees.value.length < MAX_TREES) {
      trees.value.push(createTree(false))
    }
  }, 1500)
})

onUnmounted(() => {
  if (animationFrameId) {
    cancelAnimationFrame(animationFrameId)
  }
  if (addInterval) {
    clearInterval(addInterval)
  }
})
</script>

<template>
  <div class="christmas-background">
    <!-- Background decorative elements -->
    <div class="snowfall"></div>
    <div class="twinkles"></div>

    <!-- SVG Filters (hidden but needed for effects) -->
    <svg style="position: absolute; width: 0; height: 0; pointer-events: none;">
      <defs>
        <filter id="tree-glow">
          <feGaussianBlur stdDeviation="2" result="coloredBlur"/>
          <feMerge>
            <feMergeNode in="coloredBlur"/>
            <feMergeNode in="SourceGraphic"/>
          </feMerge>
        </filter>
        <filter id="ornament-glow">
          <feGaussianBlur in="SourceAlpha" stdDeviation="4"/>
          <feOffset dx="0" dy="0" result="offsetblur"/>
          <feFlood flood-color="white" flood-opacity="0.5"/>
          <feComposite in2="offsetblur" operator="in"/>
          <feMerge>
            <feMergeNode/>
            <feMergeNode in="SourceGraphic"/>
          </feMerge>
        </filter>
      </defs>
    </svg>

    <!-- Trees -->
    <div
        v-for="tree in trees"
        :key="tree.id"
        class="tree-container"
        :style="{
        left: tree.left + '%',
        top: tree.startTop + 'px',
        '--delay': tree.delay + 's',
        '--size': tree.size,
        '--rotation': tree.rotation + 'deg',
        opacity: Math.max(0.2, Math.min(1, 0.7 + tree.startTop / 500))
      }"
    >
      <!-- Modern Christmas Tree SVG -->
      <svg
          viewBox="0 0 200 240"
          class="tree-svg"
          :style="{
          filter: `drop-shadow(0 10px 20px rgba(0, 100, 0, 0.3))
                   drop-shadow(0 0 ${10 * tree.size}px rgba(255, 255, 200, 0.2))`
        }"
      >
        <!-- Trunk -->
        <rect x="88" y="160" width="24" height="40" class="tree-trunk" />

        <!-- Tree Body - 3D Pyramid Style -->
        <g class="tree-body">
          <!-- Bottom layer - dark -->
          <polygon
              points="100,40 30,170 170,170"
              class="tree-layer tree-layer-1"
          />

          <!-- Middle layer - medium -->
          <polygon
              points="100,50 40,160 160,160"
              class="tree-layer tree-layer-2"
          />

          <!-- Top layer - light -->
          <polygon
              points="100,60 50,150 150,150"
              class="tree-layer tree-layer-3"
          />
        </g>

        <!-- Decorative Lines -->
        <g class="tree-lines">
          <line x1="100" y1="65" x2="100" y2="90" class="tree-line" />
          <line x1="95" y1="75" x2="70" y2="100" class="tree-line" />
          <line x1="105" y1="75" x2="130" y2="100" class="tree-line" />
          <line x1="90" y1="100" x2="50" y2="130" class="tree-line" />
          <line x1="110" y1="100" x2="150" y2="130" class="tree-line" />
        </g>

        <!-- Star -->
        <g class="tree-star">
          <polygon
              points="100,25 104,35 115,35 107,40 110,50 100,45 90,50 93,40 85,35 96,35"
              class="star-inner"
          />
          <polygon
              points="100,20 106,35 120,35 110,45 115,60 100,50 85,60 90,45 80,35 94,35"
              class="star-outer"
          />
        </g>

        <!-- Ornaments -->
        <g class="ornaments">
          <!-- Left side ornaments -->
          <circle cx="75" cy="95" r="6" class="ornament ornament-1" />
          <circle cx="60" cy="120" r="5" class="ornament ornament-2" />
          <circle cx="85" cy="130" r="5" class="ornament ornament-3" />

          <!-- Right side ornaments -->
          <circle cx="125" cy="95" r="6" class="ornament ornament-4" />
          <circle cx="140" cy="120" r="5" class="ornament ornament-5" />
          <circle cx="115" cy="130" r="5" class="ornament ornament-6" />

          <!-- Center ornaments -->
          <circle cx="100" cy="80" r="7" class="ornament ornament-7" />
          <circle cx="100" cy="115" r="6" class="ornament ornament-8" />
        </g>

        <!-- Lights -->
        <g class="lights">
          <circle cx="70" cy="80" r="3" class="light light-1" />
          <circle cx="130" cy="80" r="3" class="light light-2" />
          <circle cx="95" cy="100" r="3" class="light light-3" />
          <circle cx="105" cy="105" r="3" class="light light-4" />
          <circle cx="80" cy="120" r="3" class="light light-5" />
          <circle cx="120" cy="125" r="3" class="light light-6" />
        </g>

        <!-- Snow on branches -->
        <g class="snow">
          <circle cx="65" cy="100" r="2" class="snow-flake" />
          <circle cx="135" cy="110" r="2" class="snow-flake" />
          <circle cx="100" cy="140" r="2" class="snow-flake" />
          <circle cx="90" cy="90" r="1.5" class="snow-flake" />
          <circle cx="110" cy="115" r="1.5" class="snow-flake" />
        </g>
      </svg>
    </div>
  </div>
</template>

<style scoped>
.christmas-background {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(180deg,
  #0a0e2a 0%,
  #1a1035 30%,
  #2d1b69 70%,
  #4a2c92 100%);
  pointer-events: none;
  overflow: hidden;
  z-index: -1;
}

.tree-container {
  position: absolute;
  will-change: transform, opacity;
  transition: opacity 0.3s ease;
  transform: rotate(var(--rotation));
  transform-origin: center;
  animation: float 4s ease-in-out infinite;
  animation-delay: var(--delay);
}

.tree-svg {
  width: calc(80px * var(--size));
  height: auto;
  transition: transform 0.3s ease;
}

.tree-container:hover .tree-svg {
  transform: scale(1.1);
  filter: drop-shadow(0 0 30px rgba(255, 255, 200, 0.5)) !important;
}

/* Tree Styles */
.tree-trunk {
  fill: #8B4513;
  stroke: #5D2906;
  stroke-width: 2;
}

.tree-body {
  filter: url(#tree-glow);
}

.tree-layer {
  stroke: #2a5c2a;
  stroke-width: 1.5;
}

.tree-layer-1 {
  fill: #0c3b0c;
  stroke: #1e4d1e;
}

.tree-layer-2 {
  fill: #1e6b1e;
  stroke: #2d7a2d;
}

.tree-layer-3 {
  fill: #2d8b2d;
  stroke: #3c9c3c;
}

.tree-lines .tree-line {
  stroke: #a0522d;
  stroke-width: 1;
  opacity: 0.6;
}

/* Star */
.tree-star {
  animation: starPulse 2s ease-in-out infinite;
  transform-origin: center;
}

.star-inner {
  fill: #FFD700;
  filter: drop-shadow(0 0 10px gold);
}

.star-outer {
  fill: #FFEC8B;
  opacity: 0.8;
}

/* Ornaments */
.ornaments .ornament {
  animation: ornamentFloat 3s ease-in-out infinite;
  filter: url(#ornament-glow);
}

.ornament-1 { fill: #FF6B6B; animation-delay: 0s; }
.ornament-2 { fill: #4ECDC4; animation-delay: 0.5s; }
.ornament-3 { fill: #FFD166; animation-delay: 1s; }
.ornament-4 { fill: #06D6A0; animation-delay: 1.5s; }
.ornament-5 { fill: #118AB2; animation-delay: 2s; }
.ornament-6 { fill: #EF476F; animation-delay: 2.5s; }
.ornament-7 { fill: #FFD700; animation-delay: 0.2s; }
.ornament-8 { fill: #9D4EDD; animation-delay: 0.7s; }

/* Lights */
.lights .light {
  animation: lightTwinkle 1.5s ease-in-out infinite;
  filter: drop-shadow(0 0 8px currentColor);
}

.light-1 { fill: #FF0000; animation-delay: 0s; }
.light-2 { fill: #00FF00; animation-delay: 0.3s; }
.light-3 { fill: #FFFF00; animation-delay: 0.6s; }
.light-4 { fill: #00FFFF; animation-delay: 0.9s; }
.light-5 { fill: #FF00FF; animation-delay: 1.2s; }
.light-6 { fill: #FFA500; animation-delay: 1.5s; }

/* Snow */
.snow-flake {
  fill: white;
  animation: snowTwinkle 4s ease-in-out infinite;
}

/* Animations */
@keyframes float {
  0%, 100% {
    transform: rotate(var(--rotation)) translateY(0);
  }
  50% {
    transform: rotate(var(--rotation)) translateY(-20px);
  }
}

@keyframes starPulse {
  0%, 100% {
    transform: scale(1);
    filter: drop-shadow(0 0 10px gold);
  }
  50% {
    transform: scale(1.2);
    filter: drop-shadow(0 0 20px gold);
  }
}

@keyframes ornamentFloat {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-8px);
  }
}

@keyframes lightTwinkle {
  0%, 100% {
    opacity: 0.7;
    r: 3;
  }
  50% {
    opacity: 1;
    r: 4;
  }
}

@keyframes snowTwinkle {
  0%, 100% {
    opacity: 0.5;
  }
  50% {
    opacity: 1;
  }
}

/* Background effects */
.snowfall {
  position: absolute;
  top: -100px;
  left: 0;
  width: 100%;
  height: 100%;
  background-image:
      radial-gradient(2px 2px at 20px 30px, #EEE 50%, transparent 50%),
      radial-gradient(2px 2px at 40px 70px, #FFF 50%, transparent 50%),
      radial-gradient(2px 2px at 60px 20px, #EEE 50%, transparent 50%),
      radial-gradient(3px 3px at 90px 50px, #FFF 50%, transparent 50%),
      radial-gradient(2px 2px at 130px 80px, #EEE 50%, transparent 50%),
      radial-gradient(3px 3px at 160px 20px, #FFF 50%, transparent 50%);
  background-size: 200px 200px;
  animation: snowfallAnim 20s linear infinite;
  opacity: 0.6;
}

.twinkles {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image:
      radial-gradient(1px 1px at 50px 100px, #FFF 50%, transparent 50%),
      radial-gradient(1px 1px at 150px 200px, #FFF 50%, transparent 50%),
      radial-gradient(1px 1px at 250px 150px, #FFF 50%, transparent 50%),
      radial-gradient(1px 1px at 350px 250px, #FFF 50%, transparent 50%),
      radial-gradient(1px 1px at 450px 50px, #FFF 50%, transparent 50%);
  animation: twinkleAnim 4s ease-in-out infinite;
}

@keyframes snowfallAnim {
  0% {
    transform: translateY(-100px);
  }
  100% {
    transform: translateY(100vh);
  }
}

@keyframes twinkleAnim {
  0%, 100% {
    opacity: 0.2;
  }
  50% {
    opacity: 0.8;
  }
}
</style>