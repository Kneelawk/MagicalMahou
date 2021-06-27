package com.kneelawk.magicalmahou.client

import com.kneelawk.magicalmahou.client.image.ClientSkinManagers
import com.kneelawk.magicalmahou.component.MMComponents
import com.kneelawk.magicalmahou.image.InvalidImageException
import com.kneelawk.magicalmahou.image.SkinUtils
import com.kneelawk.magicalmahou.util.EnumUtils
import com.kneelawk.magicalmahou.util.SaveDirUtils
import com.mojang.blaze3d.systems.RenderSystem
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.text.LiteralText
import net.minecraft.text.TranslatableText
import org.lwjgl.glfw.GLFW
import org.lwjgl.system.MemoryStack
import org.lwjgl.util.tinyfd.TinyFileDialogs
import java.io.File

object MMKeys {
    private lateinit var PRINT_SKIN: KeyBinding
    private lateinit var TRANSFORM: KeyBinding
    private lateinit var UPLOAD_PLAYER_SKIN: KeyBinding
    private lateinit var CHANGE_PLAYER_MODEL: KeyBinding

    private var openingPlayerSkin = false

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
        UPLOAD_PLAYER_SKIN = KeyBindingHelper.registerKeyBinding(
            KeyBinding(
                "key.magical-mahou.upload-player-skin", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_APOSTROPHE,
                "category.magical-mahou.general"
            )
        )
        CHANGE_PLAYER_MODEL = KeyBindingHelper.registerKeyBinding(
            KeyBinding(
                "key.magical-mahou.change-player-model", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_BRACKET,
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

                    if (component.isTransformed) {
                        player.sendMessage(LiteralText("Un-Transforming..."), false)
                    } else {
                        player.sendMessage(LiteralText("Transforming..."), false)
                    }

                    component.clientRequestTransform(!component.isTransformed)
                }
            }

            while (UPLOAD_PLAYER_SKIN.wasPressed()) {
                client.player?.let { player ->
                    uploadPlayerSkin(player)
                }
            }

            while (CHANGE_PLAYER_MODEL.wasPressed()) {
                client.player?.let { player ->
                    val component = MMComponents.GENERAL[player]
                    val newPlayerModel = EnumUtils.rotateEnum(component.playerSkinModel, 1)
                    player.sendMessage(LiteralText("Setting player model to: ${newPlayerModel.modelStr}"), false)
                    component.clientSetPlayerSkinModel(newPlayerModel)
                }
            }
        }
    }

    private fun uploadPlayerSkin(player: ClientPlayerEntity) {
        if (!openingPlayerSkin) {
            openingPlayerSkin = true

            val uiThread = Thread {
                val res = MemoryStack.stackPush().use { stack ->
                    val aFilterPatterns = stack.mallocPointer(1)
                    aFilterPatterns.put(stack.UTF8("*.png"))
                    aFilterPatterns.flip()

                    TinyFileDialogs.tinyfd_openFileDialog(
                        "Select Transformed Skin", MMClientSettings.previousPlayerSkinDir.absolutePath, aFilterPatterns,
                        "PNG Files", false
                    )
                }

                if (res != null) {
                    val file = File(res)
                    MMClientSettings.previousPlayerSkinDir = file.parentFile

                    RenderSystem.recordRenderCall {
                        try {
                            // Set client-side skin
                            ClientSkinManagers.CLIENT_PLAYER_SKIN.loadPNGFromFile(file.toPath(), player.uuid, true)
                            ClientSkinManagers.CLIENT_PLAYER_SKIN.update(player.uuid)

                            // Set server-side skin
                            MMComponents.GENERAL[player].clientSendSkinUpdate()
                        } catch (e: InvalidImageException) {
                            when (e) {
                                is InvalidImageException.BadImage -> player.sendMessage(
                                    TranslatableText("message.magical-mahou.bad-image", e.cause), false
                                )
                                is InvalidImageException.WrongDimensions -> player.sendMessage(
                                    TranslatableText(
                                        "message.magical-mahou.player-skin-wrong-dimensions", e.providedWidth,
                                        e.providedHeight
                                    ), false
                                )
                            }
                        } finally {
                            openingPlayerSkin = false
                        }
                    }
                } else {
                    RenderSystem.recordRenderCall {
                        openingPlayerSkin = false
                    }
                }
            }
            uiThread.start()
        }
    }
}