package com.kneelawk.magicalmahou.client.image

import com.kneelawk.magicalmahou.MMConstants
import com.kneelawk.magicalmahou.image.SkinManagerHolder
import com.kneelawk.magicalmahou.image.SkinManagers
import com.kneelawk.magicalmahou.server.image.ServerSkinManager

object ClientSkinManagers {
    lateinit var CLIENT_PLAYER_SKIN: ClientSkinManager
        private set
    lateinit var SERVER_PLAYER_SKIN: ServerSkinManager
        private set

    fun init() {
        CLIENT_PLAYER_SKIN = ClientSkinManager(64, 64, MMConstants.PLAYER_SKIN_PATH, true)
        SERVER_PLAYER_SKIN = ServerSkinManager(64, 64)

        SkinManagers.init(SkinManagerHolder(CLIENT_PLAYER_SKIN, SERVER_PLAYER_SKIN))
    }
}