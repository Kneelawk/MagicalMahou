package com.kneelawk.magicalmahou.screenhandler

import alexiil.mc.lib.net.impl.CoreMinecraftNetUtil
import alexiil.mc.lib.net.impl.McNetworkStack
import com.kneelawk.magicalmahou.MMConstants.gui
import com.kneelawk.magicalmahou.MMConstants.str
import com.kneelawk.magicalmahou.component.MMComponents
import com.kneelawk.magicalmahou.net.setC2SReceiver
import com.kneelawk.magicalmahou.proxy.MMProxy
import com.kneelawk.magicalmahou.screenhandler.widget.WScalableButton
import com.kneelawk.magicalmahou.util.TextUtils.enabled
import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.LiteralTextContent
import net.minecraft.text.MutableText

class TeleportAtScreenHandler(syncId: Int, inventory: PlayerInventory) :
    SyncedGuiDescription(MMScreenHandlers.TELEPORT_AT, syncId, inventory) {
    companion object {
        private val NET_PARENT =
            McNetworkStack.SCREEN_HANDLER.subType(TeleportAtScreenHandler::class.java, str("teleport_at"))

        private val ID_OPEN_CRYSTAL_BALL = NET_PARENT.idSignal("OPEN_CRYSTAL_BALL").setC2SReceiver { ctx ->
            ctx.connection.player.openHandledScreen(MMScreenHandlers.createCrystalBallScreenHandlerFactory())
        }

        private val ID_SET_ENABLED = NET_PARENT.idData("SET_ENABLED").setC2SReceiver { buf, _ ->
            component.serverSetEnabled(buf.readBoolean())
        }
    }

    private val component = MMComponents.TELEPORT_AT[inventory.player]
    private val enabledButton: WScalableButton

    init {
        titleAlignment = HorizontalAlignment.CENTER
        val root = rootPanel as WGridPanel

        val backButton = WScalableButton(MutableText.of(LiteralTextContent("<-")))
        root.add(backButton, 0, 0, 1, 1)
        backButton.onClick = {
            MMProxy.getProxy().presetCursorPosition()
            ID_OPEN_CRYSTAL_BALL.send(CoreMinecraftNetUtil.getClientConnection(), this)
        }

        enabledButton = WScalableButton(gui("teleport_at.enabled", enabled(component.enabled)))
        root.add(enabledButton, 0, 2, 9, 1)
        enabledButton.onClick = {
            ID_SET_ENABLED.send(CoreMinecraftNetUtil.getClientConnection(), this) { _, buf, ctx ->
                ctx.assertClientSide()
                buf.writeBoolean(!component.enabled)
            }
        }

        root.validate(this)
    }

    fun s2cReceiveEnabled(enabled: Boolean) {
        enabledButton.label = gui("teleport_at.enabled", enabled(enabled))
    }
}
