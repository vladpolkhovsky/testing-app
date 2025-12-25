import type { TreeParticle, SnowflakeParticle, Particle, ParticleConfig } from './particleTypes'

export const random = (min: number, max: number): number => {
  return Math.random() * (max - min) + min
}

export const getRGB = (hex: string): string => {
  if (hex.indexOf('#') === 0 && hex.length === 7) {
    const r = parseInt(hex.substring(1, 3), 16)
    const g = parseInt(hex.substring(3, 5), 16)
    const b = parseInt(hex.substring(5, 7), 16)
    return `${ r },${ g },${ b }`
  }
  return '255,255,255'
}

const shuffleArray = <T>(array: T[]): T[] => {
  const shuffled = [...array]
  for (let i = shuffled.length - 1; i > 0; i--) {
    const j = Math.floor(Math.random() * (i + 1));
    [shuffled[i], shuffled[j]] = [shuffled[j]!, shuffled[i]!]
  }
  return shuffled
}

export const createGrid = (canvasWidth: number, canvasHeight: number, treeCount: number, treeMaxSize: number) => {
  const cellSize = Math.max(
      treeMaxSize * 3,
      Math.sqrt((canvasWidth * canvasHeight) / treeCount)
  )

  const grid: { x: number, y: number }[] = []

  const padding = treeMaxSize * 2
  const totalWidth = canvasWidth + padding * 6
  const totalHeight = canvasHeight + padding * 2

  const cols = Math.ceil(totalWidth / cellSize)
  const rows = Math.ceil(totalHeight / cellSize)

  for (let row = 0; row < rows; row++) {
    for (let col = 0; col < cols; col++) {
      grid.push({
        x: col * cellSize - padding,
        y: row * cellSize - padding
      })
    }
  }

  return shuffleArray(grid)
}

// Helper to get cell size based on canvas dimensions and tree count
const getCellSize = (canvasWidth: number, canvasHeight: number, treeCount: number, treeMaxSize: number): number => {
  return Math.max(
      treeMaxSize * 2.5,
      Math.sqrt((canvasWidth * canvasHeight) / treeCount)
  )
}

// Helper to get a random position within a grid cell
const getRandomPositionInCell = (cell: { x: number, y: number }, cellSize: number): { x: number, y: number } => {
  const maxOffset = cellSize * 0.3
  return {
    x: cell.x + random(-maxOffset, maxOffset),
    y: cell.y + random(-maxOffset, maxOffset)
  }
}

// Helper to find available grid cells
const getAvailableGridCells = (
    grid: { x: number, y: number }[],
    canvasWidth: number,
    canvasHeight: number,
    config: ParticleConfig,
    verticalConstraint?: 'top' | 'bottom' | 'none'
): { x: number, y: number }[] => {
  return grid.filter(cell => {
    const withinHorizontal = cell.x >= -config.TREE_MAX_SIZE &&
        cell.x <= canvasWidth + config.TREE_MAX_SIZE

    let withinVertical = true
    if (verticalConstraint === 'top') {
      withinVertical = cell.y >= -config.TREE_MAX_SIZE && cell.y <= config.TREE_MAX_SIZE
    } else if (verticalConstraint === 'bottom') {
      withinVertical = cell.y >= canvasHeight - config.TREE_MAX_SIZE &&
          cell.y <= canvasHeight + config.TREE_MAX_SIZE
    } else {
      withinVertical = cell.y >= -config.TREE_MAX_SIZE &&
          cell.y <= canvasHeight + config.TREE_MAX_SIZE
    }

    return withinHorizontal && withinVertical
  })
}

// Helper to create tree properties (reused for creation and resetting)
const createTreeProperties = (config: ParticleConfig) => {
  return {
    type: 'tree' as const,
    r: random(config.TREE_MIN_SIZE, config.TREE_MAX_SIZE),
    velX: config.TREE_WIND * random(-0.5, 0.5),
    velY: random(config.TREE_MIN_SPEED, config.TREE_MAX_SPEED),
    swing: random(0, 2 * Math.PI),
    opacity: config.TREE_OPACITY * random(0.8, 1),
    rotation: random(-config.TREE_SWING_ANGLE_MAX, config.TREE_SWING_ANGLE_MAX) ?? config.TREE_SWING_ANGLE_MAX,
    swingSpeed: random(config.TREE_SWING_SPEED_MIN, config.TREE_SWING_SPEED_MAX),
    swingAngle: random(15, config.TREE_SWING_ANGLE_MAX),
    swingDirection: Math.random() > 0.5 ? 1 : -1
  }
}

