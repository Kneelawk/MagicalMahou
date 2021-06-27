package com.kneelawk.magicalmahou.image

import java.io.IOException

sealed class InvalidImageException : IOException {
    constructor() : super()
    constructor(cause: Throwable?) : super(cause)

    class BadImage(cause: Throwable?) : InvalidImageException(cause)

    data class WrongDimensions(
        val requiredWidth: Int, val requiredHeight: Int, val providedWidth: Int, val providedHeight: Int
    ) : InvalidImageException()
}