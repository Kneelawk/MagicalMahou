package com.kneelawk.magicalmahou.screenhandler

import com.kneelawk.magicalmahou.MMConstants
import com.kneelawk.magicalmahou.MMConstants.id
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.SimpleNamedScreenHandlerFactory

/**
 * Manages screens from the logical server (common) side.
 */
object MMScreenHandlers {
    private val CRYSTAL_BALL_TITLE = MMConstants.tt("container", "crystal_ball")

    lateinit var CRYSTAL_BALL: ScreenHandlerType<CrystalBallScreenHandler>
    lateinit var CAT_EARS: ScreenHandlerType<CatEarsScreenHandler>
    lateinit var TELEPORT_AT: ScreenHandlerType<TeleportAtScreenHandler>

    fun init() {
        CRYSTAL_BALL = ScreenHandlerRegistry.registerSimple(id("crystal_ball"), ::CrystalBallScreenHandler)
        CAT_EARS = ScreenHandlerRegistry.registerSimple(id("cat_ears"), ::CatEarsScreenHandler)
        TELEPORT_AT = ScreenHandlerRegistry.registerSimple(id("teleport_at"), ::TeleportAtScreenHandler)
    }

    fun createCrystalBallScreenHandlerFactory(): NamedScreenHandlerFactory {
        return SimpleNamedScreenHandlerFactory(
            { syncId, inventory, _ -> CrystalBallScreenHandler(syncId, inventory) }, CRYSTAL_BALL_TITLE
        )
    }
}