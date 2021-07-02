package com.kneelawk.magicalmahou.screenhandler

import alexiil.mc.lib.net.impl.CoreMinecraftNetUtil
import alexiil.mc.lib.net.impl.McNetworkStack
import com.kneelawk.magicalmahou.MMConstants.gui
import com.kneelawk.magicalmahou.MMConstants.str
import com.kneelawk.magicalmahou.component.MMComponents
import com.kneelawk.magicalmahou.net.setC2SReceiver
import com.kneelawk.magicalmahou.proxy.MMProxy
import com.kneelawk.magicalmahou.screenhandler.widget.WScalableButton
import com.kneelawk.magicalmahou.skin.PlayerSkinModel
import com.kneelawk.magicalmahou.util.EnumUtils
import io.github.cottonmc.cotton.gui.SyncedGuiDescription
import io.github.cottonmc.cotton.gui.widget.WCardPanel
import io.github.cottonmc.cotton.gui.widget.WGridPanel
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment
import io.github.cottonmc.cotton.gui.widget.data.Insets
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.LiteralText

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
    private val changePlayerModelButton: WScalableButton

    init {
        titleAlignment = HorizontalAlignment.CENTER
        rootPanel = root

        // initial 'accept' panel

        val acceptPanel = WGridPanel()
        acceptPanel.insets = Insets.ROOT_PANEL

        // I'm too tired of 20px tall buttons so I'm using one that isn't 20px tall
        val acceptButton = WScalableButton(gui("crystal_ball.accept"))
        acceptPanel.add(acceptButton, 0, 3, 9, 1)
        acceptButton.onClick = {
            ID_ACCEPT.send(CoreMinecraftNetUtil.getClientConnection(), this)
        }

        root.add(0, acceptPanel)

        // first 'configure' panel

        val mainPanel = WGridPanel()
        mainPanel.insets = Insets.ROOT_PANEL

        val configureSkinButton = WScalableButton(gui("crystal_ball.configure_skin"))
        mainPanel.add(configureSkinButton, 0, 2, 9, 1)
        configureSkinButton.onClick = {
            root.selectedIndex = 2
        }

        val configureAbilitiesButton = WScalableButton(gui("crystal_ball.configure_abilities"))
        mainPanel.add(configureAbilitiesButton, 0, 3, 9, 1)
        configureAbilitiesButton.onClick = {
            root.selectedIndex = 3
        }

        root.add(1, mainPanel)

        // skin section

        val skinPanel = WGridPanel()
        skinPanel.insets = Insets.ROOT_PANEL

        val skinBackButton = WScalableButton(LiteralText("<-"))
        skinPanel.add(skinBackButton, 0, 0, 1, 1)
        skinBackButton.onClick = {
            root.selectedIndex = 1
        }

        val uploadSkinButton = WScalableButton(gui("crystal_ball.upload_skin"))
        skinPanel.add(uploadSkinButton, 0, 2, 9, 1)
        uploadSkinButton.onClick = {
            MMProxy.getProxy().uploadPlayerSkin(playerInventory.player)
        }

        changePlayerModelButton =
            WScalableButton(gui("crystal_ball.change_player_model", component.playerSkinModel.modelStr))
        skinPanel.add(changePlayerModelButton, 0, 3, 9, 1)
        changePlayerModelButton.onClick = {
            val newPlayerModel = EnumUtils.rotateEnum(component.playerSkinModel, 1)
            component.clientSetPlayerSkinModel(newPlayerModel)
        }

        val setTransformationColorButton = WScalableButton(gui("crystal_ball.set_transformation_color"))
        skinPanel.add(setTransformationColorButton, 0, 4, 9, 1)
        setTransformationColorButton.onClick = {
            MMProxy.getProxy().pickTransformationColor(playerInventory.player)
        }

        root.add(2, skinPanel)

        // ability section

        val abilityPanel = WGridPanel()
        abilityPanel.insets = Insets.ROOT_PANEL

        val abilityBackButton = WScalableButton(LiteralText("<-"))
        abilityPanel.add(abilityBackButton, 0, 0, 1, 1)
        abilityBackButton.onClick = {
            root.selectedIndex = 1
        }

        root.add(3, abilityPanel)

        // Select the current panel depending on whether the player is magical or not
        if (component.isMagical) {
            root.selectedIndex = 1
        } else {
            root.selectedIndex = 0
        }

        root.validate(this)
    }

    fun s2cReceiveIsMagicalChange(isMagical: Boolean) {
        if (isMagical) {
            if (root.selectedIndex == 0) {
                root.selectedIndex = 1
            }
        } else {
            root.selectedIndex = 0
        }
    }

    fun s2cReceiveSkinModelChange(model: PlayerSkinModel) {
        changePlayerModelButton.label = gui("crystal_ball.change_player_model", model.modelStr)
    }
}