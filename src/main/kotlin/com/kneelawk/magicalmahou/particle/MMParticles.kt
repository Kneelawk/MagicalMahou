package com.kneelawk.magicalmahou.particle

import com.kneelawk.magicalmahou.MMConstants.id
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes
import net.minecraft.particle.DefaultParticleType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry

object MMParticles {
    lateinit var TRANSFORMATION: DefaultParticleType

    fun init() {
        TRANSFORMATION = FabricParticleTypes.simple()

        Registry.register(Registries.PARTICLE_TYPE, id("transformation"), TRANSFORMATION)
    }
}
