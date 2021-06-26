package com.kneelawk.magicalmahou.server.image

import com.kneelawk.magicalmahou.image.InvalidImageException
import com.kneelawk.magicalmahou.image.SkinManager
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import javax.imageio.ImageIO

class ServerSkinManager(override val imageWidth: Int, override val imageHeight: Int) : SkinManager {
    private val skinMap = hashMapOf<UUID, BufferedImage>()
    private val toRemove = ArrayDeque<UUID>()

    init {
        ServerLifecycleEvents.SERVER_STOPPED.register {
            // No use keeping this stuff around using memory
            skinMap.clear()
        }
        ServerPlayConnectionEvents.DISCONNECT.register { handler, _ ->
            println("Player ${handler.player.uuid} disconnected")
            // Make sure to remove skins of disconnected players on the next tick
            toRemove.add(handler.player.uuid)
        }
        ServerTickEvents.END_SERVER_TICK.register {
            // Remove all the skins of disconnected players
            while (toRemove.isNotEmpty()) {
                val removeUUID = toRemove.remove()
                println("Removing player skin $removeUUID")
                skinMap.remove(removeUUID)
            }
        }
    }

    private fun getOrCreate(playerId: UUID): BufferedImage {
        return skinMap.getOrElse(playerId) { BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB) }
    }

    override fun ensureExists(playerId: UUID): Boolean {
        return if (skinMap.containsKey(playerId)) {
            true
        } else {
            skinMap[playerId] = BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB)
            false
        }
    }

    private fun putBufferedImage(playerId: UUID, bufferedImage: BufferedImage, convertLegacy: Boolean) {
        var newImage = bufferedImage

        if (imageWidth != bufferedImage.width) {
            throw InvalidImageException.WrongDimensions(
                imageWidth, imageHeight, bufferedImage.width, bufferedImage.height
            )
        } else if (convertLegacy && imageWidth == 64 && imageHeight == 64 && bufferedImage.height == 32) {
            newImage = ServerSkinUtils.convertLegacyPlayerSkin(bufferedImage)
        } else if (imageHeight != bufferedImage.height) {
            throw InvalidImageException.WrongDimensions(
                imageWidth, imageHeight, bufferedImage.width, bufferedImage.height
            )
        }

        // Here we can let the image resource get cleaned up by the garbage collector
        skinMap[playerId] = newImage
    }

    override fun loadPNGFromBytes(data: ByteArray, playerId: UUID) {
        val newImage: BufferedImage
        try {
            newImage = ImageIO.read(ByteArrayInputStream(data))
        } catch (e: IOException) {
            throw InvalidImageException.BadImage(e)
        }

        putBufferedImage(playerId, newImage, false)
    }

    override fun loadPNGFromFile(path: Path, playerId: UUID, convertLegacy: Boolean) {
        val newImage: BufferedImage
        try {
            newImage = ImageIO.read(Files.newInputStream(path))
        } catch (e: IOException) {
            throw InvalidImageException.BadImage(e)
        }

        putBufferedImage(playerId, newImage, convertLegacy)
    }

    override fun storePNGToBytes(playerId: UUID): ByteArray {
        val baos = ByteArrayOutputStream()
        ImageIO.write(getOrCreate(playerId), "png", baos)
        return baos.toByteArray()
    }

    override fun storePNGToFile(path: Path, playerId: UUID) {
        ImageIO.write(getOrCreate(playerId), "png", Files.newOutputStream(path))
    }

    override fun setARGBPixel(playerId: UUID, x: Int, y: Int, argb: Int) {
        getOrCreate(playerId).setRGB(x, y, argb)
    }

    override fun update(playerId: UUID) {
        // This does nothing on the server-side
    }
}