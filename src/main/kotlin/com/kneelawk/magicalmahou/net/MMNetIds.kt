package com.kneelawk.magicalmahou.net

import alexiil.mc.lib.net.*
import alexiil.mc.lib.net.impl.ActiveMinecraftConnection
import alexiil.mc.lib.net.impl.McNetworkStack
import com.kneelawk.magicalmahou.MMConstants
import com.kneelawk.magicalmahou.component.ProvidingPlayerComponent
import dev.onyxstudios.cca.api.v3.component.ComponentKey
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry
import net.minecraft.entity.player.PlayerEntity

object MMNetIds {
    val COMPONENT_KEY_CACHE = NetObjectCache.createMappedIdentifier<ComponentKey<*>>(
        McNetworkStack.ROOT.child(MMConstants.str("cache_component")), { key -> key.id },
        { id -> ComponentRegistry.get(id) })

    val PLAYER_COMPONENT_NET_ID = object : ParentNetIdSingle<ProvidingPlayerComponent<*>>(
        McNetworkStack.ROOT, ProvidingPlayerComponent::class.java, MMConstants.str("player-component"), -1
    ) {
        override fun readContext(buffer: NetByteBuf, ctx: IMsgReadCtx): ProvidingPlayerComponent<*>? {
            val mcConn = ctx.connection as ActiveMinecraftConnection
            val connPlayer = mcConn.player
            val player = connPlayer.world.getEntityById(buffer.readInt())

            if (player !is PlayerEntity) {
                return null
            }

            val componentKey = COMPONENT_KEY_CACHE.getObj(ctx.connection, buffer.readVarUnsignedInt()) ?: return null

            return componentKey.getNullable(player) as? ProvidingPlayerComponent<*>
        }

        override fun writeContext(buffer: NetByteBuf, ctx: IMsgWriteCtx, value: ProvidingPlayerComponent<*>) {
            buffer.writeInt(value.provider.id)
            buffer.writeVarUnsignedInt(COMPONENT_KEY_CACHE.getId(ctx.connection, value.key))
        }
    }
}