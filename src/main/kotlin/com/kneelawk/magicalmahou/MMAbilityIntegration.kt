package com.kneelawk.magicalmahou

import com.kneelawk.magicalmahou.component.MMComponents
import com.kneelawk.magicalmahou.mixin.api.PlayerEntityEvents

object MMAbilityIntegration {
    fun init() {
        PlayerEntityEvents.IS_INVULNERABLE_TO.register { player, source ->
            val component = MMComponents.LONG_FALL[player]
            source.isFromFalling && component.isActuallyEnabled()
        }
    }
}