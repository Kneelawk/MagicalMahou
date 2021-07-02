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
import net.minecraft.text.LiteralText

class CatEarsScreenHandler(syncId: Int, inventory: PlayerInventory) :
    SyncedGuiDescription(MMScreenHandlers.CAT_EARS, syncId, inventory) {
    companion object {
        private val NET_PARENT =
            McNetworkStack.SCREEN_HANDLER.subType(CatEarsScreenHandler::class.java, str("cat_ears"))

        private val ID_OPEN_CRYSTAL_BALL = NET_PARENT.idSignal("OPEN_CRYSTAL_BALL").setC2SReceiver { ctx ->
            ctx.connection.player.openHandledScreen(MMScreenHandlers.createCrystalBallScreenHandlerFactory())
        }

        private val ID_SET_CAT_EARS_ENABLED = NET_PARENT.idData("SET_CAT_EARS_ENABLED").setC2SReceiver { buf, _ ->
            component.serverSetCatEarsEnabled(buf.readBoolean())
        }
    }

    private val component = MMComponents.CAT_EARS[inventory.player]
    private val enabledButton: WScalableButton

    init {
        titleAlignment = HorizontalAlignment.CENTER
        val root = rootPanel as WGridPanel

        val backButton = WScalableButton(LiteralText("<-"))
        root.add(backButton, 0, 0, 1, 1)
        backButton.onClick = {
            MMProxy.getProxy().presetCursorPosition()
            ID_OPEN_CRYSTAL_BALL.send(CoreMinecraftNetUtil.getClientConnection(), this)
        }

        enabledButton = WScalableButton(gui("cat_ears.enabled", enabled(component.isCatEarsActuallyEnabled())))
        root.add(enabledButton, 0, 2, 9, 1)
        enabledButton.onClick = {
            ID_SET_CAT_EARS_ENABLED.send(CoreMinecraftNetUtil.getClientConnection(), this) { _, buf, ctx ->
                ctx.assertClientSide()
                buf.writeBoolean(!component.isCatEarsActuallyEnabled())
            }
        }

        root.validate(this)
    }

    fun s2cReceiveCatEarsEnabled(enabled: Boolean) {
        enabledButton.label = gui("cat_ears.enabled", enabled(enabled))
    }
}