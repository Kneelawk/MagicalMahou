package com.kneelawk.magicalmahou.client.image

import com.kneelawk.magicalmahou.image.ImageWrapper
import com.kneelawk.magicalmahou.image.ImageWrapperFactory
import com.kneelawk.magicalmahou.mixin.api.PlayerEntityRendererEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.texture.NativeImage
import net.minecraft.client.texture.NativeImageBackedTexture
import org.lwjgl.system.MemoryUtil
import java.nio.file.Files
import java.nio.file.Path

object ClientImageWrapperFactory : ImageWrapperFactory {
    override fun createFromPNG(data: ByteArray): ImageWrapper {
        val buf = MemoryUtil.memAlloc(data.size)
        buf.put(0, data)
        val newImage = NativeImage.read(NativeImage.Format.ABGR, buf)
        MemoryUtil.memFree(buf)
        return ClientImageWrapper(NativeImageBackedTexture(newImage))
    }

    override fun loadFromFile(path: Path): ImageWrapper {
        val newImage = NativeImage.read(Files.newInputStream(path))
        return ClientImageWrapper(NativeImageBackedTexture(newImage))
    }

    override fun default(): ImageWrapper {
        val mc = MinecraftClient.getInstance()
        val player = mc.player ?: throw IllegalStateException(
            "Attempted to create an image wrapper from a player who is not in the world"
        )
        PlayerEntityRendererEvents.setSkinTextureEventEnabled(false)
        val texture = mc.textureManager.getTexture(player.skinTexture)
        PlayerEntityRendererEvents.setSkinTextureEventEnabled(true)
        val newImage = NativeImage(NativeImage.Format.ABGR, 64, 64, false)
        texture.bindTexture()
        newImage.loadFromTextureImage(0, false)
        return ClientImageWrapper(NativeImageBackedTexture(newImage))
    }
}