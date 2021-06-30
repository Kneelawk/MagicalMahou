package com.kneelawk.magicalmahou.client.render

import com.kneelawk.magicalmahou.MMConstants.str
import com.kneelawk.magicalmahou.mixin.api.RenderLayerHelper
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.RenderPhase
import net.minecraft.client.render.VertexFormat
import net.minecraft.client.render.VertexFormats
import net.minecraft.util.Identifier
import net.minecraft.util.Util

object MMRenderLayers {
    private val ICON = Util.memoize<Identifier, RenderLayer> { texture ->
        val multiPhaseParameters = RenderLayer.MultiPhaseParameters.builder()
            .texture(RenderPhase.Texture(texture, false, false))
            .transparency(RenderLayerHelper.getTranslucentTransparency())
            .build(true)

        RenderLayerHelper.of(
            str("icon"), VertexFormats.POSITION_COLOR_TEXTURE, VertexFormat.DrawMode.QUADS, 256, true,
            false,
            multiPhaseParameters
        )
    }

    fun getIcon(texture: Identifier): RenderLayer {
        return ICON.apply(texture)
    }
}