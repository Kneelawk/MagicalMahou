package com.kneelawk.magicalmahou.component

import dev.onyxstudios.cca.api.v3.component.Component
import dev.onyxstudios.cca.api.v3.component.ComponentKey
import dev.onyxstudios.cca.api.v3.entity.PlayerComponent
import net.minecraft.entity.player.PlayerEntity

interface ProvidingPlayerComponent<C : Component> : PlayerComponent<C> {
    val provider: PlayerEntity

    val key: ComponentKey<C>
}