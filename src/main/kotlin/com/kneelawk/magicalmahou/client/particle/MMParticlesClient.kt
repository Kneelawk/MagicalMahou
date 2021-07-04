package com.kneelawk.magicalmahou.client.particle

import com.kneelawk.magicalmahou.particle.MMParticles
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry
import net.minecraft.client.MinecraftClient
import net.minecraft.client.particle.Particle
import net.minecraft.entity.Entity
import java.util.*
import kotlin.math.sqrt

object MMParticlesClient {
    fun init() {
        ParticleFactoryRegistry.getInstance().register(MMParticles.TRANSFORMATION, TransformationParticle::Factory)
    }

    private fun addTransformationParticle(
        x: Double, y: Double, z: Double, velocityX: Double, velocityY: Double, velocityZ: Double, color: Int
    ): Particle {
        val particle = MinecraftClient.getInstance().particleManager.addParticle(
            MMParticles.TRANSFORMATION, x, y, z, velocityX, velocityY, velocityZ
        )

        if (particle !is TransformationParticle) {
            throw IllegalStateException("Expected TransformationParticle but got $particle")
        }

        particle.setColor(color)

        return particle
    }

    fun addTransformationParticles(
        entity: Entity, size: Double, amount: Int, color: Int
    ) {
        val random = Random()
        val x = entity.x
        val z = entity.z

        for (particleY in -amount..amount) {
            for (particleX in -amount..amount) {
                var particleZ: Int = -amount

                while (particleZ <= amount) {
                    val y = entity.getBodyY((particleY.toDouble() / amount.toDouble() + 1.0) * 0.25 + 0.25)

                    val velX: Double = particleX.toDouble() + (random.nextDouble() - random.nextDouble()) * 0.5
                    val velY: Double = particleY.toDouble() + (random.nextDouble() - random.nextDouble()) * 0.5
                    val velZ: Double = particleZ.toDouble() + (random.nextDouble() - random.nextDouble()) * 0.5
                    val velLen: Double =
                        sqrt(velX * velX + velY * velY + velZ * velZ) / size + random.nextGaussian() * 0.05

                    addTransformationParticle(x, y, z, velX / velLen, velY / velLen, velZ / velLen, color)

                    if (particleY != -amount && particleY != amount && particleX != -amount && particleX != amount) {
                        particleZ += amount * 2 - 1
                    }

                    ++particleZ
                }
            }
        }
    }
}