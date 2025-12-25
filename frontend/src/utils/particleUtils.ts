import type { TreeParticle, SnowflakeParticle, Particle, ParticleConfig } from './particleTypes'

export const random = (min: number, max: number): number => {
  return Math.random() * (max - min) + min
}

export const getRGB = (hex: string): string => {
  if (hex.indexOf('#') === 0 && hex.length === 7) {
    const r = parseInt(hex.substring(1, 3), 16)
    const g = parseInt(hex.substring(3, 5), 16)
    const b = parseInt(hex.substring(5, 7), 16)
    return `${r},${g},${b}`
  }
  return '255,255,255'
}

export const createTreeParticle = (canvasWidth: number, canvasHeight: number, config: ParticleConfig): TreeParticle => {
  const size = random(config.TREE_MIN_SIZE, config.TREE_MAX_SIZE)

  return {
    type: 'tree',
    x: random(-config.TREE_MAX_SIZE, canvasWidth),
    y: random(-config.TREE_MAX_SIZE, canvasHeight),
    r: size,
    velX: config.TREE_WIND * random(-0.5, 0.5),
    velY: random(config.TREE_MIN_SPEED, config.TREE_MAX_SPEED),
    swing: random(0, 2 * Math.PI),
    opacity: config.TREE_OPACITY * random(0.8, 1),
    rotation: random(-config.TREE_SWING_ANGLE_MAX, config.TREE_SWING_ANGLE_MAX),
    swingSpeed: random(config.TREE_SWING_SPEED_MIN, config.TREE_SWING_SPEED_MAX),
    swingAngle: random(15, config.TREE_SWING_ANGLE_MAX),
    swingDirection: Math.random() > 0.5 ? 1 : -1
  }
}

export const createSnowflakeParticle = (canvasWidth: number, canvasHeight: number, config: ParticleConfig): SnowflakeParticle => {
  return {
    type: 'snowflake',
    x: random(0, canvasWidth),
    y: random(0, canvasHeight),
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

export const updateTreeParticle = (particle: TreeParticle, config: ParticleConfig, deltaTime: number = 1) => {
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

export const resetParticle = (particle: Particle, width: number, height: number, config: ParticleConfig) => {
  const prevR = particle.r

  if (particle.type === 'tree') {
    particle.r = random(config.TREE_MIN_SIZE, config.TREE_MAX_SIZE)
  } else {
    particle.r = random(config.SNOWFLAKE_MIN_SIZE, config.SNOWFLAKE_MAX_SIZE)
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
    treeParticle.velX = config.TREE_WIND * random(-0.5, 0.5)
    treeParticle.velY = random(config.TREE_MIN_SPEED, config.TREE_MAX_SPEED)
    treeParticle.swing = random(0, 2 * Math.PI)
    treeParticle.opacity = config.TREE_OPACITY * random(0.8, 1)
    treeParticle.rotation = random(-config.TREE_SWING_ANGLE_MAX, config.TREE_SWING_ANGLE_MAX)
    treeParticle.swingSpeed = random(config.TREE_SWING_SPEED_MIN, config.TREE_SWING_SPEED_MAX)
    treeParticle.swingAngle = random(15, config.TREE_SWING_ANGLE_MAX)
    treeParticle.swingDirection = Math.random() > 0.5 ? 1 : -1
  } else {
    const snowflakeParticle = particle as SnowflakeParticle
    snowflakeParticle.velX = config.SNOWFLAKE_WIND * random(-0.3, 0.3)
    snowflakeParticle.velY = random(config.SNOWFLAKE_MIN_SPEED, config.SNOWFLAKE_MAX_SPEED)
    snowflakeParticle.swing = random(0, 2 * Math.PI)
    snowflakeParticle.opacity = config.SNOWFLAKE_OPACITY * random(0.5, 1)
    snowflakeParticle.rotationSpeed = random(-1, 1)
    snowflakeParticle.drift = random(-0.2, 0.2)
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
    ctx.strokeStyle = `rgba(${getRGB('#FFFFFF')},${particle.opacity})`
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
  ctx.fillStyle = `rgba(${getRGB('#FFFFFF')},${particle.opacity})`
  ctx.fill()

  ctx.restore()
}
