package com.kneelawk.magicalmahou.component

import com.kneelawk.magicalmahou.MMConstants.id
import dev.onyxstudios.cca.api.v3.component.ComponentKey
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy

object MMComponents {
    val GENERAL: ComponentKey<MagicalMahouComponent> =
        ComponentRegistry.getOrCreate(id("general"), MagicalMahouComponent::class.java)
    val CAT_EARS: ComponentKey<CatEarsComponent> =
        ComponentRegistry.getOrCreate(id("cat_ears"), CatEarsComponent::class.java)
    val TELEPORT_AT: ComponentKey<TeleportAtComponent> =
        ComponentRegistry.getOrCreate(id("teleport_at"), TeleportAtComponent::class.java)

    fun registerEntityComponentFactories(registry: EntityComponentFactoryRegistry) {
        // I'm doing ALWAYS_COPY here but I may change it to CHARACTER if I ever implement abilities for SkinManagers to
        // have multiple skins per player.
        registry.registerForPlayers(GENERAL, ::MagicalMahouComponent, RespawnCopyStrategy.ALWAYS_COPY)
        registry.registerForPlayers(CAT_EARS, ::CatEarsComponent, RespawnCopyStrategy.ALWAYS_COPY)
        registry.registerForPlayers(TELEPORT_AT, ::TeleportAtComponent, RespawnCopyStrategy.ALWAYS_COPY)
    }

    private val ABILITY_COMPONENTS = mutableListOf<ComponentKey<out MMAbilityComponent<*>>>()

    fun getAbilityComponents(): List<ComponentKey<out MMAbilityComponent<*>>> = ABILITY_COMPONENTS

    fun registerAbilityComponent(key: ComponentKey<out MMAbilityComponent<*>>) {
        ABILITY_COMPONENTS.add(key)
    }

    fun mmInit() {
        registerAbilityComponent(CAT_EARS)
        registerAbilityComponent(TELEPORT_AT)
    }
}
