package com.kneelawk.magicalmahou.client

import com.kneelawk.magicalmahou.component.MMComponents
import com.kneelawk.magicalmahou.image.SkinUtils
import com.kneelawk.magicalmahou.util.SaveDirUtils
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.text.LiteralText
import org.lwjgl.glfw.GLFW

object MMKeys {
    lateinit var PRINT_SKIN: KeyBinding
    lateinit var TRANSFORM: KeyBinding

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

        ClientTickEvents.END_CLIENT_TICK.register { client ->
            while (PRINT_SKIN.wasPressed()) {
                println("Attempting to print skin...")
                client.player?.let { player ->
                    println("Printing skin...")
                    player.sendMessage(LiteralText("Printing skin..."), false)
                    SkinUtils.storePlayerSkin(SaveDirUtils.getPlayerIdStr(player), player.uuid, player.world)
                    player.sendMessage(LiteralText("Skin printed."), false)
                    println("Skin Printed.")
                }
            }

            while (TRANSFORM.wasPressed()) {
                client.player?.let { player ->
                    val component = MMComponents.GENERAL[player]

                    if (component.isTransformed) {
                        player.sendMessage(LiteralText("Un-Transforming..."), false)
                    } else {
                        player.sendMessage(LiteralText("Transforming..."), false)
                    }

                    component.clientRequestTransform(!component.isTransformed)
                }
            }
        }
    }
}