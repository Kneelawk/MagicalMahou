package com.kneelawk.magicalmahou.client.screen

import com.kneelawk.magicalmahou.screenhandler.TeleportAtScreenHandler
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text

class TeleportAtScreen(gui: TeleportAtScreenHandler, inventory: PlayerInventory, title: Text) :
    AbstractMMScreen<TeleportAtScreenHandler>(gui, inventory.player, title)
