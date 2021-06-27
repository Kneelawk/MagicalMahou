package com.kneelawk.magicalmahou

import net.minecraft.util.Identifier

object MMConstants {
    val MOD_ID = "magical-mahou"

    val PLAYER_SKIN_PATH = "player-skin"

    fun id(path: String): Identifier {
        return Identifier(MOD_ID, path)
    }

    fun str(path: String): String {
        return "$MOD_ID:$path"
    }
}