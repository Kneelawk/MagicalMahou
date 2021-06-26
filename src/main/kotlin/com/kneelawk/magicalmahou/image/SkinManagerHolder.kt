package com.kneelawk.magicalmahou.image

import net.minecraft.world.World

data class SkinManagerHolder(val logicalClient: SkinManager?, val logicalServer: SkinManager) {
    fun getSkinManager(world: World): SkinManager {
        return if (world.isClient) {
            logicalClient ?: throw IllegalStateException(
                "Supplied a client world without a logical client skin manager"
            )
        } else {
            logicalServer
        }
    }
}