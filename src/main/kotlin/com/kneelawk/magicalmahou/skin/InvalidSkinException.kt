package com.kneelawk.magicalmahou.skin

import java.io.IOException

sealed class InvalidSkinException : IOException {
    constructor() : super()
    constructor(cause: Throwable?) : super(cause)

    class BadImage(cause: Throwable?) : InvalidSkinException(cause)

    data class WrongDimensions(
        val requiredWidth: Int, val requiredHeight: Int, val providedWidth: Int, val providedHeight: Int
    ) : InvalidSkinException()
}