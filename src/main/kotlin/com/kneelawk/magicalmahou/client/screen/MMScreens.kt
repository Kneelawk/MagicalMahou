package com.kneelawk.magicalmahou.client.screen

import com.kneelawk.magicalmahou.screenhandler.MMScreenHandlers
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry

/**
 * Manages screens from the logical client side.
 */
object MMScreens {
    fun init() {
        ScreenRegistry.register(MMScreenHandlers.CRYSTAL_BALL, ::CrystalBallScreen)
        ScreenRegistry.register(MMScreenHandlers.CAT_EARS, ::CatEarsScreen)
        ScreenRegistry.register(MMScreenHandlers.TELEPORT_AT, ::TeleportAtScreen)
        ScreenRegistry.register(MMScreenHandlers.LONG_FALL, ::LongFallScreen)
    }
}