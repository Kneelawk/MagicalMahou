package com.kneelawk.magicalmahou.client.skin

import com.kneelawk.magicalmahou.component.MMComponents
import com.kneelawk.magicalmahou.mixin.api.PlayerEntityRendererEvents
import com.kneelawk.magicalmahou.skin.SkinManagers

object MMPlayerSkinRenderer {
    fun init() {
        PlayerEntityRendererEvents.GET_SKIN_TEXTURE.register { player ->
            val component = MMComponents.GENERAL[player]

            if (component.isActuallyTransformed()) {
                val clientSkinManager = SkinManagers.getPlayerSkinManger(player.world) as ClientSkinManager
                clientSkinManager.getIdentifier(player.uuid)
            } else {
                null
            }
        }

        PlayerEntityRendererEvents.GET_SKIN_MODEL.register { player ->
            val component = MMComponents.GENERAL[player]

            if (component.isActuallyTransformed()) {
                component.playerSkinModel.modelStr
            } else {
                null
            }
        }
    }
}