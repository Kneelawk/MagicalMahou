package com.kneelawk.magicalmahou.screenhandler

import alexiil.mc.lib.net.impl.CoreMinecraftNetUtil
import alexiil.mc.lib.net.impl.McNetworkStack
import com.kneelawk.magicalmahou.MMConstants.gui
import com.kneelawk.magicalmahou.MMConstants.str
import com.kneelawk.magicalmahou.component.MMComponents
import com.kneelawk.magicalmahou.net.MMNetIds
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
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.text.LiteralText
import kotlin.math.min

class CrystalBallScreenHandler(syncId: Int, inventory: PlayerInventory) :
    SyncedGuiDescription(MMScreenHandlers.CRYSTAL_BALL, syncId, inventory) {
    companion object {
        private const val ABILITIES_PER_PAGE = 9

        private val NET_PARENT = McNetworkStack.SCREEN_HANDLER.subType(
            CrystalBallScreenHandler::class.java, str("crystal_ball_screen_handler")
        )

        private val ID_ACCEPT = NET_PARENT.idSignal("ACCEPT").setC2SReceiver {
            component.serverMakeMagical()
        }

        private val ID_OPEN_ABILITY = NET_PARENT.idData("OPEN_ABILITY").setC2SReceiver { buf, ctx ->
            val key =
                MMNetIds.COMPONENT_KEY_CACHE.getObj(ctx.connection, buf.readVarUnsignedInt()) ?: return@setC2SReceiver
            val component = key[ctx.connection.player] as? NamedScreenHandlerFactory ?: return@setC2SReceiver
            ctx.connection.player.openHandledScreen(component)
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
            ScreenHandlerHelper.lastOpenCrystalBallCard = 2
        }

        val configureAbilitiesButton = WScalableButton(gui("crystal_ball.configure_abilities"))
        mainPanel.add(configureAbilitiesButton, 0, 3, 9, 1)
        configureAbilitiesButton.onClick = {
            root.selectedIndex = 3
            ScreenHandlerHelper.lastOpenCrystalBallCard = 3
        }

        root.add(1, mainPanel)

        // skin section

        val skinPanel = WGridPanel()
        skinPanel.insets = Insets.ROOT_PANEL

        val skinBackButton = WScalableButton(LiteralText("<-"))
        skinPanel.add(skinBackButton, 0, 0, 1, 1)
        skinBackButton.onClick = {
            root.selectedIndex = 1
            ScreenHandlerHelper.lastOpenCrystalBallCard = 1
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
            ScreenHandlerHelper.lastOpenCrystalBallCard = 1
        }

        val abilityButtonsPanel = WCardPanel()

        val abilityButtons = createAbilityButtons()
        val abilitiesCount = abilityButtons.size
        for (page in abilityButtons.indices step ABILITIES_PER_PAGE) {
            val buttonPanel = WGridPanel()
            for (indexOnPage in 0 until min(ABILITIES_PER_PAGE, abilitiesCount - page)) {
                val buttonIndex = page + indexOnPage
                buttonPanel.add(abilityButtons[buttonIndex], indexOnPage, 0, 1, 1)
            }
            abilityButtonsPanel.add(page, buttonPanel)
        }

        if (abilitiesCount == 0) {
            abilityButtonsPanel.add(0, WGridPanel())
        }

        abilityPanel.add(abilityButtonsPanel, 0, 3, ABILITIES_PER_PAGE, 1)

        val abilityPageBackButton = WScalableButton(LiteralText("<"))
        abilityPanel.add(abilityPageBackButton, 0, 4, 1, 1)
        abilityPageBackButton.enabled = abilityButtonsPanel.selectedIndex > 0
        abilityPageBackButton.onClick = {
            if (abilityButtonsPanel.selectedIndex > 0) {
                abilityButtonsPanel.selectedIndex--
            }
            abilityPageBackButton.enabled = abilityButtonsPanel.selectedIndex > 0
        }

        val abilityPageForwardButton = WScalableButton(LiteralText(">"))
        abilityPanel.add(abilityPageForwardButton, ABILITIES_PER_PAGE - 1, 4, 1, 1)
        abilityPageForwardButton.enabled = abilityButtonsPanel.selectedIndex < (abilitiesCount - 1) / ABILITIES_PER_PAGE
        abilityPageForwardButton.onClick = {
            if (abilityButtonsPanel.selectedIndex < (abilitiesCount - 1) / ABILITIES_PER_PAGE) {
                abilityButtonsPanel.selectedIndex++
            }
            abilityPageForwardButton.enabled =
                abilityButtonsPanel.selectedIndex < (abilitiesCount - 1) / ABILITIES_PER_PAGE
        }

        root.add(3, abilityPanel)

        // Select the current panel depending on whether the player is magical or not
        if (component.isMagical) {
            if (ScreenHandlerHelper.lastOpenCrystalBallCard < 1 || ScreenHandlerHelper.lastOpenCrystalBallCard > 3) {
                ScreenHandlerHelper.lastOpenCrystalBallCard = 1
            }
            root.selectedIndex = ScreenHandlerHelper.lastOpenCrystalBallCard
        } else {
            root.selectedIndex = 0
        }

        root.validate(this)
    }

    private fun createAbilityButtons(): List<WScalableButton> {
        val list = mutableListOf<WScalableButton>()

        for (key in MMComponents.getAbilityComponents()) {
            val component = key[playerInventory.player]

            if (component.getPlayerHasComponent()) {
                val button = WScalableButton(icon = component.icon)
                button.tooltip = listOf(component.name)
                button.enabled = component is NamedScreenHandlerFactory
                button.onClick = {
                    if (component is NamedScreenHandlerFactory) {
                        MMProxy.getProxy().presetCursorPosition()
                        ID_OPEN_ABILITY.send(CoreMinecraftNetUtil.getClientConnection(), this) { _, buf, ctx ->
                            ctx.assertClientSide()
                            buf.writeVarUnsignedInt(MMNetIds.COMPONENT_KEY_CACHE.getId(ctx.connection, component.key))
                        }
                    }
                }
                list.add(button)
            }
        }

        return list
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