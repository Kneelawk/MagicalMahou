package com.kneelawk.magicalmahou.block

import com.kneelawk.magicalmahou.MMConstants.id
import com.kneelawk.magicalmahou.item.MMItems
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.block.MapColor
import net.minecraft.block.Material
import net.minecraft.item.BlockItem
import net.minecraft.util.registry.Registry

object MMBlocks {
    lateinit var CRYSTAL_BALL: CrystalBallBlock
        private set

    fun init() {
        val crystalBallSettings =
            FabricBlockSettings.of(Material.GLASS).mapColor(MapColor.PURPLE).requiresTool().strength(50.0F, 1200.0F)
                .nonOpaque()

        CRYSTAL_BALL = CrystalBallBlock(crystalBallSettings)

        Registry.register(Registry.BLOCK, id("crystal_ball"), CRYSTAL_BALL)
        Registry.register(Registry.ITEM, id("crystal_ball"), BlockItem(CRYSTAL_BALL, MMItems.ITEM_SETTINGS))
    }
}