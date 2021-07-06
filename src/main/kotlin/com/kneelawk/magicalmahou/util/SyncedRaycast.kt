package com.kneelawk.magicalmahou.util

import alexiil.mc.lib.net.NetByteBuf
import com.kneelawk.magicalmahou.MMLog
import com.kneelawk.magicalmahou.net.readVec3d
import com.kneelawk.magicalmahou.net.writeVec3d
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.RaycastContext
import net.minecraft.world.World

class SyncedRaycast private constructor(private val start: Vec3d, private val direction: Vec3d) {
    companion object {
        const val MAX_START_OFFSET_FACTOR = 1.5
        const val MAX_START_OFFSET_ADDEND = 1.0

        fun clientStartRaycast(): SyncedRaycast? {
            val mc = MinecraftClient.getInstance()
            val raycastEntity = mc.cameraEntity ?: return null
            val tickDelta = mc.tickDelta
            val start = raycastEntity.getCameraPosVec(tickDelta)
            val direction = raycastEntity.getRotationVec(tickDelta)

            return SyncedRaycast(start, direction)
        }

        fun serverReadRaycast(buf: NetByteBuf): SyncedRaycast {
            return SyncedRaycast(buf.readVec3d(), buf.readVec3d())
        }
    }

    fun write(buf: NetByteBuf) {
        buf.writeVec3d(start)
        buf.writeVec3d(direction)
    }

    fun raycast(raycastEntity: Entity, maxDistance: Double): BlockPos? {
        val entityPos = Vec3d(raycastEntity.x, raycastEntity.y + raycastEntity.standingEyeHeight, raycastEntity.z)
        val previousPos = Vec3d(raycastEntity.prevX, raycastEntity.prevY, raycastEntity.prevZ)
        val midPos = previousPos.add(entityPos).multiply(0.5)
        val maxStartOffset = previousPos.distanceTo(entityPos) * MAX_START_OFFSET_FACTOR + MAX_START_OFFSET_ADDEND
        if (!midPos.isInRange(start, maxStartOffset)) {
            MMLog.warn(
                "Entity ($raycastEntity) tried to raycast from an unknown location: ($start), should have been near ($entityPos)"
            )
            return null
        }

        val end = start.add(direction.multiply(maxDistance))
        val world = raycastEntity.world

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

        if (wouldSuffocate(world, resultPos) || wouldSuffocate(world, resultPos.up())) {
            return null
        }

        return resultPos
    }

    private fun wouldSuffocate(world: World, pos: BlockPos): Boolean {
        return world.getBlockState(pos).shouldSuffocate(world, pos)
    }
}