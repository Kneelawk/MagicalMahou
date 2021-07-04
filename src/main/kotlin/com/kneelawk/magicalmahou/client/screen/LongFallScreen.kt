package com.kneelawk.magicalmahou.client.screen

import com.kneelawk.magicalmahou.screenhandler.LongFallScreenHandler
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text

class LongFallScreen(gui: LongFallScreenHandler, inventory: PlayerInventory, title: Text) :
    AbstractMMScreen<LongFallScreenHandler>(gui, inventory.player, title)
