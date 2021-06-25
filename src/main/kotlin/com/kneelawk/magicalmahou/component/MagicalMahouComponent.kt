package com.kneelawk.magicalmahou.component

import com.kneelawk.magicalmahou.image.MMImageUtils
import dev.onyxstudios.cca.api.v3.component.Component
import dev.onyxstudios.cca.api.v3.component.CopyableComponent
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity

/**
 * This component contains information about a magical player to be synced with all players.
 *
 * This information includes:
 * * Is this player transformed?
 * * What is this player's transformed skin?
 * * Is this player's transformed skin using the "slim" model or the "default" model?
 * * What is this player's transformation particle color?
 */
class MagicalMahouComponent(private val provider: PlayerEntity) : Component, CopyableComponent<MagicalMahouComponent>, AutoSyncedComponent {
    private var isTransformed = false
    // TODO: Consider whether I should be using references here and storing the skins elsewhere
    private val transformedSkin = MMImageUtils.IMAGE_FACTORY.default(provider.uuid)

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