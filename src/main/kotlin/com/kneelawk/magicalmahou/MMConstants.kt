package com.kneelawk.magicalmahou

import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
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

    fun tt(prefix: String, suffix: String, vararg args: Any?): Text {
        return TranslatableText("$prefix.$MOD_ID.$suffix", *args)
    }

    fun gui(suffix: String, vararg args: Any?): Text {
        return tt("gui", suffix, *args)
    }
}