// Helper to create snowflake properties (reused for creation and resetting)
const createSnowflakeProperties = (config: ParticleConfig) => {
  return {
    type: 'snowflake' as const,
    r: random(config.SNOWFLAKE_MIN_SIZE, config.SNOWFLAKE_MAX_SIZE),
    velX: config.SNOWFLAKE_WIND * random(-0.3, 0.3),
    velY: random(config.SNOWFLAKE_MIN_SPEED, config.SNOWFLAKE_MAX_SPEED),
    swing: random(0, 2 * Math.PI),
    opacity: config.SNOWFLAKE_OPACITY * random(0.5, 1),
    drift: random(-0.2, 0.2),
    rotation: random(0, 360),
    rotationSpeed: random(-1, 1)
  }
}

export const createTreeParticle = (
    canvasWidth: number,
    canvasHeight: number,
    config: ParticleConfig,
    grid: { x: number, y: number }[] = [],
    verticalConstraint?: 'top' | 'bottom' | 'none'
): TreeParticle => {
  const baseProps = createTreeProperties(config)

  let x: number, y: number

  if (grid.length > 0) {
    const availableCells = getAvailableGridCells(grid, canvasWidth, canvasHeight, config, verticalConstraint)

    if (availableCells.length > 0) {
      const cell = availableCells[Math.floor(Math.random() * availableCells.length)]!
      const cellSize = getCellSize(canvasWidth, canvasHeight, config.TREE_COUNT, config.TREE_MAX_SIZE)
      const pos = getRandomPositionInCell(cell, cellSize)
      x = pos.x
      y = pos.y
    } else {
      // Fallback: random position with constraint
      if (verticalConstraint === 'top') {
        x = random(-config.TREE_MAX_SIZE, canvasWidth + config.TREE_MAX_SIZE)
        y = random(-config.TREE_MAX_SIZE, 0)
      } else {
        x = random(-config.TREE_MAX_SIZE, canvasWidth + config.TREE_MAX_SIZE)
        y = random(-config.TREE_MAX_SIZE, canvasHeight + config.TREE_MAX_SIZE)
      }
    }
  } else {
    // No grid available
    x = random(-config.TREE_MAX_SIZE, canvasWidth + config.TREE_MAX_SIZE)
    y = random(-config.TREE_MAX_SIZE, canvasHeight + config.TREE_MAX_SIZE)
  }

  return {
    ...baseProps,
    x,
    y
  }
}

export const createSnowflakeParticle = (
    canvasWidth: number,
    canvasHeight: number,
    config: ParticleConfig,
    spawnFromTop: boolean = false
): SnowflakeParticle => {
  const baseProps = createSnowflakeProperties(config)

  const x = random(0, canvasWidth)
  const y = spawnFromTop
      ? random(-config.SNOWFLAKE_MAX_SIZE, 0)
      : random(0, canvasHeight)

  return {
    ...baseProps,
    x,
    y
  }
}

export const updateTreeParticle = (
    particle: TreeParticle,
    config: ParticleConfig,
    deltaTime: number = 1
) => {
  particle.velX = Math.abs(particle.velX) < Math.abs(config.TREE_WIND)
      ? particle.velX + config.TREE_WIND / 20
      : config.TREE_WIND

  particle.x += particle.velX * 0.5 * deltaTime
  particle.y += particle.velY * 0.5 * deltaTime

  particle.rotation += particle.swingSpeed * particle.swingDirection * deltaTime

  if (Math.abs(particle.rotation) >= particle.swingAngle) {
    particle.swingDirection *= -1
    particle.rotation = Math.max(
        -particle.swingAngle,
        Math.min(particle.swingAngle, particle.rotation)
    )
  }

  if (Math.abs(particle.rotation) > particle.swingAngle * 0.8) {
    particle.swingSpeed *= 0.98
  } else {
    particle.swingSpeed = Math.min(
        particle.swingSpeed * 1.02,
        random(config.TREE_SWING_SPEED_MIN, config.TREE_SWING_SPEED_MAX)
    )
  }
}

