package com.kneelawk.magicalmahou.server.image

import com.kneelawk.magicalmahou.image.ImageWrapper
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.Path
import javax.imageio.ImageIO

class ServerImageWrapper(private var image: BufferedImage) : ImageWrapper {
    override val width = image.width
    override val height = image.height

    override fun encodePNGData(): ByteArray {
        val baos = ByteArrayOutputStream()
        ImageIO.write(image, "png", baos)
        return baos.toByteArray()
    }

    override fun writeToFile(path: Path) {
        ImageIO.write(image, "png", Files.newOutputStream(path))
    }

    override fun setARGBPixel(x: Int, y: Int, color: Int) {
        image.setRGB(x, y, color)
    }

    override fun decodePNGData(data: ByteArray) {
        val newImage = ImageIO.read(ByteArrayInputStream(data))

        if (newImage.width != width || newImage.height != height) {
            throw IllegalStateException("PNG dimensions do not match current ones")
        }

        image = newImage
    }

    override fun update() {
        // Nothing to do on the server side
    }
}