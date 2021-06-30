package com.kneelawk.magicalmahou.screenhandler

import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.data.Insets
import net.minecraft.entity.player.PlayerInventory

class CrystalBallScreenHandler(syncId: Int, inventory: PlayerInventory) :
    SyncedGuiDescription(MMScreenHandlers.CRYSTAL_BALL, syncId, inventory) {
    init {
        val root = WGridPanel()
        rootPanel = root

        root.insets = Insets.ROOT_PANEL

        root.validate(this)
    }
}