package com.kneelawk.magicalmahou.particle

import com.kneelawk.magicalmahou.MMConstants.id
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes
import net.minecraft.particle.DefaultParticleType
import net.minecraft.util.registry.Registry

object MMParticles {
    lateinit var TRANSFORMATION: DefaultParticleType

    fun init() {
        TRANSFORMATION = FabricParticleTypes.simple()

        Registry.register(Registry.PARTICLE_TYPE, id("transformation"), TRANSFORMATION)
    }
}