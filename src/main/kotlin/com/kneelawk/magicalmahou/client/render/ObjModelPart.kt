package com.kneelawk.magicalmahou.client.render

import com.kneelawk.magicalmahou.mixin.api.MatrixStackUtils
import dev.monarkhes.myron.api.Myron
import net.minecraft.client.model.ModelPart
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier
import net.minecraft.util.math.Quaternion
import net.minecraft.util.math.Vec3f
import java.util.*

class ObjModelPart(private val parts: List<ObjPart>, private val children: Map<String, ModelPart>) :
    ModelPart(listOf(), children) {
    data class ObjPart(val id: Identifier, val pos: Vec3f, val rot: Quaternion) {
        fun render(
            entry: MatrixStack.Entry, vertexConsumer: VertexConsumer, light: Int, overlay: Int, red: Float,
            green: Float, blue: Float, alpha: Float
        ) {
            Myron.getModel(id)?.let { model ->
                val newEntry = MatrixStackUtils.copyEntry(entry)
                newEntry.model.multiplyByTranslation(pos.x, pos.y, pos.z)
                newEntry.model.multiply(rot)
                newEntry.normal.multiply(rot)
                model.getQuads(null, null, Random()).forEach { quad ->
                    RenderUtils.quad(
                        vertexConsumer, newEntry, quad, red, green, blue, alpha, light, overlay
                    )
                }
            }
        }
    }

    override fun render(
        matrices: MatrixStack, vertices: VertexConsumer, light: Int, overlay: Int, red: Float, green: Float,
        blue: Float, alpha: Float
    ) {
        if (visible) {
            if (parts.isNotEmpty() || children.isNotEmpty()) {
                matrices.push()
                rotate(matrices)

                for (part in parts) {
                    part.render(matrices.peek(), vertices, light, overlay, red, green, blue, alpha)
                }

                for (modelPart in children.values) {
                    modelPart.render(matrices, vertices, light, overlay, red, green, blue, alpha)
                }

                matrices.pop()
            }
        }
    }
}