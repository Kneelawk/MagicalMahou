package com.kneelawk.magicalmahou.proxy

import com.kneelawk.magicalmahou.mixin.api.PlayerEntityRendererEvents
import com.kneelawk.magicalmahou.MMLog
import com.kneelawk.magicalmahou.image.PlayerSkinModel
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.entity.player.PlayerEntity

object ClientProxy : CommonProxy {
    override fun getDefaultPlayerSkinModel(player: PlayerEntity): PlayerSkinModel {
        return if (player is AbstractClientPlayerEntity) {
            PlayerEntityRendererEvents.setSkinEventsEnabled(false)
            val modelStr = player.model
            PlayerEntityRendererEvents.setSkinEventsEnabled(true)
            val model = PlayerSkinModel.byModelStr(modelStr)

            if (model == null) {
                MMLog.warn("Found invalid player skin model: $modelStr")
                PlayerSkinModel.DEFAULT
            } else {
                model
            }
        } else {
            PlayerSkinModel.DEFAULT
        }
    }
}