export const updateSnowflakeParticle = (particle: SnowflakeParticle, config: ParticleConfig, deltaTime: number = 1) => {
  particle.velX = Math.abs(particle.velX) < Math.abs(config.SNOWFLAKE_WIND)
      ? particle.velX + config.SNOWFLAKE_WIND / 20
      : config.SNOWFLAKE_WIND

  particle.x += (particle.velX * 0.5 + particle.drift) * deltaTime
  particle.y += particle.velY * 0.5 * deltaTime
  particle.rotation += particle.rotationSpeed * deltaTime
}

// Check if a particle is out of bounds
export const isParticleOutOfBounds = (
    particle: Particle,
    width: number,
    height: number,
    margin: number = 0,
    checkTreeHeight: boolean = false
): boolean => {
  const marginX = particle.r + margin
  const marginY = particle.r + margin

  // Check horizontal bounds
  const outOfHorizontal = particle.x > width + marginX || particle.x < -marginX

  // For trees, check if the entire tree (including height) is out of bounds
  if (particle.type === 'tree' && checkTreeHeight) {
    // Trees are drawn with height = particle.r * 3 from particle.y upward
    const treeHeight = particle.r * 3
    const treeTop = particle.y - treeHeight

    // Tree is out if its top is below the bottom, OR if bottom is above the top
    const outOfVertical = treeTop > height + marginY || particle.y < -marginY
    return outOfHorizontal || outOfVertical
  }

  // For snowflakes, use simple bounds check
  const outOfVertical = particle.y > height + marginY || particle.y < -marginY
  return outOfHorizontal || outOfVertical
}

export const resetParticle = (
    particle: Particle,
    width: number,
    height: number,
    config: ParticleConfig,
    grid?: { x: number, y: number }[]
) => {
  // Reset position based on particle type
  if (particle.type === 'tree') {
    // For trees, always spawn from top using grid
    const newTree = createTreeParticle(width, height, config, grid, 'top')
    Object.assign(particle, newTree)
  } else {
    // For snowflakes, spawn from top
    const newSnowflake = createSnowflakeParticle(width, height, config, true)
    Object.assign(particle, newSnowflake)
  }
}

export const drawTree = (ctx: CanvasRenderingContext2D, particle: TreeParticle, treeImage: HTMLImageElement) => {
  ctx.save()
  ctx.translate(particle.x, particle.y)
  ctx.rotate(particle.rotation * Math.PI / 180)
  ctx.globalAlpha = particle.opacity

  const imageHeight = particle.r * 3
  const imageWidth = (treeImage.width / treeImage.height) * imageHeight
  ctx.drawImage(treeImage, -imageWidth / 2, -imageHeight, imageWidth, imageHeight)

  ctx.restore()
}

export const drawSnowflake = (ctx: CanvasRenderingContext2D, particle: SnowflakeParticle) => {
  ctx.save()
  ctx.translate(particle.x, particle.y)
  ctx.rotate(particle.rotation * Math.PI / 180)
  ctx.globalAlpha = particle.opacity

  const branches = 6
  for (let i = 0; i < branches; i++) {
    const angle = (i * 360 / branches) * Math.PI / 180

    ctx.beginPath()
    ctx.moveTo(0, 0)
    ctx.lineTo(Math.cos(angle) * particle.r * 1.5, Math.sin(angle) * particle.r * 1.5)
    ctx.strokeStyle = `rgba(${ getRGB('#FFFFFF') },${ particle.opacity })`
    ctx.lineWidth = 1
    ctx.stroke()

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

  ctx.beginPath()
  ctx.arc(0, 0, particle.r * 0.3, 0, Math.PI * 2)
  ctx.fillStyle = `rgba(${ getRGB('#FFFFFF') },${ particle.opacity })`
  ctx.fill()

  ctx.restore()
}