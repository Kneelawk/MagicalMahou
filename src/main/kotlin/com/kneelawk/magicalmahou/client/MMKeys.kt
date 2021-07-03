package com.kneelawk.magicalmahou.client

import com.kneelawk.magicalmahou.component.MMComponents
import com.kneelawk.magicalmahou.skin.SkinUtils
import com.kneelawk.magicalmahou.util.SaveDirUtils
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.text.LiteralText
import org.lwjgl.glfw.GLFW

object MMKeys {
    private lateinit var PRINT_SKIN: KeyBinding
    private lateinit var TRANSFORM: KeyBinding
    private lateinit var TELEPORT_AT: KeyBinding

    fun register() {
        PRINT_SKIN = KeyBindingHelper.registerKeyBinding(
            KeyBinding(
                "key.magical-mahou.print-skin", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F9,
                "category.magical-mahou.general"
            )
        )
        TRANSFORM = KeyBindingHelper.registerKeyBinding(
            KeyBinding(
                "key.magical-mahou.transform", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R,
                "category.magical-mahou.general"
            )
        )
        TELEPORT_AT = KeyBindingHelper.registerKeyBinding(
            KeyBinding(
                "key.magical-mahou.teleport_at", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V,
                "category.magical-mahou.general"
            )
        )

        ClientTickEvents.END_CLIENT_TICK.register { client ->
            while (PRINT_SKIN.wasPressed()) {
                client.player?.let { player ->
                    player.sendMessage(LiteralText("Printing skin..."), false)
                    SkinUtils.storePlayerSkin(SaveDirUtils.getPlayerIdStr(player), player.uuid, player.world)
                    player.sendMessage(LiteralText("Skin printed."), false)
                }
            }

            while (TRANSFORM.wasPressed()) {
                client.player?.let { player ->
                    val component = MMComponents.GENERAL[player]

                    if (component.isMagical) {
                        val transformed = component.isActuallyTransformed()
                        component.clientRequestTransform(!transformed)
                    }
                }
            }

            while (TELEPORT_AT.wasPressed()) {
                client.player?.let { player ->
                    val general = MMComponents.GENERAL[player]
                    val teleportAt = MMComponents.TELEPORT_AT[player]

                    if (general.isActuallyTransformed() && teleportAt.isActuallyEnabled()) {
                        println("Attempting teleport at")
                        teleportAt.clientTeleportAt()
                    }
                }
            }
        }
    }
}