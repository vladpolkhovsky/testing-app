<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

// ===================== CONFIGURATION =====================
const CONFIG = {
  // Trees
  TREE_COUNT: 15,
  TREE_MIN_SIZE: 30,
  TREE_MAX_SIZE: 60,
  TREE_MIN_SPEED: 0.25,
  TREE_MAX_SPEED: 1,
  TREE_WIND: 0.3,
  TREE_SWING: 0.5,
  TREE_OPACITY: 0.9,

  // Tree Swing Animation
  TREE_SWING_SPEED_MIN: 0.25,
  TREE_SWING_SPEED_MAX: 1,
  TREE_SWING_ANGLE_MAX: 30,

  // Snowflakes
  SNOWFLAKE_COUNT: 150,
  SNOWFLAKE_MIN_SIZE: 1,
  SNOWFLAKE_MAX_SIZE: 8,
  SNOWFLAKE_MIN_SPEED: 0.5,
  SNOWFLAKE_MAX_SPEED: 2.5,
  SNOWFLAKE_WIND: 0.8,
  SNOWFLAKE_SWING: 1.2,
  SNOWFLAKE_OPACITY: 0.7,

  // Colors
  TREE_COLORS: [
    { dark: '#0c3b0c', medium: '#1e6b1e', light: '#2d8b2d' },
    { dark: '#1a3c1a', medium: '#2d5a2d', light: '#3c693c' },
    { dark: '#2c5545', medium: '#3d7a5c', light: '#4a9c73' }
  ],
  ORNAMENT_COLORS: ['#FF6B6B', '#4ECDC4', '#FFD166', '#06D6A0', '#118AB2', '#EF476F', '#FFD700', '#9D4EDD']
}

// ===================== CANVAS SETUP =====================
const canvasRef = ref<HTMLCanvasElement | null>(null)
let ctx: CanvasRenderingContext2D | null = null
let animationFrameId: number | null = null
let treeImage: HTMLImageElement | null = null

const loadTreeImage = (): Promise<HTMLImageElement> => {
  return new Promise((resolve, reject) => {
    const img = new Image()
    img.onload = () => resolve(img)
    img.onerror = reject
    img.src = '/christmas_tree.png'
  })
}

// ===================== PARTICLE DEFINITIONS =====================
interface BaseParticle {
  x: number
  y: number
  r: number
  velX: number
  velY: number
  swing: number
  opacity: number
}

interface TreeParticle extends BaseParticle {
  type: 'tree'
  colorVariant: number
  ornaments: { x: number; y: number; color: string; size: number }[]
  lights: { x: number; y: number; color: string }[]
  rotation: number
  swingSpeed: number
  swingAngle: number
  swingDirection: number
}

interface SnowflakeParticle extends BaseParticle {
  type: 'snowflake'
  drift: number
  rotation: number
  rotationSpeed: number
}

type Particle = TreeParticle | SnowflakeParticle

const particles = ref<Particle[]>([])

// ===================== UTILITY FUNCTIONS =====================
const random = (min: number, max: number): number => {
  return Math.random() * (max - min) + min
}

const getRGB = (hex: string): string => {
  if (hex.indexOf('#') === 0) {
    if (hex.length === 7) {
      const r = parseInt(hex.substring(1, 3), 16)
      const g = parseInt(hex.substring(3, 5), 16)
      const b = parseInt(hex.substring(5, 7), 16)
      return `${r},${g},${b}`
    }
  }
  return '255,255,255'
}

