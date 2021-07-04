package com.kneelawk.magicalmahou.util

import net.minecraft.client.MinecraftClient
import net.minecraft.util.math.BlockPos
import net.minecraft.world.RaycastContext

object RaycastUtils {
    fun clientRaycast(maxDistance: Double): BlockPos? {
        val mc = MinecraftClient.getInstance()
        val raycastEntity = mc.cameraEntity ?: return null
        val world = mc.world ?: return null
        val tickDelta = mc.tickDelta
        val direction = raycastEntity.getRotationVec(tickDelta)
        val start = raycastEntity.getCameraPosVec(tickDelta)
        val end = start.add(direction.multiply(maxDistance))

        val raycast = world.raycast(
            RaycastContext(
                start, end, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.ANY, raycastEntity
            )
        )

        val hitPos = raycast.blockPos
        val hitSide = raycast.side

        if (world.isAir(hitPos)) {
            return null
        }

        val resultPos = hitPos.offset(hitSide)

        if (!world.isAir(resultPos) || !world.isAir(resultPos.up())) {
            return null
        }

        return resultPos
    }
}