package com.kneelawk.magicalmahou.server.image

import com.kneelawk.magicalmahou.image.ImageWrapper
import com.kneelawk.magicalmahou.image.ImageWrapperFactory
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import javax.imageio.ImageIO

object ServerImageWrapperFactory : ImageWrapperFactory {
    override fun createFromPNG(data: ByteArray): ImageWrapper {
        val newImage = ImageIO.read(ByteArrayInputStream(data))
        return ServerImageWrapper(newImage)
    }

    override fun loadFromFile(path: Path): ImageWrapper {
        val newImage = ImageIO.read(Files.newInputStream(path))
        return ServerImageWrapper(newImage)
    }

    override fun default(): ImageWrapper {
        return ServerImageWrapper(BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB))
    }
}