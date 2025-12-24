<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

// ===================== CONFIGURATION =====================
const CONFIG = {
  // Trees
  TREE_COUNT: 15,
  TREE_MIN_SIZE: 45,
  TREE_MAX_SIZE: 60,
  TREE_MIN_SPEED: 0.75,
  TREE_MAX_SPEED: 2.5,
  TREE_WIND: 0.3,
  TREE_OPACITY: 0.9,

  // Tree Swing Animation
  TREE_SWING_SPEED_MIN: 0.25,
  TREE_SWING_SPEED_MAX: 0.75,
  TREE_SWING_ANGLE_MAX: 30,

  // Snowflakes
  SNOWFLAKE_COUNT: 150,
  SNOWFLAKE_MIN_SIZE: 1,
  SNOWFLAKE_MAX_SIZE: 8,
  SNOWFLAKE_MIN_SPEED: 0.25,
  SNOWFLAKE_MAX_SPEED: 2.5,
  SNOWFLAKE_WIND: 0.8,
  SNOWFLAKE_OPACITY: 0.7,
}

// ===================== TYPES =====================
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

// ===================== REACTIVE STATE =====================
const canvasRef = ref<HTMLCanvasElement | null>(null)
const particles = ref<Particle[]>([])

// ===================== ANIMATION STATE =====================
let ctx: CanvasRenderingContext2D | null = null
let animationFrameId: number | null = null
let treeImage: HTMLImageElement | null = null

// ===================== UTILITY FUNCTIONS =====================
const random = (min: number, max: number): number => {
  return Math.random() * (max - min) + min
}

const getRGB = (hex: string): string => {
  if (hex.indexOf('#') === 0 && hex.length === 7) {
    const r = parseInt(hex.substring(1, 3), 16)
    const g = parseInt(hex.substring(3, 5), 16)
    const b = parseInt(hex.substring(5, 7), 16)
    return `${r},${g},${b}`
  }
  return '255,255,255'
}

// ===================== RESOURCE LOADING =====================
const loadTreeImage = (): Promise<HTMLImageElement> => {
  return new Promise((resolve, reject) => {
    const img = new Image()
    img.onload = () => resolve(img)
    img.onerror = reject
    img.src = '/christmas_tree.png'
  })
}

