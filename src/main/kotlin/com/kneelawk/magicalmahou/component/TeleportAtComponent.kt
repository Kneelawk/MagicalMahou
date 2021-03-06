package com.kneelawk.magicalmahou.component

import alexiil.mc.lib.net.NetByteBuf
import alexiil.mc.lib.net.impl.CoreMinecraftNetUtil
import com.kneelawk.magicalmahou.MMConstants.str
import com.kneelawk.magicalmahou.MMConstants.tt
import com.kneelawk.magicalmahou.MMLog
import com.kneelawk.magicalmahou.component.ComponentHelper.withScreenHandler
import com.kneelawk.magicalmahou.icon.MMIcons
import com.kneelawk.magicalmahou.net.MMNetIds
import com.kneelawk.magicalmahou.net.setC2SReceiver
import com.kneelawk.magicalmahou.net.setS2CReceiver
import com.kneelawk.magicalmahou.screenhandler.TeleportAtScreenHandler
import com.kneelawk.magicalmahou.util.SyncedRaycast
import com.kneelawk.magicalmahou.util.TeleportUtils
import dev.onyxstudios.cca.api.v3.component.CopyableComponent
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent
import dev.onyxstudios.cca.api.v3.component.tick.ClientTickingComponent
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.entity.EntityStatuses
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf
import net.minecraft.particle.ParticleTypes
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text

