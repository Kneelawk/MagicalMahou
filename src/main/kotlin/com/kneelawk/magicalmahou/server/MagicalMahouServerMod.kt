package com.kneelawk.magicalmahou.server

import com.kneelawk.magicalmahou.skin.SkinManagerHolder
import com.kneelawk.magicalmahou.skin.SkinManagers
import com.kneelawk.magicalmahou.proxy.MMProxy
import com.kneelawk.magicalmahou.proxy.ServerProxy
import com.kneelawk.magicalmahou.server.skin.ServerSkinManager

fun init() {
    MMProxy.init(ServerProxy)

    // Setup image wrapper factory
    SkinManagers.init(SkinManagerHolder(null, ServerSkinManager(64, 64)))
}
