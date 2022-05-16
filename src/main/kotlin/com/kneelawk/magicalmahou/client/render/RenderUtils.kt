package com.kneelawk.magicalmahou.client.render

import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.render.model.BakedQuad
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Vec3f
import net.minecraft.util.math.Vector4f
import org.lwjgl.system.MemoryStack

object RenderUtils {
    fun quad(
        consumer: VertexConsumer, matrixEntry: MatrixStack.Entry, quad: BakedQuad, red: Float, green: Float,
        blue: Float, alpha: Float, light: Int, overlay: Int
    ) {
        val vertexData = quad.vertexData
        val normInt = quad.face.vector
        val normal = Vec3f(normInt.x.toFloat(), normInt.y.toFloat(), normInt.z.toFloat())
        val modelMatrix = matrixEntry.positionMatrix
        normal.transform(matrixEntry.normalMatrix)
        val vertexCount = vertexData.size / 8

        MemoryStack.stackPush().use { memoryStack ->
            val byteBuffer = memoryStack.malloc(VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL.vertexSize)
            val intBuffer = byteBuffer.asIntBuffer()

            for (vertex in 0 until vertexCount) {
                intBuffer.clear()
                intBuffer.put(vertexData, vertex * 8, 8)

                val posX = byteBuffer.getFloat(0)
                val posY = byteBuffer.getFloat(4)
                val posZ = byteBuffer.getFloat(8)
                val u: Float = byteBuffer.getFloat(16)
                val v: Float = byteBuffer.getFloat(20)

                val position = Vector4f(posX, posY, posZ, 1.0f)
                position.transform(modelMatrix)

                consumer.vertex(
                    position.x, position.y, position.z, red, green, blue, alpha, u, v, overlay, light, normal.x,
                    normal.y, normal.z
                )
            }
        }
    }
}