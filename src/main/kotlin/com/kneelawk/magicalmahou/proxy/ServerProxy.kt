package com.kneelawk.magicalmahou.proxy

import com.kneelawk.magicalmahou.MMLog
import com.kneelawk.magicalmahou.skin.PlayerSkinModel
import net.minecraft.entity.player.PlayerEntity

object ServerProxy : CommonProxy {
    override fun getDefaultPlayerSkinModel(player: PlayerEntity): PlayerSkinModel {
        return PlayerSkinModel.DEFAULT
    }

    override fun uploadPlayerSkin(player: PlayerEntity) {
        MMLog.warn("Upload player skin called on the server")
    }

    override fun pickTransformationColor(player: PlayerEntity) {
        MMLog.warn("Pick transformation color called on the server")
    }

    override fun presetCursorPosition() {
        MMLog.warn("Preset cursor position called on the server")
    }
}