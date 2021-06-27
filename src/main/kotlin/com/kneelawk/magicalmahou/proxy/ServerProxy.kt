package com.kneelawk.magicalmahou.proxy

import com.kneelawk.magicalmahou.image.PlayerSkinModel
import net.minecraft.entity.player.PlayerEntity

object ServerProxy : CommonProxy {
    override fun getDefaultPlayerSkinModel(player: PlayerEntity): PlayerSkinModel {
        return PlayerSkinModel.DEFAULT
    }
}