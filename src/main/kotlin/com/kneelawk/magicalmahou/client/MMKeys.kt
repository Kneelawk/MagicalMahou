package com.kneelawk.magicalmahou.client

import com.kneelawk.magicalmahou.image.MMImageUtils
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.text.LiteralText
import org.lwjgl.glfw.GLFW

object MMKeys {
    lateinit var PRINT_SKIN: KeyBinding

    fun register() {
        PRINT_SKIN = KeyBindingHelper.registerKeyBinding(
            KeyBinding(
                "key.magical-mahou.print-skin", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F9,
                "category.magical-mahou.general"
            )
        )

        ClientTickEvents.END_CLIENT_TICK.register { client ->
            while (PRINT_SKIN.wasPressed()) {
                client.player?.let { player ->
                    // TODO: Have this print the magical skin once that's implemented
                    player.sendMessage(LiteralText("Printing skin..."), false)
                    val skin = MMImageUtils.IMAGE_FACTORY.default()
                    MMImageUtils.writeForPlayer(player, skin)
                    player.sendMessage(LiteralText("Skin printed."), false)
                }
            }
        }
    }
}