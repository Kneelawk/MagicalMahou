package com.kneelawk.magicalmahou.client.render.player

import com.kneelawk.magicalmahou.MMLog
import com.kneelawk.magicalmahou.mixin.api.PlayerEntityRendererEvents

object MMFeatureRenderers {
    fun init() {
        PlayerEntityRendererEvents.ADD_FEATURES.register { renderer, _, _, consumer ->
            MMLog.info("Adding cat ears...")
            consumer.accept(CatEarsFeatureRenderer(renderer))
            consumer.accept(TeleportAtFeatureRenderer(renderer))
            consumer.accept(LongFallFeatureRenderer(renderer))
        }
    }
}
