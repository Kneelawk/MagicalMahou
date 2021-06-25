package com.kneelawk.magicalmahou.image

import java.io.IOException
import java.nio.file.Path

interface ImageWrapper {
    val width: Int
    val height: Int

    fun encodePNGData(): ByteArray

    @Throws(IOException::class)
    fun writeToFile(path: Path)

    fun setARGBPixel(x: Int, y: Int, color: Int)

    fun decodePNGData(data: ByteArray)

    fun update()
}