package com.kneelawk.magicalmahou

import net.minecraft.util.Identifier

object MMConstants {
    val MOD_ID = "magical-mahou"

    fun id(path: String): Identifier {
        return Identifier(MOD_ID, path)
    }
}