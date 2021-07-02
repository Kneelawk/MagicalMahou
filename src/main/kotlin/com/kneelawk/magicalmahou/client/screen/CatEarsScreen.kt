package com.kneelawk.magicalmahou.client.screen

import com.kneelawk.magicalmahou.screenhandler.CatEarsScreenHandler
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text

class CatEarsScreen(gui: CatEarsScreenHandler, inventory: PlayerInventory, title: Text) :
    AbstractMMScreen<CatEarsScreenHandler>(gui, inventory.player, title)
