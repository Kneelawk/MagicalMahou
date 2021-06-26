package com.kneelawk.magicalmahou.server

import com.kneelawk.magicalmahou.image.SkinManagerHolder
import com.kneelawk.magicalmahou.image.SkinManagers
import com.kneelawk.magicalmahou.server.image.ServerSkinManager

fun init() {
    // Setup image wrapper factory
    SkinManagers.init(SkinManagerHolder(null, ServerSkinManager(64, 64)))
}
