package com.kneelawk.mama

import net.minecraft.util.Identifier

object MMConstants {
    val MOD_ID = "mama"

    fun id(path: String): Identifier {
        return Identifier(MOD_ID, path)
    }
}