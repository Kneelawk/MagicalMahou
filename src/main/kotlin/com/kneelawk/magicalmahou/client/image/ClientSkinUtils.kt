package com.kneelawk.magicalmahou.client.image

import net.minecraft.client.texture.NativeImage

object ClientSkinUtils {
    fun convertLegacyPlayerSkin(nativeImage: NativeImage): NativeImage {
        // Convert the loaded image to the standard skin size
        val nativeImage2 = NativeImage(64, 64, true)
        nativeImage2.copyFrom(nativeImage)
        nativeImage.close()
        nativeImage2.fillRect(0, 32, 64, 32, 0)
        nativeImage2.copyRect(4, 16, 16, 32, 4, 4, true, false)
        nativeImage2.copyRect(8, 16, 16, 32, 4, 4, true, false)
        nativeImage2.copyRect(0, 20, 24, 32, 4, 12, true, false)
        nativeImage2.copyRect(4, 20, 16, 32, 4, 12, true, false)
        nativeImage2.copyRect(8, 20, 8, 32, 4, 12, true, false)
        nativeImage2.copyRect(12, 20, 16, 32, 4, 12, true, false)
        nativeImage2.copyRect(44, 16, -8, 32, 4, 4, true, false)
        nativeImage2.copyRect(48, 16, -8, 32, 4, 4, true, false)
        nativeImage2.copyRect(40, 20, 0, 32, 4, 12, true, false)
        nativeImage2.copyRect(44, 20, -8, 32, 4, 12, true, false)
        nativeImage2.copyRect(48, 20, -16, 32, 4, 12, true, false)
        nativeImage2.copyRect(52, 20, -8, 32, 4, 12, true, false)
        return nativeImage2
    }
}