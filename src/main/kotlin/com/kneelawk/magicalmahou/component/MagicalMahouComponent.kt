package com.kneelawk.magicalmahou.component

import alexiil.mc.lib.net.ActiveConnection
import alexiil.mc.lib.net.impl.CoreMinecraftNetUtil
import com.kneelawk.magicalmahou.MMConstants.str
import com.kneelawk.magicalmahou.MMLog
import com.kneelawk.magicalmahou.client.particle.MMParticlesClient
import com.kneelawk.magicalmahou.component.ComponentHelper.withScreenHandler
import com.kneelawk.magicalmahou.net.*
import com.kneelawk.magicalmahou.proxy.MMProxy
import com.kneelawk.magicalmahou.screenhandler.CrystalBallScreenHandler
import com.kneelawk.magicalmahou.skin.InvalidSkinException
import com.kneelawk.magicalmahou.skin.PlayerSkinModel
import com.kneelawk.magicalmahou.skin.SkinManagers
import com.kneelawk.magicalmahou.skin.SkinUtils
import com.kneelawk.magicalmahou.util.SaveDirUtils
import com.kneelawk.magicalmahou.util.lazyVar
import dev.onyxstudios.cca.api.v3.component.CopyableComponent
import dev.onyxstudios.cca.api.v3.component.tick.ClientTickingComponent
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import java.util.*

