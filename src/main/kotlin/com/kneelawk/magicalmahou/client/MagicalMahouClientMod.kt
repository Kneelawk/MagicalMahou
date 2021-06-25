package com.kneelawk.magicalmahou.client

import com.kneelawk.magicalmahou.MMLog
import com.kneelawk.magicalmahou.client.image.ClientImageWrapperFactory
import com.kneelawk.magicalmahou.client.render.player.CatEarsFeatureRenderer
import com.kneelawk.magicalmahou.image.MMImageUtils
import com.kneelawk.magicalmahou.mixin.api.PlayerEntityRendererEvents

fun init() {
    PlayerEntityRendererEvents.ADD_FEATURES.register { renderer, _, _, consumer ->
        MMLog.info("Adding cat ears...")
        consumer.accept(CatEarsFeatureRenderer(renderer))
    }

    // Setup image wrapper factory
    MMImageUtils.IMAGE_FACTORY = ClientImageWrapperFactory

    MMKeys.register()
}
