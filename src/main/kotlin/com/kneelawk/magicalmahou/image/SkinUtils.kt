package com.kneelawk.magicalmahou.image

import com.kneelawk.magicalmahou.MMLog
import com.kneelawk.magicalmahou.util.SaveDirUtils
import net.minecraft.world.World
import java.nio.file.Files
import java.util.*

object SkinUtils {

    /**
     * Stores a player skin to the designated player skin location based on the player's UUID.
     *
     * @param uuid the UUID of the player to load the skin for.
     * @param world the world that the player is in (used to tell things like logical side and save dir).
     */
    fun storePlayerSkin(uuid: UUID, world: World) {
        println("Storing player skin...")
        val uuidStr = uuid.toString()
        val skinDir = SaveDirUtils.getPlayerSkinStorageDir(world).resolve(uuidStr.substring(0, 2))
        Files.createDirectories(skinDir)
        val skinPath = skinDir.resolve(uuidStr)
        SkinManagers.getPlayerSkinManger(world).storePNGToFile(skinPath, uuid)
    }

    /**
     * Loads a player skin from the designated player skin location based on the player's UUID.
     *
     * @param uuid the UUID of the player to load the skin for.
     * @param world the world that the player is in (used to tell things like logical side and save dir).
     * @return `true` if the load operation was successful, `false` otherwise.
     * @throws InvalidImageException if there was an error while loading the image.
     */
    @Throws(InvalidImageException::class)
    fun loadPlayerSkin(uuid: UUID, world: World): Boolean {
        println("Loading player skin...")
        val uuidStr = uuid.toString()
        val skinDir = SaveDirUtils.getSkinStorageDir(world).resolve(uuidStr.substring(0, 2))
        val skinPath = skinDir.resolve(uuidStr)

        if (!Files.exists(skinPath)) {
            MMLog.warn("Skin for $uuidStr does not exist")
            return false
        }

        return try {
            val skinManager = SkinManagers.getPlayerSkinManger(world)
            skinManager.loadPNGFromFile(skinPath, uuid, true)
            skinManager.update(uuid)
            return true
        } catch (e: InvalidImageException) {
            when (e) {
                is InvalidImageException.BadImage -> MMLog.warn("Error while loading player skin from save", e)
                is InvalidImageException.WrongDimensions -> MMLog.warn(
                    "Loaded saved player skin with wrong dimensions. Should be (${e.requiredWidth}x${e.requiredHeight}) but were (${e.requiredWidth}x${e.providedHeight})",
                    e
                )
            }
            false
        }
    }
}