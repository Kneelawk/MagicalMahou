package com.kneelawk.magicalmahou.screenhandler

import alexiil.mc.lib.net.impl.CoreMinecraftNetUtil
import alexiil.mc.lib.net.impl.McNetworkStack
import com.kneelawk.magicalmahou.MMConstants.gui
import com.kneelawk.magicalmahou.MMConstants.str
import com.kneelawk.magicalmahou.component.MMComponents
import com.kneelawk.magicalmahou.net.setC2SReceiver
import com.kneelawk.magicalmahou.screenhandler.widget.WScalableButton
import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import io.github.cottonmc.cotton.gui.widget.WCardPanel
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.data.Insets
import net.minecraft.entity.player.PlayerInventory

class CrystalBallScreenHandler(syncId: Int, inventory: PlayerInventory) :
    SyncedGuiDescription(MMScreenHandlers.CRYSTAL_BALL, syncId, inventory) {
    companion object {
        private val NET_PARENT = McNetworkStack.SCREEN_HANDLER.subType(
            CrystalBallScreenHandler::class.java, str("crystal_ball_screen_handler")
        )

        private val ID_ACCEPT = NET_PARENT.idSignal("ACCEPT").setC2SReceiver {
            component.serverMakeMagical()
        }
    }

    private val component = MMComponents.GENERAL[inventory.player]

    init {
        val root = WCardPanel()
        rootPanel = root

        val acceptPanel = WGridPanel()
        root.add(0, acceptPanel)
        acceptPanel.insets = Insets.ROOT_PANEL
        acceptPanel.setSize(9 * 18 + 14, 9 * 18 + 14)

        // I'm so tired to 20px tall buttons so I'm using one that isn't 20px tall
        val acceptButton = WScalableButton(gui("crystal_ball.accept"))
        acceptPanel.add(acceptButton, 2, 3, 5, 1)
        acceptButton.onClick = {
            ID_ACCEPT.send(CoreMinecraftNetUtil.getClientConnection(), this)
        }

        root.validate(this)
    }
}