class TeleportAtComponent(override val provider: PlayerEntity) : ProvidingPlayerComponent<TeleportAtComponent>,
    CopyableComponent<TeleportAtComponent>, MMAbilityComponent<TeleportAtComponent>, AutoSyncedComponent,
    NamedScreenHandlerFactory, ClientTickingComponent {

    companion object {
        val NAME = tt("component", "teleport_at")

        private const val DEFAULT_HAS = false
        private const val DEFAULT_ENABLED = false
        private const val TELEPORT_COOLDOWN = 10
        private const val MAX_TELEPORT_DISTANCE = 128.0

        private val NET_PARENT =
            MMNetIds.PLAYER_COMPONENT_NET_ID.subType(TeleportAtComponent::class.java, str("teleport_at"))

        private val ID_C2S_TELEPORT_TO = NET_PARENT.idData("C2S_TELEPORT_TO").setC2SReceiver { buf, ctx ->
            MMLog.debug("Received C2S_TELEPORT_TO packet")

            val now = provider.world.time
            if (isActuallyEnabled()) {
                if (now - lastTeleport > TELEPORT_COOLDOWN) {
                    lastTeleport = now

                    val oldPos = provider.pos

                    val raycast = SyncedRaycast.serverReadRaycast(buf)
                    val pos = raycast.raycast(provider, MAX_TELEPORT_DISTANCE)

                    if (pos != null && TeleportUtils.serverTeleport(
                            provider as ServerPlayerEntity, pos, MAX_TELEPORT_DISTANCE
                        )
                    ) {
                        provider.world.playSound(
                            null, oldPos.x, oldPos.y, oldPos.z, SoundEvents.ENTITY_ENDERMAN_TELEPORT,
                            SoundCategory.PLAYERS,
                            1.0F, 1.0F
                        )
                        provider.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F)
                        provider.world.sendEntityStatus(provider, EntityStatuses.ADD_PORTAL_PARTICLES)
                    } else {
                        ID_S2C_TELEPORT_REJECT.send(ctx.connection, this) { _, s2cBuf, s2cCtx ->
                            s2cCtx.assertServerSide()
                            s2cBuf.writeByte(RejectionType.NORMAL.id)
                        }
                    }
                } else {
                    buf.clear()
                    ID_S2C_TELEPORT_REJECT.send(ctx.connection, this) { _, s2cBuf, s2cCtx ->
                        s2cCtx.assertServerSide()
                        s2cBuf.writeByte(RejectionType.THROTTLE.id)
                    }
                }
            } else {
                buf.clear()
                // make sure the client knows that we're not enabled
                key.sync(provider)
                ID_S2C_TELEPORT_REJECT.send(ctx.connection, this) { _, s2cBuf, s2cCtx ->
                    s2cCtx.assertServerSide()
                    s2cBuf.writeByte(RejectionType.NORMAL.id)
                }
            }
        }

        private val ID_S2C_TELEPORT_REJECT = NET_PARENT.idData("S2C_TELEPORT_REJECT").setS2CReceiver { buf, _ ->
            MMLog.debug("Received S2C_TELEPORT_REJECT packet")
            provider.sendMessage(
                tt("message", "teleport_at.reject.${RejectionType.byId(buf.readByte().toInt()).errorName}"), true
            )
        }
    }

    override val name = NAME
    override val icon = MMIcons.TELEPORTATION_RINGS_ICON
    override val key = MMComponents.TELEPORT_AT

    private var lastTeleport: Long = provider.world.time

    private var has = DEFAULT_HAS
    var enabled = DEFAULT_ENABLED
        private set

    fun isActuallyEnabled(): Boolean {
        return has && enabled && MMComponents.GENERAL[provider].isActuallyTransformed()
    }

    fun serverSetEnabled(newEnabled: Boolean) {
        if (has) {
            enabled = newEnabled
            key.sync(provider)
        }
    }

    override fun serverGiveAbility() {
        if (MMComponents.isMagical(provider)) {
            has = true
            enabled = true
            key.sync(provider)
        }
    }

    @Environment(EnvType.CLIENT)
    fun clientTeleportAt() {
        val raycast = SyncedRaycast.clientStartRaycast()

        if (raycast == null) {
            provider.sendMessage(tt("message", "teleport_at.reject.${RejectionType.NORMAL.errorName}"), true)
            return
        }

        ID_C2S_TELEPORT_TO.send(CoreMinecraftNetUtil.getClientConnection(), this) { _, buf, ctx ->
            ctx.assertClientSide()
            raycast.write(buf)
        }
    }

    override fun clientTick() {
        if (isActuallyEnabled()) {
            provider.world.addParticle(
                ParticleTypes.PORTAL, provider.getParticleX(0.5), provider.getRandomBodyY(),
                provider.getParticleZ(0.5), (provider.random.nextDouble() - 0.5) * 2.0,
                -provider.random.nextDouble(), (provider.random.nextDouble() - 0.5) * 2.0
            )
        }
    }

    override fun getPlayerHasComponent(): Boolean {
        return has
    }

    override fun readFromNbt(tag: NbtCompound) {
        has = if (tag.contains("has")) {
            tag.getBoolean("has")
        } else {
            DEFAULT_HAS
        }
        enabled = if (tag.contains("enabled")) {
            tag.getBoolean("enabled")
        } else {
            DEFAULT_ENABLED
        }
    }

    override fun writeToNbt(tag: NbtCompound) {
        tag.putBoolean("has", has)
        tag.putBoolean("enabled", enabled)
    }

    override fun copyFrom(other: TeleportAtComponent) {
        has = other.has
        enabled = other.enabled
    }

    override fun writeSyncPacket(packetBuf: PacketByteBuf, recipient: ServerPlayerEntity) {
        val buf = NetByteBuf.asNetByteBuf(packetBuf)
        buf.writeBoolean(has)
        buf.writeBoolean(enabled)
    }

    override fun applySyncPacket(packetBuf: PacketByteBuf) {
        val buf = NetByteBuf.asNetByteBuf(packetBuf)
        has = buf.readBoolean()
        enabled = buf.readBoolean()

        // notify ui
        withScreenHandler<TeleportAtScreenHandler>(provider) {
            it.s2cReceiveEnabled(enabled)
        }
    }

    override fun createMenu(syncId: Int, inv: PlayerInventory, player: PlayerEntity): ScreenHandler {
        return TeleportAtScreenHandler(syncId, inv)
    }

    override fun getDisplayName(): Text {
        return NAME
    }

    private enum class RejectionType(val errorName: String) {
        NORMAL("normal"),
        THROTTLE("throttle");

        companion object {
            private val VALUES = values()
            private const val MIN_VALUE = 0
            private val MAX_VALUE = VALUES.size - 1

            fun byId(id: Int): RejectionType {
                return VALUES[id.coerceIn(MIN_VALUE, MAX_VALUE)]
            }
        }

        val id = ordinal
    }
}
