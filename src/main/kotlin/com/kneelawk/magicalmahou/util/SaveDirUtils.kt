package com.kneelawk.magicalmahou.util

import com.kneelawk.magicalmahou.MMConstants
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.WorldSavePath
import net.minecraft.world.World
import java.nio.file.Path
import kotlin.io.path.Path

object SaveDirUtils {
    fun getRootDir(world: World): Path {
        return if (world.isClient) {
            Path("")
        } else {
            world as ServerWorld
            world.server.getSavePath(WorldSavePath.ROOT)
        }
    }

    fun getStorageDir(world: World): Path {
        return getRootDir(world).resolve(MMConstants.MOD_ID)
    }

    fun getSkinStorageDir(world: World): Path {
        return getStorageDir(world).resolve("skins")
    }

    fun getPlayerSkinStorageDir(world: World): Path {
        return getSkinStorageDir(world).resolve(MMConstants.MOD_ID).resolve(MMConstants.PLAYER_SKIN_PATH)
    }
}