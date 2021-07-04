package com.kneelawk.magicalmahou.component

import alexiil.mc.lib.net.NetByteBuf
import com.kneelawk.magicalmahou.MMConstants.tt
import com.kneelawk.magicalmahou.component.ComponentHelper.withScreenHandler
import com.kneelawk.magicalmahou.icon.MMIcons
import com.kneelawk.magicalmahou.screenhandler.CatEarsScreenHandler
import dev.onyxstudios.cca.api.v3.component.CopyableComponent
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

class CatEarsComponent(override val provider: PlayerEntity) : ProvidingPlayerComponent<CatEarsComponent>,
    CopyableComponent<CatEarsComponent>, MMAbilityComponent<CatEarsComponent>, AutoSyncedComponent,
    NamedScreenHandlerFactory {

    companion object {
        val NAME = tt("component", "cat_ears")

        private const val DEFAULT_HAS = true
        private const val DEFAULT_ENABLED = true

        // No NET_ID stuff here because, until cat ears can have custom skins, Cardinal Component's synchronization
        // system will do just fine.
    }

    override val key = MMComponents.CAT_EARS
    override val name = NAME
    override val icon = MMIcons.CAT_EARS_ICON

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

    override fun copyFrom(other: CatEarsComponent) {
        has = other.has
        enabled = other.enabled
    }

    override fun writeSyncPacket(packetBuf: PacketByteBuf, recipient: ServerPlayerEntity) {
        // Until cat ears can have custom skins, Cardinal Component's synchronization system will do just fine.
        val buf = NetByteBuf.asNetByteBuf(packetBuf)
        buf.writeBoolean(has)
        buf.writeBoolean(enabled)
    }

    override fun applySyncPacket(packetBuf: PacketByteBuf) {
        val buf = NetByteBuf.asNetByteBuf(packetBuf)
        has = buf.readBoolean()
        enabled = buf.readBoolean()

        // update ui
        withScreenHandler<CatEarsScreenHandler>(provider) {
            it.s2cReceiveEnabled(enabled)
        }
    }

    override fun createMenu(syncId: Int, inv: PlayerInventory, player: PlayerEntity): ScreenHandler {
        return CatEarsScreenHandler(syncId, inv)
    }

    override fun getDisplayName(): Text {
        return NAME
    }
}