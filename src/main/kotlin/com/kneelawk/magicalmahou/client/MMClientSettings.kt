package com.kneelawk.magicalmahou.client

import com.kneelawk.magicalmahou.MMConstants
import com.kneelawk.magicalmahou.MMLog
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtIo
import java.io.File
import java.io.IOException

object MMClientSettings {
    private val SETTINGS_FILE = File(MMConstants.MOD_ID, "settings.dat")

    var previousPlayerSkinDir = File(System.getProperty("user.home"))

    fun init() {
        ClientLifecycleEvents.CLIENT_STOPPING.register { store() }

        if (SETTINGS_FILE.exists()) {
            load()
        }
    }

    private fun load() {
        try {
            val tag = NbtIo.readCompressed(SETTINGS_FILE)

            previousPlayerSkinDir = File(tag.getString("previousPlayerSkinDir"))
        } catch (e: IOException) {
            MMLog.warn("Error loading client settings", e)
        }
    }

    private fun store() {
        try {
            val tag = NbtCompound()

            tag.putString("previousPlayerSkinDir", previousPlayerSkinDir.absolutePath)

            NbtIo.writeCompressed(tag, SETTINGS_FILE)
        } catch (e: IOException) {
            MMLog.warn("Error storing client settings", e)
        }
    }
}