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
import net.minecraft.entity.player.PlayerEntity
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

    private val root = WCardPanel()

    private val listener: (Boolean) -> Unit = {
        if (it) {
            root.selectedIndex = 1
        } else {
            root.selectedIndex = 0
        }
    }

    init {
        rootPanel = root

        val acceptPanel = WGridPanel()
        root.add(0, acceptPanel)
        acceptPanel.insets = Insets.ROOT_PANEL

        // I'm too tired of 20px tall buttons so I'm using one that isn't 20px tall
        val acceptButton = WScalableButton(gui("crystal_ball.accept"))
        acceptPanel.add(acceptButton, 0, 3, 9, 1)
        acceptButton.onClick = {
            ID_ACCEPT.send(CoreMinecraftNetUtil.getClientConnection(), this)
        }

        val mainPanel = WGridPanel()
        root.add(1, mainPanel)

        // Select the current panel depending on whether the player is magical or not
        if (component.isMagical) {
            root.selectedIndex = 1
        } else {
            root.selectedIndex = 0
        }

        // add the listener so we are notified of magical changes
        component.addIsMagicalListener(listener)

        root.validate(this)
    }

    override fun close(playerEntity: PlayerEntity?) {
        super.close(playerEntity)
        component.removeIsMagicalListener(listener)
    }
}