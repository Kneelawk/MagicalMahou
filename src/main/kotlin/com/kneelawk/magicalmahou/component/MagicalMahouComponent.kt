package com.kneelawk.magicalmahou.component

import dev.onyxstudios.cca.api.v3.component.Component
import dev.onyxstudios.cca.api.v3.component.CopyableComponent
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity

/**
 * This component contains information about a magical player to be synced with all players.
 *
 * This information includes:
 * * Is this player transformed.
 * * What is this player's current transformed skin.
 * * What is this player's transformation particle color.
 */
class MagicalMahouComponent : Component, CopyableComponent<MagicalMahouComponent>, AutoSyncedComponent {
    private var isTransformed = false

    override fun readFromNbt(tag: NbtCompound) {

    }

    override fun writeToNbt(tag: NbtCompound) {

    }

    override fun copyFrom(other: MagicalMahouComponent) {

    }

    override fun writeSyncPacket(buf: PacketByteBuf, recipient: ServerPlayerEntity) {
        super.writeSyncPacket(buf, recipient)
    }

    override fun shouldSyncWith(player: ServerPlayerEntity): Boolean {
        return super.shouldSyncWith(player)
    }

    override fun applySyncPacket(buf: PacketByteBuf) {
        super.applySyncPacket(buf)
    }

    enum class SyncType {
        FULL
    }
}