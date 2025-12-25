export interface BaseParticle {
  x: number
  y: number
  r: number
  velX: number
  velY: number
  swing: number
  opacity: number
}

export interface TreeParticle extends BaseParticle {
  type: 'tree'
  rotation: number
  swingSpeed: number
  swingAngle: number
  swingDirection: number
}

export interface SnowflakeParticle extends BaseParticle {
  type: 'snowflake'
  drift: number
  rotation: number
  rotationSpeed: number
}

export type Particle = TreeParticle | SnowflakeParticle

export interface ParticleConfig {
  TREE_COUNT: number
  TREE_MIN_SIZE: number
  TREE_MAX_SIZE: number
  TREE_MIN_SPEED: number
  TREE_MAX_SPEED: number
  TREE_WIND: number
  TREE_OPACITY: number
  TREE_SWING_SPEED_MIN: number
  TREE_SWING_SPEED_MAX: number
  TREE_SWING_ANGLE_MAX: number
  SNOWFLAKE_COUNT: number
  SNOWFLAKE_MIN_SIZE: number
  SNOWFLAKE_MAX_SIZE: number
  SNOWFLAKE_MIN_SPEED: number
  SNOWFLAKE_MAX_SPEED: number
  SNOWFLAKE_WIND: number
  SNOWFLAKE_OPACITY: number
}

export const DEFAULT_CONFIG: ParticleConfig = {
  TREE_COUNT: 10,
  TREE_MIN_SIZE: 60,
  TREE_MAX_SIZE: 80,
  TREE_MIN_SPEED: 1.5,
  TREE_MAX_SPEED: 2,
  TREE_WIND: 0.3,
  TREE_OPACITY: 0.9,
  TREE_SWING_SPEED_MIN: 0.15,
  TREE_SWING_SPEED_MAX: 0.5,
  TREE_SWING_ANGLE_MAX: 30,
  SNOWFLAKE_COUNT: 150,
  SNOWFLAKE_MIN_SIZE: 1,
  SNOWFLAKE_MAX_SIZE: 8,
  SNOWFLAKE_MIN_SPEED: 0.25,
  SNOWFLAKE_MAX_SPEED: 2.5,
  SNOWFLAKE_WIND: 0.8,
  SNOWFLAKE_OPACITY: 0.7,
}
