package com.kneelawk.magicalmahou.util

object ColorUtils {
    fun argbFromBytes(a: Int, r: Int, g: Int, b: Int): Int {
        return ((a and 0xFF) shl 0x18) or ((r and 0xFF) shl 0x10) or ((g and 0xFF) shl 0x08) or (b and 0xFF)
    }

    fun rgbToBytes(rgb: Int): ByteArray {
        return byteArrayOf(
            ((rgb shr 0x10) and 0xFF).toByte(), ((rgb shr 0x08) and 0xFF).toByte(), (rgb and 0xFF).toByte()
        )
    }
}