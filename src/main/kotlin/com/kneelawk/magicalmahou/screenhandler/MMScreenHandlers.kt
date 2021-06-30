package com.kneelawk.magicalmahou.screenhandler

import com.kneelawk.magicalmahou.MMConstants.id
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry
import net.minecraft.screen.ScreenHandlerType

/**
 * Manages screens from the logical server (common) side.
 */
object MMScreenHandlers {
    lateinit var CRYSTAL_BALL: ScreenHandlerType<CrystalBallScreenHandler>

    fun init() {
        CRYSTAL_BALL = ScreenHandlerRegistry.registerSimple(id("crystal_ball"), ::CrystalBallScreenHandler)
    }
}