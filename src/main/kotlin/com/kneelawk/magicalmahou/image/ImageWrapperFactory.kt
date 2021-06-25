package com.kneelawk.magicalmahou.image

import java.io.IOException
import java.nio.file.Path

interface ImageWrapperFactory {
    fun createFromPNG(data: ByteArray): ImageWrapper

    @Throws(IOException::class)
    fun loadFromFile(path: Path): ImageWrapper

    fun default(): ImageWrapper
}