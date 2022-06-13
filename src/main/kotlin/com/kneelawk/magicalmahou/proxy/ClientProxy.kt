package com.kneelawk.magicalmahou.proxy

import com.kneelawk.magicalmahou.MMLog
import com.kneelawk.magicalmahou.client.MMClientSettings
import com.kneelawk.magicalmahou.client.screen.MMScreenUtils
import com.kneelawk.magicalmahou.client.skin.ClientSkinManagers
import com.kneelawk.magicalmahou.component.MMComponents
import com.kneelawk.magicalmahou.mixin.api.PlayerEntityRendererEvents
import com.kneelawk.magicalmahou.skin.InvalidSkinException
import com.kneelawk.magicalmahou.skin.PlayerSkinModel
import com.kneelawk.magicalmahou.util.ColorUtils
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.MutableText
import net.minecraft.text.TranslatableTextContent
import org.lwjgl.system.MemoryStack
import org.lwjgl.util.tinyfd.TinyFileDialogs
import java.io.File

object ClientProxy : CommonProxy {
    private var openingPlayerSkin = false
    private var pickingTransformationColor = false

    override fun getDefaultPlayerSkinModel(player: PlayerEntity): PlayerSkinModel {
        return if (player is AbstractClientPlayerEntity) {
            PlayerEntityRendererEvents.setSkinEventsEnabled(false)
            val modelStr = player.model
            PlayerEntityRendererEvents.setSkinEventsEnabled(true)
            val model = PlayerSkinModel.byModelStr(modelStr)

            if (model == null) {
                MMLog.warn("Found invalid player skin model: $modelStr")
                PlayerSkinModel.DEFAULT
            } else {
                model
            }
        } else {
            PlayerSkinModel.DEFAULT
        }
    }

    override fun uploadPlayerSkin(player: PlayerEntity) {
        val component = MMComponents.GENERAL[player]

        if (player is ClientPlayerEntity && !openingPlayerSkin && component.isMagical) {
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
                            component.clientSendSkinUpdate()
                        } catch (e: InvalidSkinException) {
                            when (e) {
                                is InvalidSkinException.BadImage -> player.sendMessage(
                                    MutableText.of(TranslatableTextContent("message.magical-mahou.bad-image", e.cause)),
                                    false
                                )
                                is InvalidSkinException.WrongDimensions -> player.sendMessage(
                                    MutableText.of(
                                        TranslatableTextContent(
                                            "message.magical-mahou.player-skin-wrong-dimensions", e.providedWidth,
                                            e.providedHeight
                                        )
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

    override fun pickTransformationColor(player: PlayerEntity) {
        val component = MMComponents.GENERAL[player]

        if (player is ClientPlayerEntity && !pickingTransformationColor && component.isMagical) {
            pickingTransformationColor = true

            val uiThread = Thread {
                val res = MemoryStack.stackPush().use { stack ->
                    val colorBuf = stack.bytes(*ColorUtils.rgbToBytes(component.transformationColor))

                    val res = TinyFileDialogs.tinyfd_colorChooser("Transformation Color", null, colorBuf, colorBuf)

                    if (res == null) {
                        null
                    } else {
                        ColorUtils.argbFromBytes(0xFF, colorBuf[0].toInt(), colorBuf[1].toInt(), colorBuf[2].toInt())
                    }
                }

                if (res != null) {
                    RenderSystem.recordRenderCall {
                        component.clientSetTransformationColor(res)
                        pickingTransformationColor = false
                    }
                } else {
                    pickingTransformationColor = false
                }
            }

            uiThread.start()
        }
    }

    override fun presetCursorPosition() {
        MMScreenUtils.presetCursorPosition()
    }
}
