package com.kneelawk.magicalmahou.image

import com.kneelawk.magicalmahou.MMLog
import com.kneelawk.magicalmahou.util.SaveDirUtils
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraft.world.World
import java.io.IOException
import java.nio.file.Files
import java.util.*

object MMImageUtils {
    // FIXME: This assumes that physical server and physical client are the same as logical server and logical client!
    lateinit var IMAGE_FACTORY: ImageWrapperFactory

    fun writeToPacket(packet: PacketByteBuf, image: ImageWrapper) {
        packet.writeByteArray(image.encodePNGData())
    }

    fun readFromPacket(packet: PacketByteBuf, image: ImageWrapper) {
        // a 64 x 64 png should never be larger than 65kb
        image.decodePNGData(packet.readByteArray(65536))
    }

    fun readFromPacket(packet: PacketByteBuf): ImageWrapper {
        return IMAGE_FACTORY.createFromPNG(packet.readByteArray(65536))
    }

    fun writeForPlayer(player: PlayerEntity, image: ImageWrapper): UUID {
        val world = player.world
        val uuid = player.uuid
        val uuidStr = uuid.toString()
        val skinDir = SaveDirUtils.getSkinStorageDir(world).resolve(uuidStr.substring(0, 2))
        Files.createDirectories(skinDir)
        val skinPath = skinDir.resolve(uuidStr)
        image.writeToFile(skinPath)

        return uuid
    }

    fun readForUUID(uuid: UUID, world: World): ImageWrapper {
        val uuidStr = uuid.toString()
        val skinDir = SaveDirUtils.getSkinStorageDir(world).resolve(uuidStr.substring(0, 2))
        val skinPath = skinDir.resolve(uuidStr)

        if (!Files.exists(skinPath)) {
            MMLog.warn("Skin for $uuidStr does not exist")
            return IMAGE_FACTORY.default()
        }

        return try {
            IMAGE_FACTORY.loadFromFile(skinPath)
        } catch (e: IOException) {
            MMLog.warn("Error loading skin for $uuidStr", e)
            IMAGE_FACTORY.default()
        }
    }
}