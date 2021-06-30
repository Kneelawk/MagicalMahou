package com.kneelawk.magicalmahou.client.screen

import com.kneelawk.magicalmahou.screenhandler.CrystalBallScreenHandler
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text

class CrystalBallScreen(handler: CrystalBallScreenHandler, inventory: PlayerInventory, title: Text) :
    CottonInventoryScreen<CrystalBallScreenHandler>(handler, inventory.player, title)
