package com.kneelawk.magicalmahou.client.render.player

import com.kneelawk.magicalmahou.MMConstants.id
import com.kneelawk.magicalmahou.client.render.ObjModelPart
import com.kneelawk.magicalmahou.component.MMComponents
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
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

@Environment(EnvType.CLIENT)
class CatEarsFeatureRenderer(
    ctx: FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>
) : FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>(ctx) {

    private val entityModel: BipedEntityModel<AbstractClientPlayerEntity>

    init {
        val root = ModelPart(
            listOf(), mapOf(
                EntityModelPartNames.HEAD to ObjModelPart(
                    listOf(
                        ObjModelPart.ObjPart(
                            id("models/misc/cat_ears"), Vec3f(0f, -0.5f, 0f),
                            Quaternion.IDENTITY
                        )
                    ),
                    mapOf()
                ),
                EntityModelPartNames.HAT to ModelPart(listOf(), mapOf()),
                EntityModelPartNames.BODY to ModelPart(listOf(), mapOf()),
                EntityModelPartNames.RIGHT_ARM to ModelPart(listOf(), mapOf()),
                EntityModelPartNames.LEFT_ARM to ModelPart(listOf(), mapOf()),
                EntityModelPartNames.RIGHT_LEG to ModelPart(listOf(), mapOf()),
                EntityModelPartNames.LEFT_LEG to ModelPart(listOf(), mapOf())
            )
        )

        entityModel = BipedEntityModel(root)
    }

    override fun render(
        matrices: MatrixStack, vertexConsumers: VertexConsumerProvider, light: Int,
        entity: AbstractClientPlayerEntity, limbAngle: Float, limbDistance: Float, tickDelta: Float,
        animationProgress: Float, headYaw: Float, headPitch: Float
    ) {
        // Only render the cat ears if transformed (debug I guess)
        val component = MMComponents.GENERAL[entity]

        if (component.isTransformed) {
            // This does all the rotation and positioning for me!
            contextModel.setAttributes(entityModel)

            val consumer = vertexConsumers.getBuffer(RenderLayer.getCutout())
            val overlay = LivingEntityRenderer.getOverlay(entity, 0.0f)
            entityModel.render(matrices, consumer, light, overlay, 1f, 1f, 1f, 1f)
        }
    }
}