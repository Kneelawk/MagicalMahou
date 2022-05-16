package com.kneelawk.magicalmahou.client.particle

import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteProvider
import net.minecraft.client.particle.AnimatedParticle
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleFactory
import net.minecraft.client.world.ClientWorld
import net.minecraft.particle.DefaultParticleType

class TransformationParticle(
    world: ClientWorld, x: Double, y: Double, z: Double, velocityX: Double, velocityY: Double, velocityZ: Double,
    spriteProvider: FabricSpriteProvider
) : AnimatedParticle(world, x, y, z, spriteProvider, 0f) {

    init {
        maxAge = 8
        scale = 0.6f
        setVelocity(velocityX, velocityY, velocityZ)
        setSpriteForAge(spriteProvider)
    }

    class Factory(private val spriteProvider: FabricSpriteProvider) : ParticleFactory<DefaultParticleType> {
        override fun createParticle(
            parameters: DefaultParticleType, world: ClientWorld, x: Double, y: Double, z: Double, velocityX: Double,
            velocityY: Double, velocityZ: Double
        ): Particle {
            return TransformationParticle(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider)
        }
    }
}