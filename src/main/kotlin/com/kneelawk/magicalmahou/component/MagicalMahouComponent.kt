package com.kneelawk.magicalmahou.component

import alexiil.mc.lib.net.ActiveConnection
import alexiil.mc.lib.net.impl.CoreMinecraftNetUtil
import com.kneelawk.magicalmahou.MMConstants.str
import com.kneelawk.magicalmahou.MMLog
import com.kneelawk.magicalmahou.image.InvalidImageException
import com.kneelawk.magicalmahou.image.SkinManagers
import com.kneelawk.magicalmahou.image.SkinUtils
import com.kneelawk.magicalmahou.net.MMNetIds
import com.kneelawk.magicalmahou.net.setC2SReadWrite
import com.kneelawk.magicalmahou.net.setC2SReceiver
import com.kneelawk.magicalmahou.net.setS2CReadWrite
import dev.onyxstudios.cca.api.v3.component.CopyableComponent
import dev.onyxstudios.cca.api.v3.component.tick.ClientTickingComponent
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound

/**
 * This component contains information about a magical player to be synced with all players.
 *
 * This information includes:
 * * Is this player transformed?
 * * What is this player's transformed skin?
 * * Is this player's transformed skin using the "slim" model or the "default" model?
 * * What is this player's transformation particle color?
 */
