package com.kneelawk.magicalmahou.util

object EnumUtils {
    inline fun <reified E : Enum<E>> rotateEnum(value: E, amount: Int): E {
        val values = E::class.java.enumConstants
        return values[Math.floorMod(value.ordinal + amount, values.size)]
    }
}