package com.kneelawk.magicalmahou.client.screen.icon

import io.github.cottonmc.cotton.gui.widget.icon.Icon
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack

interface EnhancedIcon : Icon {
    val baseWidth: Int
    val baseHeight: Int

    @Environment(EnvType.CLIENT)
    fun paint(matrices: MatrixStack, x: Int, y: Int, width: Int, height: Int)

    @Environment(EnvType.CLIENT)
    fun paint(matrices: MatrixStack, consumers: VertexConsumerProvider, x: Int, y: Int, width: Int, height: Int)

    @Environment(EnvType.CLIENT)
    override fun paint(matrices: MatrixStack, x: Int, y: Int, size: Int) {
        paint(matrices, x, y, size, size)
    }
}