class MagicalMahouComponent(override val provider: PlayerEntity) : ProvidingPlayerComponent<MagicalMahouComponent>,
    CopyableComponent<MagicalMahouComponent>, ClientTickingComponent {
    companion object {
        private val NET_PARENT =
            MMNetIds.PLAYER_COMPONENT_NET_ID.subType(MagicalMahouComponent::class.java, str("component_general"))


        /* Sync Packets */

        /**
         * Used by the client to ask the server to sync this component's data to this client.
         */
        private val ID_C2S_REQUEST_SYNC = NET_PARENT.idSignal("C2S_REQUEST_SYNC").setC2SReceiver { ctx ->
            println("Received C2S_REQUEST_SYNC packet")
            syncTo(ctx.connection)
        }

        /**
         * Used to sync a player's data from the server to the client. This is sent when the client requests as long as
         * the server has a valid transformed skin for this component's player. This likely also happens when a player
         * transforms, hopefully getting rid of the really bad desync.
         */
        private val ID_S2C_FULL_SYNC = NET_PARENT.idData("S2C_FULL_SYNC").setS2CReadWrite({ buf, _ ->
            println("Received S2C_FULL_SYNC packet")
            isTransformed = buf.readBoolean()
            playerSkinManager.loadPNGFromBytes(buf.readByteArray(65536), playerId)
            playerSkinManager.update(playerId)
        }, { buf, _ ->
            buf.writeBoolean(isTransformed)
            buf.writeByteArray(playerSkinManager.storePNGToBytes(playerId))
        })

        /**
         * Used to sync a player's data (without skin) from the server to the client. This is sent when the client
         * requests but when the server does not have the associated player's transformed skin. If the client receiving
         * this is the client of the associated player, then the client should send their skin to the server.
         */
        private val ID_S2C_FULL_WITHOUT_SKIN_SYNC =
            NET_PARENT.idData("S2C_FULL_WITHOUT_SKIN_SYNC").setS2CReadWrite({ buf, ctx ->
                println("Received S2C_FULL_WITHOUT_SKIN_SYNC packet")
                isTransformed = buf.readBoolean()

                // this is only run on the client
                val clientPlayer = MinecraftClient.getInstance().player

                // are we syncing this client's player or someone else's player
                if (provider == clientPlayer) {
                    ID_C2S_PLAYER_SKIN_SYNC.send(ctx.connection, this)
                }
            }, { buf, _ -> buf.writeBoolean(isTransformed) })

        /**
         * Used to sync a player's transformed skin to the server, either because the server requested it or because the
         * player updated their transformed skin.
         */
        private val ID_C2S_PLAYER_SKIN_SYNC = NET_PARENT.idData("C2S_PLAYER_SKIN_SYNC").setC2SReadWrite({ buf, _ ->
            println("Received C2S_PLAYER_SKIN_SYNC packet")
            try {
                playerSkinManager.loadPNGFromBytes(buf.readByteArray(65536), playerId)
                playerSkinManager.update(playerId)
                needsC2SSkinSync = false

                // Send skin to other players
                syncToEveryone(true)
            } catch (e: InvalidImageException) {
                when (e) {
                    is InvalidImageException.BadImage -> MMLog.warn("Error receiving C2S_PLAYER_SKIN_SYNC packet", e)
                    is InvalidImageException.WrongDimensions -> MMLog.warn(
                        "Received C2S_PLAYER_SKIN_SYNC packet with wrong dimensions. Should be (${e.requiredWidth}x${e.requiredHeight}) but were (${e.requiredWidth}x${e.providedHeight})",
                        e
                    )
                }
            }
        }, { buf, _ -> buf.writeByteArray(playerSkinManager.storePNGToBytes(playerId)) })


        /* Action Packets */

        private val ID_C2S_REQUEST_TRANSFORM = NET_PARENT.idData("C2S_REQUEST_TRANSFORM").setC2SReceiver { buf, _ ->
            println("Received C2S_REQUEST_TRANSFORM packet")
            // TODO: packet throttling
            isTransformed = buf.readBoolean()

            // Send the transform and sync to all clients
            syncToEveryone(false)
        }
    }

    override val key = MMComponents.GENERAL

    /* Internal State */

    private val playerSkinManager = SkinManagers.getPlayerSkinManger(provider.world)
    private val playerId = provider.uuid
    private var clientTickInitialized = false

    /* Component State */

    var isTransformed = false
        private set

    // On the server, we always start by assuming that the player's skin is invalid. Then, if we load the player's skin
    // from a file, we can say that the skin is now valid.
    private var needsC2SSkinSync = !provider.world.isClient


    /* Usable Methods */

    /**
     * Used on the client-side to request a transformation.
     */
    fun clientRequestTransform(transformed: Boolean) {
        ID_C2S_REQUEST_TRANSFORM.send(CoreMinecraftNetUtil.getClientConnection(), this) { _, buf, ctx ->
            ctx.assertClientSide()
            buf.writeBoolean(transformed)
        }
    }


    /* Component Methods */

    /**
     * Tick this component on the client-side. Currently, this just used to have the client ask the server for a full
     * sync once the component has been fully initialized.
     */
    override fun clientTick() {
        if (!clientTickInitialized) {
            clientTickInitialized = true

            // Make sure we have a texture on the client-side so as to not lag when we need to access it
            playerSkinManager.ensureExists(playerId)

            // Once everything is set up here, let us send a sync request to the server.
            ID_C2S_REQUEST_SYNC.send(CoreMinecraftNetUtil.getClientConnection(), this)
        }
    }

    override fun readFromNbt(tag: NbtCompound) {
        isTransformed = tag.getBoolean("transformed")
        val previousPlayerId = if (tag.contains("playerId")) {
            tag.getUuid("playerId")
        } else {
            playerId
        }

        needsC2SSkinSync = !SkinUtils.loadPlayerSkin(previousPlayerId, provider.world)
        playerSkinManager.update(playerId)
    }

    override fun writeToNbt(tag: NbtCompound) {
        tag.putBoolean("transformed", isTransformed)

        // Only save the skin if we know it's valid
        if (!needsC2SSkinSync) {
            SkinUtils.storePlayerSkin(playerId, provider.world)
        }
    }

    override fun copyFrom(other: MagicalMahouComponent) {
        isTransformed = other.isTransformed
        needsC2SSkinSync = other.needsC2SSkinSync
    }


    /* Synchronization Methods */

    /**
     * Used to sync everything so an individual client. If this
     */
    private fun syncTo(conn: ActiveConnection) {
        if (needsC2SSkinSync) {
            ID_S2C_FULL_WITHOUT_SKIN_SYNC.send(conn, this)
        } else {
            ID_S2C_FULL_SYNC.send(conn, this)
        }
    }

    private fun syncTo(conn: ActiveConnection, playerSkinBytes: ByteArray) {
        ID_S2C_FULL_SYNC.send(conn, this) { _, buf, ctx ->
            ctx.assertServerSide()
            buf.writeBoolean(isTransformed)
            buf.writeByteArray(playerSkinBytes)
        }
    }

    private fun syncToEveryone(exceptSelf: Boolean) {
        val playerSkinBytes = playerSkinManager.storePNGToBytes(playerId)

        for (conn in CoreMinecraftNetUtil.getPlayersWatching(provider.world, provider.blockPos)) {
            if (conn.player == provider) {
                if (!exceptSelf) {
                    syncTo(conn, playerSkinBytes)
                }
            } else {
                syncTo(conn, playerSkinBytes)
            }
        }
    }
}