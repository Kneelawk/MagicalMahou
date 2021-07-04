package com.kneelawk.magicalmahou.util

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ChunkTicketType
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d

object TeleportUtils {
    fun serverTeleport(player: ServerPlayerEntity, pos: BlockPos, maxDistance: Double): Boolean {
        val oldPos = player.blockPos

        return if (oldPos.isWithinDistance(pos, maxDistance)) {
            val world = player.serverWorld
            val posD = Vec3d.ofBottomCenter(pos)

            val chunkPos = ChunkPos(pos)
            world.chunkManager.addTicket(ChunkTicketType.POST_TELEPORT, chunkPos, 1, player.id)

            player.stopRiding()
            if (player.isSleeping) {
                player.wakeUp(true, true)
            }

            player.networkHandler.requestTeleport(
                posD.x, posD.y, posD.z, MathHelper.wrapDegrees(player.yaw), MathHelper.wrapDegrees(player.pitch)
            )

            true
        } else {
            false
        }
    }
}