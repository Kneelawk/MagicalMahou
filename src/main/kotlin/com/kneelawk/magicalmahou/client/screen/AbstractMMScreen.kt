package com.kneelawk.magicalmahou.client.screen

import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text

abstract class AbstractMMScreen<T : SyncedGuiDescription>(gui: T, player: PlayerEntity, title: Text) :
    CottonInventoryScreen<T>(gui, player, title) {
    override fun init() {
        super.init()
        MMScreenUtils.applyCursorPosition()
    }
}