package com.kneelawk.magicalmahou.client.screen

import com.kneelawk.magicalmahou.mixin.impl.MouseAccessor
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW
import java.util.concurrent.atomic.AtomicReference

object MMScreenUtils {
    private val cursorPosition = AtomicReference<CursorPosition?>(null)

    fun presetCursorPosition() {
        val mouse = MinecraftClient.getInstance().mouse
        cursorPosition.set(CursorPosition(mouse.x, mouse.y))
    }

    fun applyCursorPosition() {
        cursorPosition.getAndSet(null)?.let { position ->
            val client = MinecraftClient.getInstance()
            val mouse = client.mouse as MouseAccessor
            mouse.setX(position.x)
            mouse.setY(position.y)
            // I don't know what 212993 means. I just copied it from Mouse#unlockCursor.
            InputUtil.setCursorParameters(client.window.handle, GLFW.GLFW_CURSOR_NORMAL, position.x, position.y)
        }
    }

    data class CursorPosition(val x: Double, val y: Double)
}