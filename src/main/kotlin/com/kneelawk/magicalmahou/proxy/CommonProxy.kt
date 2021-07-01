package com.kneelawk.magicalmahou.proxy

import com.kneelawk.magicalmahou.skin.PlayerSkinModel
import net.minecraft.entity.player.PlayerEntity

interface CommonProxy {
    fun getDefaultPlayerSkinModel(player: PlayerEntity): PlayerSkinModel
}