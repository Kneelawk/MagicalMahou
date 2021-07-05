package com.kneelawk.magicalmahou

import com.kneelawk.magicalmahou.MMConstants.tt
import com.kneelawk.magicalmahou.component.MMComponents
import com.kneelawk.magicalmahou.mixin.api.PlayerEntityEvents
import net.minecraft.entity.passive.CatEntity

object MMAbilityIntegration {
    fun init() {
        PlayerEntityEvents.IS_INVULNERABLE_TO.register { player, source ->
            val component = MMComponents.LONG_FALL[player]
            source.isFromFalling && component.isActuallyEnabled()
        }

        PlayerEntityEvents.TAME_ENTITY.register { player, tamed ->
            if (tamed is CatEntity) {
                val component = MMComponents.CAT_EARS[player]
                if (MMComponents.isMagical(player) && !component.getPlayerHasComponent()) {
                    component.serverGiveAbility()
                    player.sendMessage(tt("message", "accept.cat_ears"), false)
                }
            }
        }
    }
}