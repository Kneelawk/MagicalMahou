package com.kneelawk.magicalmahou.item

import com.kneelawk.magicalmahou.MMConstants.id
import com.kneelawk.magicalmahou.block.MMBlocks
import com.kneelawk.magicalmahou.component.MMComponents
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry

object MMItems {
    lateinit var ITEM_GROUP: ItemGroup
        private set
    lateinit var ITEM_SETTINGS: Item.Settings
        private set

    lateinit var LONG_FALL_CONTRACT: ContractItem
        private set
    lateinit var TELEPORT_AT_CONTRACT: ContractItem

    fun init() {
        ITEM_SETTINGS = FabricItemSettings()

        LONG_FALL_CONTRACT = ContractItem(MMComponents.LONG_FALL, "long_fall", ITEM_SETTINGS)
        TELEPORT_AT_CONTRACT = ContractItem(MMComponents.TELEPORT_AT, "teleport_at", ITEM_SETTINGS)

        Registry.register(Registries.ITEM, id("long_fall_contract"), LONG_FALL_CONTRACT)
        Registry.register(Registries.ITEM, id("teleport_at_contract"), TELEPORT_AT_CONTRACT)

        ITEM_GROUP = FabricItemGroup.builder(id("magical-mahou")).icon { ItemStack(MMBlocks.CRYSTAL_BALL) }
            .entries { _, entries, _ ->
                entries.add(LONG_FALL_CONTRACT)
                entries.add(TELEPORT_AT_CONTRACT)
            }.build()
    }
}
