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
import net.minecraft.util.math.Quaternion
import net.minecraft.util.math.Vec3f

class TeleportAtFeatureRenderer(
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
                            MMConstants.id("models/misc/teleport_at"), Vec3f(0f, 9f / 16f, 0f),
                            Quaternion.IDENTITY
                        )
                    ),
                    mapOf()
                ),
                EntityModelPartNames.LEFT_LEG to ObjModelPart(
                    listOf(
                        ObjModelPart.ObjPart(
                            MMConstants.id("models/misc/teleport_at"), Vec3f(0f, 9f / 16f, 0f),
                            Quaternion.IDENTITY
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
        val general = MMComponents.GENERAL[entity]
        val teleportAt = MMComponents.TELEPORT_AT[entity]

        if (general.isActuallyTransformed() && teleportAt.isActuallyEnabled()) {
            contextModel.setAttributes(model)

            val consumer = vertexConsumers.getBuffer(RenderLayer.getCutout())
            val overlay = LivingEntityRenderer.getOverlay(entity, 0.0f)
            model.render(matrices, consumer, light, overlay, 1f, 1f, 1f, 1f)
        }
    }
}