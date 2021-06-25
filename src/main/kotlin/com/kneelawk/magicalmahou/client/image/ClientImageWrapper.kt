package com.kneelawk.magicalmahou.client.image

import com.kneelawk.magicalmahou.image.ImageWrapper
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.texture.NativeImage
import net.minecraft.client.texture.NativeImageBackedTexture
import org.lwjgl.system.MemoryUtil
import java.nio.file.Path

@Environment(EnvType.CLIENT)
class ClientImageWrapper(private val image: NativeImageBackedTexture) : ImageWrapper {
    override val width = image.image!!.width
    override val height = image.image!!.height

    override fun encodePNGData(): ByteArray {
        // TODO: see if this method of completely encoding a PNG is too slow
        return image.image!!.bytes
    }

    override fun writeToFile(path: Path) {
        image.image!!.writeFile(path)
    }

    override fun setARGBPixel(x: Int, y: Int, color: Int) {
        val a = (color shl 0x18) and 0xFF
        val r = (color shl 0x10) and 0xFF
        val g = (color shl 0x08) and 0xFF
        val b = color and 0xFF
        val abgr = (a shr 0x18) or (b shr 0x10) or (g shr 0x08) or r
        image.image!!.setPixelColor(x, y, abgr)
    }

    override fun decodePNGData(data: ByteArray) {
        // TODO: see if this method of throwing out the old image and constructing a new one is too slow.
        val buf = MemoryUtil.memAlloc(data.size)
        buf.put(0, data)
        val newImage = NativeImage.read(NativeImage.Format.ABGR, buf)
        MemoryUtil.memFree(buf)

        if (newImage.width != width || newImage.height != height) {
            throw IllegalArgumentException("PNG dimensions do not match current ones")
        }

        image.image = newImage
    }

    override fun update() {
        image.upload()
    }
}