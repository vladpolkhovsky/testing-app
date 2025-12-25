import { ref, onUnmounted, onMounted } from 'vue'
import type { Ref } from 'vue'
import type { Particle, ParticleConfig } from '@/utils/particleTypes'
import { DEFAULT_CONFIG } from '@/utils/particleTypes'
import {
  createGrid,
  createTreeParticle,
  createSnowflakeParticle,
  updateTreeParticle,
  updateSnowflakeParticle,
  resetParticle,
  drawTree,
  drawSnowflake,
  isParticleOutOfBounds
} from '@/utils/particleUtils'

interface UseParticleAnimationOptions {
  config?: Partial<ParticleConfig>
  treeImagePath?: string
}

export const useParticleAnimation = (options: UseParticleAnimationOptions = {}) => {
  const config: ParticleConfig = {...DEFAULT_CONFIG, ...options.config}
  const treeImagePath = options.treeImagePath || '/christmas_tree.png'

  const canvasRef = ref<HTMLCanvasElement | null>(null)
  const particles: Ref<Particle[]> = ref([])

  let ctx: CanvasRenderingContext2D | null = null
  let animationFrameId: number | null = null
  let treeImage: HTMLImageElement | null = null
  let lastTime = 0
  let treeGrid: { x: number, y: number }[] = []

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
    treeGrid = createGrid(width, height, config.TREE_COUNT, config.TREE_MAX_SIZE)

    const treeParticles: Particle[] = []
    for (let i = 0; i < config.TREE_COUNT; i++) {
      const tree = createTreeParticle(width, height, config, treeGrid)
      treeParticles.push(tree)
    }

    const snowflakeParticles: Particle[] = []
    for (let i = 0; i < config.SNOWFLAKE_COUNT; i++) {
      const snowflake = createSnowflakeParticle(width, height, config)
      snowflakeParticles.push(snowflake)
    }

    particles.value = [...treeParticles, ...snowflakeParticles]
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

    const {width, height} = canvas.getBoundingClientRect()
    canvas.width = width
    canvas.height = height

    initParticles(width, height)
  }

  const animate = (timestamp: number) => {
    if (!canvasRef.value || !ctx) return

    const canvas = canvasRef.value
    const width = canvas.width
    const height = canvas.height

    const deltaTime = lastTime ? (timestamp - lastTime) / 12 : 1
    lastTime = timestamp

    ctx.clearRect(0, 0, width, height)

    particles.value.forEach(particle => {
      if (particle.type === 'tree') {
        updateTreeParticle(particle, config, deltaTime)
      } else {
        updateSnowflakeParticle(particle, config, deltaTime)
      }

      // Check bounds and reset if needed
      // For trees, check with tree height consideration
      const isOutOfBounds = isParticleOutOfBounds(
          particle,
          width,
          height,
          particle.r,
          particle.type === 'tree'
      )

      if (isOutOfBounds) {
        resetParticle(particle, width, height, config,
            particle.type === 'tree' ? treeGrid : undefined)
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

    stop()

    const canvas = canvasRef.value
    const {width, height} = canvas.getBoundingClientRect()
    canvas.width = width
    canvas.height = height

    treeGrid = createGrid(width, height, config.TREE_COUNT, config.TREE_MAX_SIZE)

    initParticles(width, height)

    if (treeImage) {
      animationFrameId = requestAnimationFrame(animate)
    }
  }

  onMounted(() => {
    window.addEventListener('resize', handleResize)
  })

  onUnmounted(() => {
    stop()
    window.removeEventListener('resize', handleResize)
  })

  return {
    canvasRef,
    particles,
    start,
    stop,
    handleResize,
    config
  }
}
