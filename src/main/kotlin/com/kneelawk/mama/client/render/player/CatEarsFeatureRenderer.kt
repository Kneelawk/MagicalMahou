package com.kneelawk.mama.client.render.player

import com.kneelawk.mama.MMConstants.id
import dev.monarkhes.myron.api.Myron
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.render.entity.LivingEntityRenderer
import net.minecraft.client.render.entity.feature.FeatureRenderer
import net.minecraft.client.render.entity.feature.FeatureRendererContext
import net.minecraft.client.render.entity.model.PlayerEntityModel
import net.minecraft.client.render.model.BakedQuad
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Vec3f
import net.minecraft.util.math.Vector4f
import org.lwjgl.system.MemoryStack

@Environment(EnvType.CLIENT)
class CatEarsFeatureRenderer(
    ctx: FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>
) : FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>(ctx) {
    override fun render(
        matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int,
        entity: AbstractClientPlayerEntity, limbAngle: Float, limbDistance: Float, tickDelta: Float,
        animationProgress: Float, headYaw: Float, headPitch: Float
    ) {
        val model = Myron.getModel(id("models/misc/cat_ears"))

        if (model != null) {
            val consumer = vertexConsumers.getBuffer(RenderLayer.getCutout())

            val head = contextModel.head

            matrices.push()

            matrices.translate(0.0, if (entity.isInSneakingPose) 0.27 else 0.0, 0.0)

            matrices.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(head.yaw))
            matrices.multiply(Vec3f.POSITIVE_X.getRadialQuaternion(head.pitch))
            matrices.translate(0.0, -0.5, 0.0)
            matrices.multiply(Vec3f.POSITIVE_X.getRadialQuaternion(-head.pitch))
            matrices.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(-head.yaw))

            matrices.translate(0.0, 0.0, head.pivotZ.toDouble())
            if (head.roll != 0.0f) {
                matrices.multiply(Vec3f.POSITIVE_Z.getRadialQuaternion(head.roll))
            }
            if (head.yaw != 0.0f) {
                matrices.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(head.yaw))
            }
            if (head.pitch != 0.0f) {
                matrices.multiply(Vec3f.POSITIVE_X.getRadialQuaternion(head.pitch))
            }
            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180f))

            val entry = matrices.peek()

            val overlay = LivingEntityRenderer.getOverlay(entity, 0.0f)

            model.getQuads(null, null, entity.world.random).forEach { quad ->
                consumer.quad(entry, quad, 1f, 1f, 1f, light, overlay)
//                objQuad(consumer, entry, quad, 1f, 1f, 1f, light, overlay)
            }

            matrices.pop()
        }
    }

    fun objQuad(
        consumer: VertexConsumer, matrixEntry: MatrixStack.Entry, quad: BakedQuad, red: Float, green: Float,
        blue: Float, light: Int, overlay: Int
    ) {
        val vertexData = quad.vertexData
        val normInt = quad.face.vector
        val normal = Vec3f(normInt.x.toFloat(), normInt.y.toFloat(), normInt.z.toFloat())
        val modelMatrix = matrixEntry.model
        normal.transform(matrixEntry.normal)
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
                    position.x, position.y, position.z, red, green, blue, 1.0f, u, v, overlay, light, normal.x,
                    normal.y, normal.z
                )
            }
        }
    }
}