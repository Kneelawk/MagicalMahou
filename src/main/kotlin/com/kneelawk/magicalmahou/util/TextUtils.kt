package com.kneelawk.magicalmahou.util

import com.kneelawk.magicalmahou.MMConstants.gui
import net.minecraft.text.MutableText

object TextUtils {
    fun enabled(enabled: Boolean): MutableText {
        return if (enabled) {
            gui("enabled")
        } else {
            gui("disabled")
        }
    }
}