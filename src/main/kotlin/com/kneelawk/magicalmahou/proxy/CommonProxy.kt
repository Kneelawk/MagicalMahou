package com.kneelawk.magicalmahou.proxy

import com.kneelawk.magicalmahou.image.PlayerSkinModel
import net.minecraft.entity.player.PlayerEntity

interface CommonProxy {
    fun getDefaultPlayerSkinModel(player: PlayerEntity): PlayerSkinModel
}