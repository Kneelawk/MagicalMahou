package com.kneelawk.magicalmahou.block

import com.kneelawk.magicalmahou.MMConstants.id
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.block.Block
import net.minecraft.block.MapColor
import net.minecraft.block.Material
import net.minecraft.item.BlockItem
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.util.registry.Registry

object MMBlocks {
    lateinit var ITEM_GROUP: ItemGroup
        private set

    lateinit var CRYSTAL_BALL: Block
        private set

    fun init() {
        ITEM_GROUP = FabricItemGroupBuilder.build(id("magical-mahou")) { ItemStack(CRYSTAL_BALL) }

        val itemSettings = FabricItemSettings().group(ITEM_GROUP)
        val crystalBallSettings =
            FabricBlockSettings.of(Material.GLASS).mapColor(MapColor.PURPLE).requiresTool().strength(50.0F, 1200.0F).nonOpaque()

        CRYSTAL_BALL = Block(crystalBallSettings)

        Registry.register(Registry.BLOCK, id("crystal_ball"), CRYSTAL_BALL)
        Registry.register(Registry.ITEM, id("crystal_ball"), BlockItem(CRYSTAL_BALL, itemSettings))
    }
}