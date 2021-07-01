package com.kneelawk.magicalmahou.skin

import net.minecraft.world.World

object SkinManagers {
    private var playerSkin: SkinManagerHolder? = null

    fun init(playerSkin: SkinManagerHolder) {
        this.playerSkin = playerSkin
    }

    fun getPlayerSkinManger(world: World): SkinManager {
        return playerSkin?.getSkinManager(world) ?: throw IllegalStateException(
            "SkinManagers has not been initialized yet"
        )
    }
}