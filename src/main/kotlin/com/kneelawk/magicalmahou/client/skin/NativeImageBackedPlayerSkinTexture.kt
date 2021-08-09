package com.kneelawk.magicalmahou.client.skin

import com.kneelawk.magicalmahou.MMLog
import com.kneelawk.magicalmahou.mixin.impl.PlayerSkinTextureAccessor
import com.mojang.blaze3d.platform.TextureUtil
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.texture.NativeImage
import net.minecraft.client.texture.PlayerSkinTexture
import net.minecraft.client.util.DefaultSkinHelper
import net.minecraft.resource.ResourceManager
import java.io.ByteArrayInputStream
import java.util.*

class NativeImageBackedPlayerSkinTexture(image: NativeImage, playerUUID: UUID) : PlayerSkinTexture(
    null, "http://skins.minecraft.net/MinecraftSkins/magical-mahou.png", DefaultSkinHelper.getTexture(playerUUID),
    false, null
) {

    var image: NativeImage? = image
        set(value) {
            field?.close()
            field = value

            // Ears compat
            updateEarsTexture()
        }

    init {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall {
                upload()
            }
        } else {
            upload()
        }

        // Ears compat
        updateEarsTexture()
    }

    /**
     * This calls the PlayerSkinTexture's `loadTexture` function that Ears uses to initialize its texture.
     */
    private fun updateEarsTexture() {
        val image = image
        if (image != null) {
            MMLog.info("Enabling Ears compatibility (will do nothing if Ears is not installed)")
            val bais = ByteArrayInputStream(image.bytes)
            (this as PlayerSkinTextureAccessor).callLoadTexture(bais)
        }
    }

    override fun load(manager: ResourceManager?) {
        // Don't do any actual loading.
    }

    fun upload() {
        val image = image
        if (image != null) {
            TextureUtil.prepareImage(getGlId(), image.width, image.height)
            bindTexture()
            image.upload(0, 0, 0, false)
        } else {
            MMLog.warn("Trying to upload disposed texture {}", getGlId())
        }
    }

    override fun close() {
        val image = image
        if (image != null) {
            image.close()
            clearGlId()
            this.image = null
        }
    }
}