// ===================== PARTICLE CREATION =====================
const createTreeParticle = (canvasWidth: number, canvasHeight: number): TreeParticle => {
  const colorVariant = Math.floor(Math.random() * CONFIG.TREE_COLORS.length)
  const size = random(CONFIG.TREE_MIN_SIZE, CONFIG.TREE_MAX_SIZE)

  // Create ornaments for this tree
  const ornaments = Array.from({ length: 8 }, (_, i) => ({
    x: random(-size * 0.6, size * 0.6),
    y: random(-size * 0.8, size * 0.4),
    color: CONFIG.ORNAMENT_COLORS[Math.floor(Math.random() * CONFIG.ORNAMENT_COLORS.length)] || '#FF6B6B',
    size: random(2, 4)
  }))

  // Create lights for this tree
  const lights = Array.from({ length: 6 }, (_, i) => ({
    x: random(-size * 0.5, size * 0.5),
    y: random(-size * 0.6, size * 0.2),
    color: ['#FF0000', '#00FF00', '#FFFF00', '#00FFFF', '#FF00FF', '#FFA500'][i] || '#FF0000'
  }))

  return {
    type: 'tree',
    x: random(0, canvasWidth),
    y: random(0, canvasHeight),
    r: size,
    velX: CONFIG.TREE_WIND * random(-0.5, 0.5),
    velY: random(CONFIG.TREE_MIN_SPEED, CONFIG.TREE_MAX_SPEED),
    swing: random(0, 2 * Math.PI),
    opacity: CONFIG.TREE_OPACITY * random(0.8, 1),
    colorVariant,
    ornaments,
    lights,
    rotation: random(-CONFIG.TREE_SWING_ANGLE_MAX, CONFIG.TREE_SWING_ANGLE_MAX),
    swingSpeed: random(CONFIG.TREE_SWING_SPEED_MIN, CONFIG.TREE_SWING_SPEED_MAX),
    swingAngle: random(15, CONFIG.TREE_SWING_ANGLE_MAX),
    swingDirection: Math.random() > 0.5 ? 1 : -1
  }
}

const createSnowflakeParticle = (canvasWidth: number, canvasHeight: number): SnowflakeParticle => {
  return {
    type: 'snowflake',
    x: random(0, canvasWidth),
    y: random(0, canvasHeight),
    r: random(CONFIG.SNOWFLAKE_MIN_SIZE, CONFIG.SNOWFLAKE_MAX_SIZE),
    velX: CONFIG.SNOWFLAKE_WIND * random(-0.3, 0.3),
    velY: random(CONFIG.SNOWFLAKE_MIN_SPEED, CONFIG.SNOWFLAKE_MAX_SPEED),
    swing: random(0, 2 * Math.PI),
    opacity: CONFIG.SNOWFLAKE_OPACITY * random(0.5, 1),
    drift: random(-0.2, 0.2),
    rotation: random(0, 360),
    rotationSpeed: random(-1, 1)
  }
}

// ===================== DRAWING FUNCTIONS =====================
const drawTree = (particle: TreeParticle) => {
  if (!ctx || !treeImage) return

  // Save context state
  ctx.save()

  // Translate to tree position
  ctx.translate(particle.x, particle.y)

  // Apply swing rotation
  ctx.rotate(particle.rotation * Math.PI / 180)

  // Apply opacity
  ctx.globalAlpha = particle.opacity

  // Draw the tree image, scaled to size
  const imageHeight = particle.r * 3
  const imageWidth = (treeImage.width / treeImage.height) * imageHeight
  ctx.drawImage(treeImage, -imageWidth / 2, -imageHeight / 2, imageWidth, imageHeight)

  // Restore context
  ctx.restore()
}

const drawSnowflake = (particle: SnowflakeParticle) => {
  if (!ctx) return

  ctx.save()
  ctx.translate(particle.x, particle.y)
  ctx.rotate(particle.rotation * Math.PI / 180)
  ctx.globalAlpha = particle.opacity

  // Complex snowflake design
  const branches = 6
  for (let i = 0; i < branches; i++) {
    const angle = (i * 360 / branches) * Math.PI / 180

    // Main branch
    ctx.beginPath()
    ctx.moveTo(0, 0)
    ctx.lineTo(Math.cos(angle) * particle.r * 1.5, Math.sin(angle) * particle.r * 1.5)
    ctx.strokeStyle = `rgba(${getRGB('#FFFFFF')},${particle.opacity})`
    ctx.lineWidth = 1
    ctx.stroke()

    // Side branches
    for (let j = 1; j <= 2; j++) {
      const pos = particle.r * 0.6 * j
      const sideAngle = angle + 45 * Math.PI / 180
      const sideAngle2 = angle - 45 * Math.PI / 180

      ctx.beginPath()
      ctx.moveTo(Math.cos(angle) * pos, Math.sin(angle) * pos)
      ctx.lineTo(Math.cos(sideAngle) * particle.r * 0.4 + Math.cos(angle) * pos,
          Math.sin(sideAngle) * particle.r * 0.4 + Math.sin(angle) * pos)
      ctx.stroke()

      ctx.beginPath()
      ctx.moveTo(Math.cos(angle) * pos, Math.sin(angle) * pos)
      ctx.lineTo(Math.cos(sideAngle2) * particle.r * 0.4 + Math.cos(angle) * pos,
          Math.sin(sideAngle2) * particle.r * 0.4 + Math.sin(angle) * pos)
      ctx.stroke()
    }
  }

  // Center dot
  ctx.beginPath()
  ctx.arc(0, 0, particle.r * 0.3, 0, Math.PI * 2)
  ctx.fillStyle = `rgba(${getRGB('#FFFFFF')},${particle.opacity})`
  ctx.fill()

  ctx.restore()
}

