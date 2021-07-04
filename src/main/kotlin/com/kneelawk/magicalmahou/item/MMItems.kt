package com.kneelawk.magicalmahou.item

import com.kneelawk.magicalmahou.MMConstants.id
import com.kneelawk.magicalmahou.block.MMBlocks
import com.kneelawk.magicalmahou.component.MMComponents
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.util.registry.Registry

object MMItems {
    lateinit var ITEM_GROUP: ItemGroup
        private set
    lateinit var ITEM_SETTINGS: Item.Settings
        private set

    lateinit var LONG_FALL_CONTRACT: ContractItem
        private set
    lateinit var TELEPORT_AT_CONTRACT: ContractItem

    fun init() {
        ITEM_GROUP = FabricItemGroupBuilder.build(id("magical-mahou")) { ItemStack(MMBlocks.CRYSTAL_BALL) }
        ITEM_SETTINGS = FabricItemSettings().group(ITEM_GROUP)

        LONG_FALL_CONTRACT = ContractItem(MMComponents.LONG_FALL, "long_fall", ITEM_SETTINGS)
        TELEPORT_AT_CONTRACT = ContractItem(MMComponents.TELEPORT_AT, "teleport_at", ITEM_SETTINGS)

        Registry.register(Registry.ITEM, id("long_fall_contract"), LONG_FALL_CONTRACT)
        Registry.register(Registry.ITEM, id("teleport_at_contract"), TELEPORT_AT_CONTRACT)
    }
}