package com.kneelawk.magicalmahou.client

import com.kneelawk.magicalmahou.MMConstants.id
import com.kneelawk.magicalmahou.client.render.player.CatEarsFeatureRenderer
import com.kneelawk.magicalmahou.mixin.api.PlayerEntityRendererEvents
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback
import net.minecraft.client.texture.SpriteAtlasTexture

fun init() {
    PlayerEntityRendererEvents.ADD_FEATURES.register { renderer, _, consumer ->
        println("Adding cat ears...")
        consumer.accept(CatEarsFeatureRenderer(renderer))
    }

    ClientSpriteRegistryCallback.event(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).register { atlasTexture, registry ->
        registry.register(id("misc/cat_ears"))
    }
}