// ===================== ANIMATION LOOP =====================
const initParticles = () => {
  if (!canvasRef.value) return

  const canvas = canvasRef.value
  ctx = canvas.getContext('2d')
  if (!ctx) return

  const { width, height } = canvas.getBoundingClientRect()
  canvas.width = width
  canvas.height = height

  // Clear existing particles
  particles.value = []

  // Create trees
  for (let i = 0; i < CONFIG.TREE_COUNT; i++) {
    particles.value.push(createTreeParticle(width, height))
  }

  // Create snowflakes
  for (let i = 0; i < CONFIG.SNOWFLAKE_COUNT; i++) {
    particles.value.push(createSnowflakeParticle(width, height))
  }
}

const animate = () => {
  if (!canvasRef.value || !ctx) return

  const canvas = canvasRef.value
  const width = canvas.width
  const height = canvas.height

  // Clear canvas
  ctx.clearRect(0, 0, width, height)

  // Update and draw each particle
  particles.value.forEach(particle => {
    // Update physics based on particle type
    if (particle.type === 'tree') {
      const treeParticle = particle as TreeParticle

      // Tree-specific physics
      treeParticle.velX = Math.abs(treeParticle.velX) < Math.abs(CONFIG.TREE_WIND)
          ? treeParticle.velX + CONFIG.TREE_WIND / 20
          : CONFIG.TREE_WIND

      treeParticle.swing += 0.03
      treeParticle.x += Math.cos(treeParticle.swing) * CONFIG.TREE_SWING * treeParticle.opacity + treeParticle.velX * 0.5
      treeParticle.y += treeParticle.velY * 0.5

      // Update swing rotation (pendulum motion)
      treeParticle.rotation += treeParticle.swingSpeed * treeParticle.swingDirection

      // Reverse direction when reaching max angle
      if (Math.abs(treeParticle.rotation) >= treeParticle.swingAngle) {
        treeParticle.swingDirection *= -1
        // Clamp the rotation to prevent overshoot
        treeParticle.rotation = Math.max(-treeParticle.swingAngle,
            Math.min(treeParticle.swingAngle, treeParticle.rotation))
      }

      // Add a little bounce effect at the extremes
      if (Math.abs(treeParticle.rotation) > treeParticle.swingAngle * 0.8) {
        treeParticle.swingSpeed *= 0.98
      } else {
        treeParticle.swingSpeed = Math.min(
            treeParticle.swingSpeed * 1.02,
            random(CONFIG.TREE_SWING_SPEED_MIN, CONFIG.TREE_SWING_SPEED_MAX)
        )
      }

    } else {
      // Snowflake-specific physics
      const snowflakeParticle = particle as SnowflakeParticle
      snowflakeParticle.velX = Math.abs(snowflakeParticle.velX) < Math.abs(CONFIG.SNOWFLAKE_WIND)
          ? snowflakeParticle.velX + CONFIG.SNOWFLAKE_WIND / 20
          : CONFIG.SNOWFLAKE_WIND

      snowflakeParticle.swing += 0.03
      snowflakeParticle.x += Math.cos(snowflakeParticle.swing) * CONFIG.SNOWFLAKE_SWING * snowflakeParticle.opacity + snowflakeParticle.velX * 0.5 + snowflakeParticle.drift
      snowflakeParticle.y += snowflakeParticle.velY * 0.5
      snowflakeParticle.rotation += snowflakeParticle.rotationSpeed
    }

    // Check boundaries and reset if needed
    const margin = particle.r * 2
    if (particle.x > width + margin || particle.x < -margin ||
        particle.y > height + margin || particle.y < -margin) {
      resetParticle(particle, width, height)
    }

    // Draw the particle
    if (particle.type === 'tree') {
      drawTree(particle as TreeParticle)
    } else {
      drawSnowflake(particle as SnowflakeParticle)
    }
  })

  // Continue animation
  animationFrameId = requestAnimationFrame(animate)
}

