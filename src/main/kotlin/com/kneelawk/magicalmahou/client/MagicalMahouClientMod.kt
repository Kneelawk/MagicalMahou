package com.kneelawk.magicalmahou.client

import com.kneelawk.magicalmahou.MMLog
import com.kneelawk.magicalmahou.block.MMBlocksClient
import com.kneelawk.magicalmahou.client.skin.ClientSkinManager
import com.kneelawk.magicalmahou.client.skin.ClientSkinManagers
import com.kneelawk.magicalmahou.client.render.player.CatEarsFeatureRenderer
import com.kneelawk.magicalmahou.client.screen.MMScreens
import com.kneelawk.magicalmahou.component.MMComponents
import com.kneelawk.magicalmahou.skin.SkinManagers
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

    MMBlocksClient.init()
    MMKeys.register()
    MMScreens.init()
}
