package com.kneelawk.magicalmahou.server

import com.kneelawk.magicalmahou.image.MMImageUtils
import com.kneelawk.magicalmahou.server.image.ServerImageWrapperFactory

fun init() {
    // Setup image wrapper factory
    MMImageUtils.IMAGE_FACTORY = ServerImageWrapperFactory
}
