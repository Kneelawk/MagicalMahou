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

        private const val DEFAULT_HAS_CAT_EARS = true
        private const val DEFAULT_CAT_EARS_ENABLED = true

        // No NET_ID stuff here because, until cat ears can have custom skins, Cardinal Component's synchronization
        // system will do just fine.
    }

    override val key = MMComponents.CAT_EARS
    override val name = NAME
    override val icon = MMIcons.CAT_EARS_ICON

    private var hasCatEars = DEFAULT_HAS_CAT_EARS
    private var catEarsEnabled = DEFAULT_CAT_EARS_ENABLED

    fun isCatEarsActuallyEnabled(): Boolean {
        return hasCatEars && catEarsEnabled
    }

    fun serverSetCatEarsEnabled(newCatEarsEnabled: Boolean) {
        if (hasCatEars) {
            catEarsEnabled = newCatEarsEnabled
            key.sync(provider)
        }
    }

    override fun getPlayerHasComponent(): Boolean {
        return hasCatEars
    }

    override fun readFromNbt(tag: NbtCompound) {
        hasCatEars = if (tag.contains("hasCatEars")) {
            tag.getBoolean("hasCatEars")
        } else {
            DEFAULT_HAS_CAT_EARS
        }
        catEarsEnabled = if (tag.contains("catEarsEnabled")) {
            tag.getBoolean("catEarsEnabled")
        } else {
            DEFAULT_CAT_EARS_ENABLED
        }
    }

    override fun writeToNbt(tag: NbtCompound) {
        tag.putBoolean("hasCatEars", hasCatEars)
        tag.putBoolean("catEarsEnabled", catEarsEnabled)
    }

    override fun copyFrom(other: CatEarsComponent) {
        hasCatEars = other.hasCatEars
        catEarsEnabled = other.catEarsEnabled
    }

    override fun writeSyncPacket(packetBuf: PacketByteBuf, recipient: ServerPlayerEntity) {
        // Until cat ears can have custom skins, Cardinal Component's synchronization system will do just fine.
        val buf = NetByteBuf.asNetByteBuf(packetBuf)
        buf.writeBoolean(hasCatEars)
        buf.writeBoolean(catEarsEnabled)
    }

    override fun applySyncPacket(packetBuf: PacketByteBuf) {
        val buf = NetByteBuf.asNetByteBuf(packetBuf)
        hasCatEars = buf.readBoolean()
        catEarsEnabled = buf.readBoolean()

        // update ui
        withScreenHandler<CatEarsScreenHandler>(provider) {
            it.s2cReceiveCatEarsEnabled(isCatEarsActuallyEnabled())
        }
    }

    override fun createMenu(syncId: Int, inv: PlayerInventory, player: PlayerEntity): ScreenHandler {
        return CatEarsScreenHandler(syncId, inv)
    }

    override fun getDisplayName(): Text {
        return NAME
    }
}