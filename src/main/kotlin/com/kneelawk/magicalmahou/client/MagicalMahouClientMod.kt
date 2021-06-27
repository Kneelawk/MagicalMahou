package com.kneelawk.magicalmahou.client

import com.kneelawk.magicalmahou.MMLog
import com.kneelawk.magicalmahou.client.image.ClientSkinManager
import com.kneelawk.magicalmahou.client.image.ClientSkinManagers
import com.kneelawk.magicalmahou.client.render.player.CatEarsFeatureRenderer
import com.kneelawk.magicalmahou.component.MMComponents
import com.kneelawk.magicalmahou.image.SkinManagers
import com.kneelawk.magicalmahou.mixin.api.PlayerEntityRendererEvents
import com.kneelawk.magicalmahou.proxy.ClientProxy
import com.kneelawk.magicalmahou.proxy.MMProxy

fun init() {
    MMProxy.init(ClientProxy)

    MMClientSettings.init()

    ClientSkinManagers.init()

    PlayerEntityRendererEvents.ADD_FEATURES.register { renderer, _, _, consumer ->
        MMLog.info("Adding cat ears...")
        consumer.accept(CatEarsFeatureRenderer(renderer))
    }

    PlayerEntityRendererEvents.GET_SKIN_TEXTURE.register { player ->
        val component = MMComponents.GENERAL[player]

        if (component.isTransformed) {
            val clientSkinManager = SkinManagers.getPlayerSkinManger(player.world) as ClientSkinManager
            clientSkinManager.getIdentifier(player.uuid)
        } else {
            null
        }
    }

    PlayerEntityRendererEvents.GET_SKIN_MODEL.register { player ->
        val component = MMComponents.GENERAL[player]

        if (component.isTransformed) {
            component.playerSkinModel.modelStr
        } else {
            null
        }
    }

    MMKeys.register()
}
