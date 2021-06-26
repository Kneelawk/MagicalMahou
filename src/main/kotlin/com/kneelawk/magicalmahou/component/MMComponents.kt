package com.kneelawk.magicalmahou.component

import com.kneelawk.magicalmahou.MMConstants.id
import dev.onyxstudios.cca.api.v3.component.ComponentKey
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy

object MMComponents {
    val GENERAL: ComponentKey<MagicalMahouComponent> =
        ComponentRegistry.getOrCreate(id("general"), MagicalMahouComponent::class.java)

    fun registerEntityComponentFactories(registry: EntityComponentFactoryRegistry) {
        // I'm doing ALWAYS_COPY here but I may change it to CHARACTER if I ever implement abilities for SkinManagers to
        // have multiple skins per player.
        registry.registerForPlayers(GENERAL, ::MagicalMahouComponent, RespawnCopyStrategy.ALWAYS_COPY)
    }
}