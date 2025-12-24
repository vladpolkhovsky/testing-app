import { ref } from 'vue'
import type { Ref } from 'vue'
import type { Particle, ParticleConfig } from '@/utils/particleTypes'
import { DEFAULT_CONFIG } from '@/utils/particleTypes'
import {
  createTreeParticle,
  createSnowflakeParticle,
  updateTreeParticle,
  updateSnowflakeParticle,
  resetParticle,
  drawTree,
  drawSnowflake
} from '@/utils/particleUtils'

interface UseParticleAnimationOptions {
  config?: Partial<ParticleConfig>
  treeImagePath?: string
}

export const useParticleAnimation = (options: UseParticleAnimationOptions = {}) => {
  const config: ParticleConfig = { ...DEFAULT_CONFIG, ...options.config }
  const treeImagePath = options.treeImagePath || '/christmas_tree.png'

  const canvasRef = ref<HTMLCanvasElement | null>(null)
  const particles: Ref<Particle[]> = ref([])

  let ctx: CanvasRenderingContext2D | null = null
  let animationFrameId: number | null = null
  let treeImage: HTMLImageElement | null = null
  let lastTime = 0

  const loadTreeImage = (): Promise<HTMLImageElement> => {
    return new Promise((resolve, reject) => {
      const img = new Image()
      img.onload = () => resolve(img)
      img.onerror = reject
      img.src = treeImagePath
    })
  }

  const initParticles = (width: number, height: number) => {
    particles.value = []

    for (let i = 0; i < config.TREE_COUNT; i++) {
      particles.value.push(createTreeParticle(width, height, config))
    }

    for (let i = 0; i < config.SNOWFLAKE_COUNT; i++) {
      particles.value.push(createSnowflakeParticle(width, height, config))
    }
  }

  const setupCanvas = () => {
    if (!canvasRef.value) {
      console.error('Canvas ref not available')
      return
    }

    const canvas = canvasRef.value
    ctx = canvas.getContext('2d')
    if (!ctx) {
      console.error('Failed to get canvas context')
      return
    }

    const { width, height } = canvas.getBoundingClientRect()
    console.log('Canvas size:', width, height)
    canvas.width = width
    canvas.height = height

    initParticles(width, height)
  }

  const animate = (timestamp: number) => {
    if (!canvasRef.value || !ctx) return

    const canvas = canvasRef.value
    const width = canvas.width
    const height = canvas.height

    const deltaTime = lastTime ? (timestamp - lastTime) / 16.67 : 1
    lastTime = timestamp

    ctx.clearRect(0, 0, width, height)

    particles.value.forEach(particle => {
      if (particle.type === 'tree') {
        updateTreeParticle(particle, config, deltaTime)
      } else {
        updateSnowflakeParticle(particle, config, deltaTime)
      }

      const margin = particle.r * 2
      if (
        particle.x > width + margin ||
        particle.x < -margin ||
        particle.y > height + margin ||
        particle.y < -margin
      ) {
        resetParticle(particle, width, height, config)
      }

      if (particle.type === 'tree' && treeImage && ctx) {
        drawTree(ctx, particle, treeImage)
      } else if (particle.type === 'snowflake' && ctx) {
        drawSnowflake(ctx, particle)
      }
    })

    animationFrameId = requestAnimationFrame(animate)
  }

  const start = () => {
    loadTreeImage()
      .then(img => {
        treeImage = img
        setupCanvas()
        animationFrameId = requestAnimationFrame(animate)
      })
      .catch(err => {
        console.error('Failed to load tree image:', err)
      })
  }

  const stop = () => {
    if (animationFrameId) {
      cancelAnimationFrame(animationFrameId)
      animationFrameId = null
    }
  }

  const handleResize = () => {
    if (!canvasRef.value) return
    const canvas = canvasRef.value
    const { width, height } = canvas.getBoundingClientRect()
    canvas.width = width
    canvas.height = height

    particles.value.forEach(particle => {
      particle.x = (particle.x / (canvas.width || 1)) * width
      particle.y = (particle.y / (canvas.height || 1)) * height
    })
  }

  return {
    canvasRef,
    particles,
    start,
    stop,
    handleResize,
    config
  }
}
