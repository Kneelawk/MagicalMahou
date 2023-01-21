package com.kneelawk.magicalmahou.client.render.player

import com.kneelawk.magicalmahou.MMConstants
import com.kneelawk.magicalmahou.client.render.ObjModelPart
import com.kneelawk.magicalmahou.component.MMComponents
import net.minecraft.client.model.ModelPart
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.LivingEntityRenderer
import net.minecraft.client.render.entity.feature.FeatureRenderer
import net.minecraft.client.render.entity.feature.FeatureRendererContext
import net.minecraft.client.render.entity.model.BipedEntityModel
import net.minecraft.client.render.entity.model.EntityModelPartNames
import net.minecraft.client.render.entity.model.PlayerEntityModel
import net.minecraft.client.util.math.MatrixStack
import org.joml.Quaternionf
import org.joml.Vector3f

class LongFallFeatureRenderer(
    ctx: FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>
) : FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>(ctx) {

    private val model: BipedEntityModel<AbstractClientPlayerEntity>

    init {
        val root = ModelPart(
            listOf(), mapOf(
                EntityModelPartNames.HEAD to ModelPart(listOf(), mapOf()),
                EntityModelPartNames.HAT to ModelPart(listOf(), mapOf()),
                EntityModelPartNames.BODY to ModelPart(listOf(), mapOf()),
                EntityModelPartNames.RIGHT_ARM to ModelPart(listOf(), mapOf()),
                EntityModelPartNames.LEFT_ARM to ModelPart(listOf(), mapOf()),
                EntityModelPartNames.RIGHT_LEG to ObjModelPart(
                    listOf(
                        ObjModelPart.ObjPart(
                            MMConstants.id("models/misc/long_fall"), Vector3f(0f, 8f / 16f, 0f),
                            Quaternionf().fromAxisAngleDeg(0f, 1f, 0f, -45f)
                        )
                    ),
                    mapOf()
                ),
                EntityModelPartNames.LEFT_LEG to ObjModelPart(
                    listOf(
                        ObjModelPart.ObjPart(
                            MMConstants.id("models/misc/long_fall"), Vector3f(0f, 8f / 16f, 0f),
                            Quaternionf().fromAxisAngleDeg(0f, 1f, 0f, 45f)
                        )
                    ),
                    mapOf()
                )
            )
        )

        model = BipedEntityModel(root)
    }

    override fun render(
        matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int,
        entity: AbstractClientPlayerEntity, limbAngle: Float, limbDistance: Float, tickDelta: Float,
        animationProgress: Float, headYaw: Float, headPitch: Float
    ) {
        val longFall = MMComponents.LONG_FALL[entity]

        if (longFall.isActuallyEnabled()) {
            contextModel.copyBipedStateTo(model)

            val consumer = vertexConsumers.getBuffer(RenderLayer.getCutout())
            val overlay = LivingEntityRenderer.getOverlay(entity, 0.0f)
            model.render(matrices, consumer, light, overlay, 1f, 1f, 1f, 1f)
        }
    }
}
