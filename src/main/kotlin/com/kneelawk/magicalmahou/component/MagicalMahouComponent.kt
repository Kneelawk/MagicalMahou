package com.kneelawk.magicalmahou.component

import alexiil.mc.lib.net.ActiveConnection
import alexiil.mc.lib.net.impl.CoreMinecraftNetUtil
import com.kneelawk.magicalmahou.MMConstants.str
import com.kneelawk.magicalmahou.MMLog
import com.kneelawk.magicalmahou.image.InvalidImageException
import com.kneelawk.magicalmahou.image.PlayerSkinModel
import com.kneelawk.magicalmahou.image.SkinManagers
import com.kneelawk.magicalmahou.image.SkinUtils
import com.kneelawk.magicalmahou.net.MMNetIds
import com.kneelawk.magicalmahou.net.setC2SReadWrite
import com.kneelawk.magicalmahou.net.setC2SReceiver
import com.kneelawk.magicalmahou.net.setS2CReadWrite
import com.kneelawk.magicalmahou.proxy.MMProxy
import com.kneelawk.magicalmahou.util.SaveDirUtils
import dev.onyxstudios.cca.api.v3.component.CopyableComponent
import dev.onyxstudios.cca.api.v3.component.tick.ClientTickingComponent
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound

/**
 * # MagicalMahou General Component
 * This component contains information about a magical player to be synced with all players.
 *
 * ## This information includes:
 * * Is this player transformed?
 * * What is this player's transformed skin?
 * * Is this player's transformed skin using the "slim" model or the "default" model?
 * * What is this player's transformation particle color?
 *
 * ## Notes:
 *
 * ### When Adding New Data
 * 1. Add a field for the data.
 * 2. Add an initializer for the data. If the data's default state relies on the player, then it must be initialized
 *    in either `tick()` or `clientTick()` depending on what it is.
 * 3. Add NBT serialization and copying for the data. NBT serialization takes place in the `readFromNBT(...)` and
 *    `writeToNBT(...)` methods. Copying takes place in the `copyFrom(...)` method.
 * 4. Add synchronization for the data.
 *     1. Is the data skin related? If so, consider how it should be synchronized with the `S2C_FULL_SYNC` packet,
 *        the `S2C_FULL_WITHOUT_SKIN_SYNC` packet, and the `C2S_PLAYER_SKIN_SYNC` packet. (Should the default value
 *        be retrieved from the client if the server doesn't have a valid version?)
 *     2. Add synchronization to:
 *         * `ID_S2C_FULL_SYNC` in the read-writer.
 *         * `ID_S2C_FULL_WITHOUT_SKIN_SYNC` in the read-writer.
 *         * `ID_C2S_PLAYER_SKIN_SYNC` in the read-writer.
 *         * `ID_S2C_FULL_SYNC` in `syncTo(ActiveConnection, ByteArray)`.
 * 5. Consider edit events.
 *     1. Add a C2S packet for the event and a S2C packet for the event.
 *     2. Consider packet throttling and a rejection S2C packet.
 */
class MagicalMahouComponent(override val provider: PlayerEntity) : ProvidingPlayerComponent<MagicalMahouComponent>,
    CopyableComponent<MagicalMahouComponent>, ClientTickingComponent, CommonTickingComponent {

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
            playerSkinModel = PlayerSkinModel.byId(buf.readByte().toInt())
        }, { buf, _ ->
            buf.writeBoolean(isTransformed)
            buf.writeByteArray(playerSkinManager.storePNGToBytes(playerId))
            buf.writeByte(playerSkinModel.id)
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
                playerSkinModel = PlayerSkinModel.byId(buf.readByte().toInt())
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
        }, { buf, _ ->
            buf.writeByteArray(playerSkinManager.storePNGToBytes(playerId))
            buf.writeByte(playerSkinModel.id)
        })


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
    private var commonTickInitialized = false

    /* Component State */

    var isTransformed = false
        private set

    // On the server, we always start by assuming that the player's skin is invalid. Then, if we load the player's skin
    // from a file, we can say that the skin is now valid.
    private var needsC2SSkinSync = !provider.world.isClient

    var playerSkinModel: PlayerSkinModel = PlayerSkinModel.DEFAULT
        private set


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
        // Make sure tick() is called
        super.clientTick()

        if (!clientTickInitialized) {
            clientTickInitialized = true

            // Once everything is set up here, let us send a sync request to the server.
            ID_C2S_REQUEST_SYNC.send(CoreMinecraftNetUtil.getClientConnection(), this)
        }
    }

    /**
     * Common component tick. This is used for initializing things that can't be initialized in the constructor.
     */
    override fun tick() {
        if (!commonTickInitialized) {
            commonTickInitialized = true

            // Make sure we have a texture on the client-side so as to not lag when we need to access it
            playerSkinManager.ensureExists(playerId)

            // We can't do this in the constructor because the network manager wouldn't have been initialized by then
            playerSkinModel = MMProxy.getProxy().getDefaultPlayerSkinModel(provider)
        }
    }

    override fun readFromNbt(tag: NbtCompound) {
        isTransformed = if (tag.contains("transformed")) {
            tag.getBoolean("transformed")
        } else {
            false
        }

        val idStr = if (tag.contains("skinIdStr")) {
            tag.getString("skinIdStr")
        } else {
            SaveDirUtils.getPlayerIdStr(provider)
        }

        needsC2SSkinSync = !SkinUtils.loadPlayerSkin(idStr, playerId, provider.world)
        playerSkinManager.update(playerId)

        playerSkinModel = if (tag.contains("playerSkinModel")) {
            val playerSkinModelStr = tag.getString("playerSkinModel")
            val model = PlayerSkinModel.byModelStr(playerSkinModelStr)
            if (model == null) {
                MMLog.warn("Loaded invalid player skin model from NBT: $playerSkinModelStr")
                MMProxy.getProxy().getDefaultPlayerSkinModel(provider)
            } else {
                model
            }
        } else {
            MMProxy.getProxy().getDefaultPlayerSkinModel(provider)
        }
    }

    override fun writeToNbt(tag: NbtCompound) {
        tag.putBoolean("transformed", isTransformed)

        val idStr = SaveDirUtils.getPlayerIdStr(provider)
        tag.putString("skinIdStr", idStr)

        // Only save the skin if we know it's valid
        if (!needsC2SSkinSync) {
            SkinUtils.storePlayerSkin(idStr, playerId, provider.world)
        }

        tag.putString("playerSkinModel", playerSkinModel.modelStr)
    }

    override fun copyFrom(other: MagicalMahouComponent) {
        isTransformed = other.isTransformed
        needsC2SSkinSync = other.needsC2SSkinSync
        playerSkinModel = other.playerSkinModel
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
            buf.writeByte(playerSkinModel.id)
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