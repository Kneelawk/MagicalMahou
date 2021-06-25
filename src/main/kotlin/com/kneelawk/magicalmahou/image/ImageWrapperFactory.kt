package com.kneelawk.magicalmahou.image

import java.io.IOException
import java.nio.file.Path
import java.util.*

interface ImageWrapperFactory {
    fun createFromPNG(data: ByteArray, playerId: UUID): ImageWrapper

    @Throws(IOException::class)
    fun loadFromFile(path: Path, playerId: UUID): ImageWrapper

    fun default(playerId: UUID): ImageWrapper
}