const resetParticle = (particle: Particle, width: number, height: number) => {
  const prevR = particle.r

  if (particle.type === 'tree') {
    const treeParticle = particle as TreeParticle
    treeParticle.r = random(CONFIG.TREE_MIN_SIZE, CONFIG.TREE_MAX_SIZE)
  } else {
    const snowflakeParticle = particle as SnowflakeParticle
    snowflakeParticle.r = random(CONFIG.SNOWFLAKE_MIN_SIZE, CONFIG.SNOWFLAKE_MAX_SIZE)
  }

  if (particle.x > width + prevR) {
    particle.x = -particle.r
    particle.y = random(0, height)
  } else if (particle.x < -prevR) {
    particle.x = width + particle.r
    particle.y = random(0, height)
  } else {
    particle.x = random(0, width)
    particle.y = -particle.r
  }

  if (particle.type === 'tree') {
    const treeParticle = particle as TreeParticle
    treeParticle.velX = CONFIG.TREE_WIND * random(-0.5, 0.5)
    treeParticle.velY = random(CONFIG.TREE_MIN_SPEED, CONFIG.TREE_MAX_SPEED)
    treeParticle.swing = random(0, 2 * Math.PI)
    treeParticle.opacity = CONFIG.TREE_OPACITY * random(0.8, 1)
    treeParticle.rotation = random(-CONFIG.TREE_SWING_ANGLE_MAX, CONFIG.TREE_SWING_ANGLE_MAX)
    treeParticle.swingSpeed = random(CONFIG.TREE_SWING_SPEED_MIN, CONFIG.TREE_SWING_SPEED_MAX)
    treeParticle.swingAngle = random(15, CONFIG.TREE_SWING_ANGLE_MAX)
    treeParticle.swingDirection = Math.random() > 0.5 ? 1 : -1
  } else {
    const snowflakeParticle = particle as SnowflakeParticle
    snowflakeParticle.velX = CONFIG.SNOWFLAKE_WIND * random(-0.3, 0.3)
    snowflakeParticle.velY = random(CONFIG.SNOWFLAKE_MIN_SPEED, CONFIG.SNOWFLAKE_MAX_SPEED)
    snowflakeParticle.swing = random(0, 2 * Math.PI)
    snowflakeParticle.opacity = CONFIG.SNOWFLAKE_OPACITY * random(0.5, 1)
    snowflakeParticle.rotationSpeed = random(-1, 1)
    snowflakeParticle.drift = random(-0.2, 0.2)
  }
}

// ===================== LIFECYCLE =====================
onMounted(() => {
  loadTreeImage().then(img => {
    treeImage = img
    initParticles()
    animationFrameId = requestAnimationFrame(animate)
  }).catch(err => {
    console.error('Failed to load tree image:', err)
  })

  // Handle window resize
  const handleResize = () => {
    if (!canvasRef.value) return
    const canvas = canvasRef.value
    const { width, height } = canvas.getBoundingClientRect()
    canvas.width = width
    canvas.height = height

    // Adjust particles for new canvas size
    particles.value.forEach(particle => {
      particle.x = (particle.x / (canvas.width || 1)) * width
      particle.y = (particle.y / (canvas.height || 1)) * height
    })
  }

  window.addEventListener('resize', handleResize)

  onUnmounted(() => {
    if (animationFrameId) {
      cancelAnimationFrame(animationFrameId)
    }
    window.removeEventListener('resize', handleResize)
  })
})
</script>

<template>
  <div class="christmas-background">
    <!-- Canvas for all animations -->
    <canvas
        ref="canvasRef"
        class="animation-canvas"
    />

    <!-- Static background elements -->
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
  background: linear-gradient(180deg,
  #0a0e2a 0%,
  #1a1035 30%,
  #2d1b69 70%,
  #4a2c92 100%);
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
      radial-gradient(1px 1px at 50px 100px, #FFF 50%, transparent 50%),
      radial-gradient(1px 1px at 150px 200px, #FFF 50%, transparent 50%),
      radial-gradient(1px 1px at 250px 150px, #FFF 50%, transparent 50%),
      radial-gradient(1px 1px at 350px 250px, #FFF 50%, transparent 50%),
      radial-gradient(1px 1px at 450px 50px, #FFF 50%, transparent 50%);
  animation: twinkleAnim 4s ease-in-out infinite;
  opacity: 0.3;
}

/* Gradient overlay for depth */
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