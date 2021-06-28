package com.kneelawk.magicalmahou.block

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap
import net.minecraft.client.render.RenderLayer

object MMBlocksClient {
    fun init() {
        BlockRenderLayerMap.INSTANCE.putBlock(MMBlocks.CRYSTAL_BALL, RenderLayer.getTranslucent())
    }
}