/**
 * # MagicalMahou General Component
 * This component contains information about a magical player to be synced with all players.
 *
 * ## This information includes:
 * * Is this player even magical?
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
 *         * `ID_S2C_FULL_SYNC` in the reader.
 *         * `ID_S2C_FULL_WITHOUT_SKIN_SYNC` in the reader.
 *         * `ID_C2S_PLAYER_SKIN_SYNC` in the reader.
 *         * `ID_S2C_FULL_WITHOUT_SKIN_SYNC` in the `sendFullWithoutSkinSync(ActiveConnection, Boolean)` writer.
 *         * `ID_S2C_FULL_SYNC` in the `sendFullSync(ActiveConnection, Boolean, ByteArray)` writer.
 *         * `ID_S2C_FULL_SYNC` in the `sendFullSync(ActiveConnection, Boolean)` writer.
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
            MMLog.debug("Received C2S_REQUEST_SYNC packet")
            syncTo(ctx.connection, false)
        }

        /**
         * Used to sync a player's data from the server to the client. This is sent when the client requests as long as
         * the server has a valid transformed skin for this component's player. This likely also happens when a player
         * transforms, hopefully getting rid of the really bad desync.
         */
        private val ID_S2C_FULL_SYNC = NET_PARENT.idData("S2C_FULL_SYNC").setS2CReceiver { buf, _ ->
            MMLog.debug("Received S2C_FULL_SYNC packet")
            val previousTransformed = isActuallyTransformed()

            isMagical = true
            isTransformed = buf.readBoolean()
            val displayTransform = buf.readBoolean()

            if (displayTransform && isActuallyTransformed() && !previousTransformed) {
                displayTransform()
            }

            playerSkinManager.loadPNGFromBytes(buf.readByteArray(65536), playerId)
            playerSkinManager.update(playerId)
            playerSkinModel = PlayerSkinModel.byId(buf.readByte().toInt())
            transformationColor = buf.readInt()
        }

        /**
         * Used to sync a player's data (without skin) from the server to the client. This is sent when the client
         * requests but when the server does not have the associated player's transformed skin. If the client receiving
         * this is the client of the associated player, then the client should send their skin to the server.
         */
        private val ID_S2C_FULL_WITHOUT_SKIN_SYNC =
            NET_PARENT.idData("S2C_FULL_WITHOUT_SKIN_SYNC").setS2CReceiver { buf, ctx ->
                MMLog.debug("Received S2C_FULL_WITHOUT_SKIN_SYNC packet")
                val previousTransformed = isActuallyTransformed()

                isMagical = true
                isTransformed = buf.readBoolean()
                val displayTransform = buf.readBoolean()

                if (displayTransform && !previousTransformed && isActuallyTransformed()) {
                    displayTransform()
                }

                transformationColor = buf.readInt()

                // this is only run on the client
                val clientPlayer = MinecraftClient.getInstance().player

                // are we syncing this client's player or someone else's player
                if (provider == clientPlayer) {
                    ID_C2S_PLAYER_SKIN_SYNC.send(ctx.connection, this)
                }
            }

        /**
         * Sent by the server to indicate that the player is not magical.
         */
        private val ID_S2C_NON_MAGICAL_SYNC = NET_PARENT.idSignal("S2C_NON_MAGICAL_SYNC").setS2CReceiver {
            MMLog.debug("Received S2C_NON_MAGICAL_SYNC packet")
            isMagical = false
            isTransformed = false
        }

        /**
         * Used to sync a player's transformed skin to the server, either because the server requested it or because the
         * player updated their transformed skin.
         */
        private val ID_C2S_PLAYER_SKIN_SYNC = NET_PARENT.idData("C2S_PLAYER_SKIN_SYNC").setC2SReadWrite({ buf, ctx ->
            MMLog.debug("Received C2S_PLAYER_SKIN_SYNC packet")
            if (isMagical) {
                try {
                    playerSkinManager.loadPNGFromBytes(buf.readByteArray(65536), playerId)
                    playerSkinManager.update(playerId)
                    playerSkinModel = PlayerSkinModel.byId(buf.readByte().toInt())
                    needsC2SSkinSync = false

                    // Send skin to other players
                    syncToEveryone(exceptSelf = true, displayTransform = false)
                } catch (e: InvalidSkinException) {
                    when (e) {
                        is InvalidSkinException.BadImage -> MMLog.warn(
                            "Error receiving C2S_PLAYER_SKIN_SYNC packet", e
                        )
                        is InvalidSkinException.WrongDimensions -> MMLog.warn(
                            "Received C2S_PLAYER_SKIN_SYNC packet with wrong dimensions. Should be (${e.requiredWidth}x${e.requiredHeight}) but were (${e.requiredWidth}x${e.providedHeight})",
                            e
                        )
                    }
                }
            } else {
                ID_S2C_NON_MAGICAL_SYNC.send(ctx.connection, this)
            }
        }, { buf, _ ->
            buf.writeByteArray(playerSkinManager.storePNGToBytes(playerId))
            buf.writeByte(playerSkinModel.id)
        })


        /* Action Packets */

        /**
         * Sent by the client when the client wants the player to transform.
         */
        private val ID_C2S_REQUEST_TRANSFORM = NET_PARENT.idData("C2S_REQUEST_TRANSFORM").setC2SReceiver { buf, ctx ->
            MMLog.debug("Received C2S_REQUEST_TRANSFORM packet")
            if (isMagical) {
                // TODO: packet throttling
                isTransformed = buf.readBoolean()

                // Send the transform and sync to all clients
                syncToEveryone(exceptSelf = false, displayTransform = true)
            } else {
                ID_S2C_NON_MAGICAL_SYNC.send(ctx.connection, this)
            }
        }

        /**
         * Sent by the client when the client wants to change the player's player skin model.
         */
        private val ID_C2S_SET_PLAYER_SKIN_MODEL =
            NET_PARENT.idData("C2S_SET_PLAYER_SKIN_MODEL").setC2SReceiver { buf, ctx ->
                MMLog.debug("Received C2S_SET_PLAYER_SKIN_MODEL packet")
                if (isMagical) {
                    playerSkinModel = PlayerSkinModel.byId(buf.readByte().toInt())

                    for (conn in CoreMinecraftNetUtil.getPlayersWatching(provider.world, provider.blockPos)) {
                        ID_S2C_SET_PLAYER_SKIN_MODEL.send(conn, this)
                    }
                } else {
                    ID_S2C_NON_MAGICAL_SYNC.send(ctx.connection, this)
                }
            }

        /**
         * Sent by the server when the server has deemed that a player's transformed player skin model should be
         * updated.
         */
        private val ID_S2C_SET_PLAYER_SKIN_MODEL = NET_PARENT.idData("S2C_SET_PLAYER_SKIN_MODEL").setS2CReadWrite(
            { buf, _ ->
                MMLog.debug("Received S2C_SET_PLAYER_SKIN_MODEL packet")
                playerSkinModel = PlayerSkinModel.byId(buf.readByte().toInt())
            },
            { buf, _ -> buf.writeByte(playerSkinModel.id) }
        )

        /**
         * Sent by the client when the client wants to change the player's transformation color.
         */
        private val ID_C2S_SET_TRANSFORMATION_COLOR =
            NET_PARENT.idData("C2S_SET_TRANSFORMATION_COLOR").setC2SReceiver { buf, ctx ->
                MMLog.debug("Received C2S_SET_TRANSFORMATION_COLOR packet")
                if (isMagical) {
                    transformationColor = buf.readInt()

                    for (conn in CoreMinecraftNetUtil.getPlayersWatching(provider.world, provider.blockPos)) {
                        ID_S2C_SET_TRANSFORMATION_COLOR.send(conn, this)
                    }
                } else {
                    ID_S2C_NON_MAGICAL_SYNC.send(ctx.connection, this)
                }
            }

        /**
         * Sent by the server when the server has deemed that the player's transformation color should be updated.
         */
        private val ID_S2C_SET_TRANSFORMATION_COLOR = NET_PARENT.idData("S2C_SET_TRANSFORMATION_COLOR").setS2CReadWrite(
            { buf, _ ->
                MMLog.debug("Received S2C_SET_TRANSFORMATION_COLOR packet")
                transformationColor = buf.readInt()
            },
            { buf, _ -> buf.writeInt(transformationColor) }
        )
    }

    override val key = MMComponents.GENERAL

    /* Internal State */

    private val playerSkinManager = SkinManagers.getPlayerSkinManger(provider.world)
    private val playerId: UUID by lazy {
        // Player ID is not valid on construction
        MMLog.debug("Setting player ${provider.gameProfile.name} id: ${provider.uuid}")
        provider.uuid
    }

    /* Initialization Tracking */

    private var syncRequestSent = false
    private var dataInitialized = false

    /* Component State */

    var isMagical = false
        private set(value) {
            field = value

            // update ui
            withScreenHandler<CrystalBallScreenHandler>(provider) {
                it.s2cReceiveIsMagicalChange(value)
            }
        }
    private var isTransformed = false

    // On the server, we always start by assuming that the player's skin is invalid. Then, if we load the player's skin
    // from a file, we can say that the skin is now valid.
    private var needsC2SSkinSync = !provider.world.isClient

    var playerSkinModel by lazyVar({
        // We can't do this in the constructor because the network manager wouldn't have been initialized by then and we
        // can't do this in the `tick()` function because the player skin might not have been loaded by then.
        MMProxy.getProxy().getDefaultPlayerSkinModel(provider)
    }, { value ->
        // update ui
        withScreenHandler<CrystalBallScreenHandler>(provider) {
            it.s2cReceiveSkinModelChange(value)
        }
    })
        private set

    var transformationColor = 0xFFFFFFFF.toInt()
        private set


    /* Usable Methods */

    fun isActuallyTransformed(): Boolean {
        return isMagical && isTransformed
    }

    /**
     * Used on the client-side to request a transformation.
     */
    fun clientRequestTransform(transformed: Boolean) {
        if (isMagical) {
            ID_C2S_REQUEST_TRANSFORM.send(CoreMinecraftNetUtil.getClientConnection(), this) { _, buf, ctx ->
                ctx.assertClientSide()
                buf.writeBoolean(transformed)
            }
        }
    }

    /**
     * Used on the client-side to send an updated skin to the server.
     */
    fun clientSendSkinUpdate() {
        if (isMagical) {
            ID_C2S_PLAYER_SKIN_SYNC.send(CoreMinecraftNetUtil.getClientConnection(), this)
        }
    }

    /**
     * Used on the client-side to send an updated player model type.
     */
    fun clientSetPlayerSkinModel(newPlayerSkinModel: PlayerSkinModel) {
        if (isMagical) {
            ID_C2S_SET_PLAYER_SKIN_MODEL.send(CoreMinecraftNetUtil.getClientConnection(), this) { _, buf, ctx ->
                ctx.assertClientSide()
                buf.writeByte(newPlayerSkinModel.id)
            }
        }
    }

    fun clientSetTransformationColor(newTransformationColor: Int) {
        if (isMagical) {
            ID_C2S_SET_TRANSFORMATION_COLOR.send(CoreMinecraftNetUtil.getClientConnection(), this) { _, buf, ctx ->
                ctx.assertClientSide()
                buf.writeInt(newTransformationColor)
            }
        }
    }

    /**
     * Used on the server-side to make a player magical.
     */
    fun serverMakeMagical() {
        isMagical = true
        isTransformed = true
        syncToEveryone(exceptSelf = false, displayTransform = true)
    }


    /* Internal Methods */

    /**
     * Causes the client to display the particle effects associated with a transformation.
     */
    private fun displayTransform() {
        MMParticlesClient.addTransformationParticles(provider, 0.5, 3, transformationColor)
    }


    /* Component Methods */

    /**
     * Tick this component on the client-side. Currently, this just used to have the client ask the server for a full
     * sync once the component has been fully initialized.
     */
    override fun clientTick() {
        // Make sure tick() is called
        super.clientTick()

        if (!syncRequestSent) {
            syncRequestSent = true

            // Once everything is set up here, let us send a sync request to the server.
            ID_C2S_REQUEST_SYNC.send(CoreMinecraftNetUtil.getClientConnection(), this)
        }
    }

    /**
     * Common component tick. This is used for initializing things that can't be initialized in the constructor.
     */
    override fun tick() {
        if (!dataInitialized) {
            dataInitialized = true

            // initialize data here
        }
    }

    override fun readFromNbt(tag: NbtCompound) {
        // We're loading our data from NBT so no need to initialize it in the common tick
        dataInitialized = true

        isMagical = if (tag.contains("magical")) {
            tag.getBoolean("magical")
        } else {
            false
        }

        // Even though we are not currently magical, we may have once been magical. Save and load that stuff on the
        // server just as normal.

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
        MMLog.debug("Loaded player skin model: $playerSkinModel")

        // If we've never been magical before and thus never had a valid skin before, we should know that.
        if (tag.contains("needsC2SSkinSync")) {
            needsC2SSkinSync = needsC2SSkinSync && tag.getBoolean("needsC2SSkinSync")
        }

        transformationColor = if (tag.contains("transformationColor")) {
            tag.getInt("transformationColor")
        } else {
            0xFFFFFFFF.toInt()
        }
    }

    override fun writeToNbt(tag: NbtCompound) {
        tag.putBoolean("magical", isMagical)

        // Again, even if we're not magical, we may have been once.

        tag.putBoolean("transformed", isTransformed)

        val idStr = SaveDirUtils.getPlayerIdStr(provider)
        tag.putString("skinIdStr", idStr)

        // Only save the skin if we know it's valid
        if (!needsC2SSkinSync) {
            SkinUtils.storePlayerSkin(idStr, playerId, provider.world)
        }

        MMLog.debug("Writing player skin model: $playerSkinModel")
        tag.putString("playerSkinModel", playerSkinModel.modelStr)

        // If we've never been magical and thus never had a valid skin, we should keep track of that.
        tag.putBoolean("needsC2SSkinSync", needsC2SSkinSync)

        tag.putInt("transformationColor", transformationColor)
    }

    override fun copyFrom(other: MagicalMahouComponent) {
        // initialization tracking
        syncRequestSent = other.syncRequestSent
        dataInitialized = other.dataInitialized

        // TODO: investigate whether to copy listeners (in theory, the player shouldn't be dying or anything while they
        //  have a gui open and still end up needing notifications)

        // data
        isMagical = other.isMagical
        isTransformed = other.isTransformed
        needsC2SSkinSync = other.needsC2SSkinSync
        playerSkinModel = other.playerSkinModel
        transformationColor = other.transformationColor
    }


    /* Synchronization Methods */

    /**
     * Used to sync everything so an individual client. If this player is not magical, then it sends the non-magical
     * packet. If this player is missing a skin on the server side, then it sends the missing-skin packet. If this
     * player has everything, it sends the full-sync packet.
     */
    private fun syncTo(conn: ActiveConnection, displayTransform: Boolean) {
        if (isMagical) {
            if (needsC2SSkinSync) {
                sendFullWithoutSkinSync(conn, displayTransform)
            } else {
                sendFullSync(conn, displayTransform)
            }
        } else {
            ID_S2C_NON_MAGICAL_SYNC.send(conn, this)
        }
    }

    /**
     * Used to sync player status when no skin is available.
     */
    private fun syncWithoutSkinTo(conn: ActiveConnection, displayTransform: Boolean) {
        if (isMagical) {
            sendFullWithoutSkinSync(conn, displayTransform)
        } else {
            ID_S2C_NON_MAGICAL_SYNC.send(conn, this)
        }
    }

    /**
     * Syncs this server-side component to all client-side components in range, optionally excluding the client of the
     * player to which this component belongs.
     */
    private fun syncToEveryone(exceptSelf: Boolean, displayTransform: Boolean) {
        val playersWatching = CoreMinecraftNetUtil.getPlayersWatching(provider.world, provider.blockPos)

        if (needsC2SSkinSync) {
            for (conn in playersWatching) {
                if (conn.player == provider) {
                    if (!exceptSelf) {
                        syncWithoutSkinTo(conn, displayTransform)
                    }
                } else {
                    syncWithoutSkinTo(conn, displayTransform)
                }
            }
        } else {
            val playerSkinBytes = playerSkinManager.storePNGToBytes(playerId)

            for (conn in playersWatching) {
                if (conn.player == provider) {
                    if (!exceptSelf) {
                        sendFullSync(conn, displayTransform, playerSkinBytes)
                    }
                } else {
                    sendFullSync(conn, displayTransform, playerSkinBytes)
                }
            }
        }
    }

    /**
     * Serializes and sends a default S2C_FULL_WITHOUT_SKIN_SYNC packet.
     */
    private fun sendFullWithoutSkinSync(conn: ActiveConnection, displayTransform: Boolean) {
        ID_S2C_FULL_WITHOUT_SKIN_SYNC.send(conn, this) { _, buf, ctx ->
            ctx.assertServerSide()
            buf.writeBoolean(isTransformed)
            buf.writeBoolean(displayTransform)
            buf.writeInt(transformationColor)
        }
    }

    /**
     * Used to sync a full skin to an individual client.
     */
    private fun sendFullSync(conn: ActiveConnection, displayTransform: Boolean, playerSkinBytes: ByteArray) {
        ID_S2C_FULL_SYNC.send(conn, this) { _, buf, ctx ->
            ctx.assertServerSide()
            buf.writeBoolean(isTransformed)
            buf.writeBoolean(displayTransform)
            buf.writeByteArray(playerSkinBytes)
            buf.writeByte(playerSkinModel.id)
            buf.writeInt(transformationColor)
        }
    }

    /**
     * Serializes and sends a default S2C_FULL_SYNC packet.
     */
    private fun sendFullSync(conn: ActiveConnection, displayTransform: Boolean) {
        ID_S2C_FULL_SYNC.send(conn, this) { _, buf, ctx ->
            ctx.assertServerSide()
            buf.writeBoolean(isTransformed)
            buf.writeBoolean(displayTransform)
            buf.writeByteArray(playerSkinManager.storePNGToBytes(playerId))
            buf.writeByte(playerSkinModel.id)
            buf.writeInt(transformationColor)
        }
    }
}