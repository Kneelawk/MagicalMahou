package com.kneelawk.magicalmahou.component

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.screen.ScreenHandler

object ComponentHelper {
    inline fun <reified T : ScreenHandler> withScreenHandler(player: PlayerEntity, callback: (T) -> Unit) {
        val openScreen = player.currentScreenHandler
        if (openScreen is T) {
            callback(openScreen)
        }
    }
}