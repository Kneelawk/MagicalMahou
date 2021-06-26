package com.kneelawk.magicalmahou.client

import com.kneelawk.magicalmahou.MMConstants
import com.kneelawk.magicalmahou.MMLog
import com.kneelawk.magicalmahou.client.image.ClientSkinManager
import com.kneelawk.magicalmahou.client.render.player.CatEarsFeatureRenderer
import com.kneelawk.magicalmahou.image.SkinManagerHolder
import com.kneelawk.magicalmahou.image.SkinManagers
import com.kneelawk.magicalmahou.mixin.api.PlayerEntityRendererEvents
import com.kneelawk.magicalmahou.server.image.ServerSkinManager

fun init() {
    PlayerEntityRendererEvents.ADD_FEATURES.register { renderer, _, _, consumer ->
        MMLog.info("Adding cat ears...")
        consumer.accept(CatEarsFeatureRenderer(renderer))
    }

    // Setup image wrapper factory
    SkinManagers.init(
        SkinManagerHolder(ClientSkinManager(64, 64, MMConstants.PLAYER_SKIN_PATH, true), ServerSkinManager(64, 64))
    )

    MMKeys.register()
}