// ===================== PARTICLE FACTORIES =====================
const createTreeParticle = (canvasWidth: number, canvasHeight: number): TreeParticle => {
  const size = random(CONFIG.TREE_MIN_SIZE, CONFIG.TREE_MAX_SIZE)

  return {
    type: 'tree',
    x: random(-CONFIG.TREE_MAX_SIZE, canvasWidth),
    y: random(-CONFIG.TREE_MAX_SIZE, canvasHeight),
    r: size,
    velX: CONFIG.TREE_WIND * random(-0.5, 0.5),
    velY: random(CONFIG.TREE_MIN_SPEED, CONFIG.TREE_MAX_SPEED),
    swing: random(0, 2 * Math.PI),
    opacity: CONFIG.TREE_OPACITY * random(0.8, 1),
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

  ctx.save()
  ctx.translate(particle.x, particle.y)
  ctx.rotate(particle.rotation * Math.PI / 180)
  ctx.globalAlpha = particle.opacity

  const imageHeight = particle.r * 3
  const imageWidth = (treeImage.width / treeImage.height) * imageHeight
  ctx.drawImage(treeImage, -imageWidth / 2, -imageHeight / 2, imageWidth, imageHeight)

  ctx.restore()
}

const drawSnowflake = (particle: SnowflakeParticle) => {
  if (!ctx) return

  ctx.save()
  ctx.translate(particle.x, particle.y)
  ctx.rotate(particle.rotation * Math.PI / 180)
  ctx.globalAlpha = particle.opacity

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
      ctx.lineTo(
          Math.cos(sideAngle) * particle.r * 0.4 + Math.cos(angle) * pos,
          Math.sin(sideAngle) * particle.r * 0.4 + Math.sin(angle) * pos
      )
      ctx.stroke()

      ctx.beginPath()
      ctx.moveTo(Math.cos(angle) * pos, Math.sin(angle) * pos)
      ctx.lineTo(
          Math.cos(sideAngle2) * particle.r * 0.4 + Math.cos(angle) * pos,
          Math.sin(sideAngle2) * particle.r * 0.4 + Math.sin(angle) * pos
      )
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

// ===================== PARTICLE UPDATERS =====================
const updateTreeParticle = (particle: TreeParticle, deltaTime: number = 1) => {
  // Apply wind force
  particle.velX = Math.abs(particle.velX) < Math.abs(CONFIG.TREE_WIND)
      ? particle.velX + CONFIG.TREE_WIND / 20
      : CONFIG.TREE_WIND

  // Update position
  particle.x += particle.velX * 0.5 * deltaTime
  particle.y += particle.velY * 0.5 * deltaTime

  // Update swing rotation (pendulum motion)
  particle.rotation += particle.swingSpeed * particle.swingDirection * deltaTime

  // Reverse direction when reaching max angle
  if (Math.abs(particle.rotation) >= particle.swingAngle) {
    particle.swingDirection *= -1
    particle.rotation = Math.max(
        -particle.swingAngle,
        Math.min(particle.swingAngle, particle.rotation)
    )
  }

  // Add bounce effect at extremes
  if (Math.abs(particle.rotation) > particle.swingAngle * 0.8) {
    particle.swingSpeed *= 0.98
  } else {
    particle.swingSpeed = Math.min(
        particle.swingSpeed * 1.02,
        random(CONFIG.TREE_SWING_SPEED_MIN, CONFIG.TREE_SWING_SPEED_MAX)
    )
  }
}

const updateSnowflakeParticle = (particle: SnowflakeParticle, deltaTime: number = 1) => {
  // Apply wind force
  particle.velX = Math.abs(particle.velX) < Math.abs(CONFIG.SNOWFLAKE_WIND)
      ? particle.velX + CONFIG.SNOWFLAKE_WIND / 20
      : CONFIG.SNOWFLAKE_WIND

  // Update position with drift
  particle.x += (particle.velX * 0.5 + particle.drift) * deltaTime
  particle.y += particle.velY * 0.5 * deltaTime
  particle.rotation += particle.rotationSpeed * deltaTime
}

// ===================== PARTICLE RESET =====================
const resetParticle = (particle: Particle, width: number, height: number) => {
  const prevR = particle.r

  // Update size based on particle type
  if (particle.type === 'tree') {
    particle.r = random(CONFIG.TREE_MIN_SIZE, CONFIG.TREE_MAX_SIZE)
  } else {
    particle.r = random(CONFIG.SNOWFLAKE_MIN_SIZE, CONFIG.SNOWFLAKE_MAX_SIZE)
  }

  // Reset position based on exit direction
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

  // Reset properties based on particle type
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

// ===================== INITIALIZATION =====================
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

// ===================== ANIMATION LOOP =====================
let lastTime = 0
const animate = (timestamp: number) => {
  if (!canvasRef.value || !ctx) return

  const canvas = canvasRef.value
  const width = canvas.width
  const height = canvas.height

  // Calculate delta time for smooth animation
  const deltaTime = lastTime ? (timestamp - lastTime) / 16.67 : 1
  lastTime = timestamp

  // Clear canvas
  ctx.clearRect(0, 0, width, height)

  // Update and draw each particle
  particles.value.forEach(particle => {
    // Update particle based on type
    if (particle.type === 'tree') {
      updateTreeParticle(particle as TreeParticle, deltaTime)
    } else {
      updateSnowflakeParticle(particle as SnowflakeParticle, deltaTime)
    }

    // Check boundaries
    const margin = particle.r * 2
    if (
        particle.x > width + margin ||
        particle.x < -margin ||
        particle.y > height + margin ||
        particle.y < -margin
    ) {
      resetParticle(particle, width, height)
    }

    // Draw particle
    if (particle.type === 'tree') {
      drawTree(particle as TreeParticle)
    } else {
      drawSnowflake(particle as SnowflakeParticle)
    }
  })

  // Continue animation
  animationFrameId = requestAnimationFrame(animate)
}

// ===================== LIFECYCLE HOOKS =====================
onMounted(() => {
  loadTreeImage()
      .then(img => {
        treeImage = img
        initParticles()
        animationFrameId = requestAnimationFrame(animate)
      })
      .catch(err => {
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