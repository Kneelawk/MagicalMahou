package com.kneelawk.magicalmahou.client.skin

import com.kneelawk.magicalmahou.MMConstants.id
import com.kneelawk.magicalmahou.mixin.api.PlayerEntityRendererEvents
import com.kneelawk.magicalmahou.skin.InvalidSkinException
import com.kneelawk.magicalmahou.skin.SkinManager
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.texture.NativeImage
import net.minecraft.util.Identifier
import org.lwjgl.system.MemoryUtil
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class ClientSkinManager(
    override val imageWidth: Int, override val imageHeight: Int, private val skinPath: String,
    tryPlayerSkin: Boolean
) : SkinManager {
    private val isSkinSized = imageWidth == 64 && imageHeight == 64
    private val usePlayerSkin = tryPlayerSkin && isSkinSized
    private val skinMap = hashMapOf<UUID, Skin>()
    private val validSkins = hashSetOf<UUID>()
    private var toRemove = AtomicBoolean(false)

    init {
        ClientPlayConnectionEvents.DISCONNECT.register { _, _ ->
            toRemove.set(true)
        }
        ClientTickEvents.END_CLIENT_TICK.register {
            if (toRemove.get()) {
                toRemove.set(false)

                validSkins.clear()
            }
        }
    }

    /**
     * Creates a texture identifier for a given player id.
     */
    private fun createId(playerId: UUID): Identifier {
        return id("$skinPath/$playerId")
    }

    /**
     * Creates a new native image. If this ClientSkinManager was created with usePlayerSkin == true, then the new native
     * image will have the player skin.
     */
    private fun createNewImage(): NativeImage {
        val mc = MinecraftClient.getInstance()
        val newImage = NativeImage(NativeImage.Format.ABGR, imageWidth, imageHeight, !usePlayerSkin)

        if (usePlayerSkin) {
            // Copy the image texture from the player's current skin
            val player = mc.player ?: throw IllegalStateException(
                "Attempted to create an image wrapper from a player who is not in the world"
            )

            PlayerEntityRendererEvents.setSkinEventsEnabled(false)
            val texture = mc.textureManager.getTexture(player.skinTexture)
            PlayerEntityRendererEvents.setSkinEventsEnabled(true)

            texture.bindTexture()
            newImage.loadFromTextureImage(0, false)
        }

        return newImage
    }

    /**
     * Creates and registers a new skin texture for the given player id and with the given native image.
     */
    private fun createSkin(playerId: UUID, image: NativeImage): Skin {
        val newTexture = NativeImageBackedPlayerSkinTexture(image, playerId)
        val id = createId(playerId)

        MinecraftClient.getInstance().textureManager.registerTexture(id, newTexture)

        return Skin(id, newTexture)
    }

    /**
     * Puts a native image into the skin map, marking the skin as valid.
     *
     * Note that this does not do any image size checks or conversions.
     */
    private fun unsafePutImage(playerId: UUID, newImage: NativeImage): Skin {
        val skin: Skin

        if (skinMap.containsKey(playerId)) {
            skin = skinMap[playerId]!!
            skin.texture.image = newImage
            skin.texture.upload()
        } else {
            skin = createSkin(playerId, newImage)
            skinMap[playerId] = skin
        }

        validSkins.add(playerId)

        return skin
    }

    /**
     * Puts a native image into the skin map, marking the skin as valid, or throws an error if the image is the wrong
     * size and could not be converted.
     */
    private fun putImage(playerId: UUID, nativeImage: NativeImage, convertLegacy: Boolean) {
        var newImage = nativeImage

        if (nativeImage.width != imageWidth) {
            throw InvalidSkinException.WrongDimensions(imageWidth, imageHeight, nativeImage.width, nativeImage.height)
        } else if (convertLegacy && isSkinSized && nativeImage.height == 32) {
            newImage = ClientSkinUtils.convertLegacyPlayerSkin(nativeImage)
        } else if (nativeImage.height != imageHeight) {
            throw InvalidSkinException.WrongDimensions(imageWidth, imageHeight, nativeImage.width, nativeImage.height)
        }

        unsafePutImage(playerId, newImage)
    }

    /**
     * Gets an existing skin from the skin map or creating/initializing one if needed.
     */
    private fun getOrCreate(playerId: UUID): Skin {
        return if (skinMap.containsKey(playerId) && validSkins.contains(playerId)) {
            skinMap[playerId]!!
        } else {
            unsafePutImage(playerId, createNewImage())
        }
    }

    fun getIdentifier(playerId: UUID): Identifier {
        return getOrCreate(playerId).id
    }

    override fun ensureExists(playerId: UUID): Boolean {
        return if (skinMap.containsKey(playerId) && validSkins.contains(playerId)) {
            true
        } else {
            unsafePutImage(playerId, createNewImage())
            false
        }
    }

    override fun loadPNGFromBytes(data: ByteArray, playerId: UUID) {
        val buf = MemoryUtil.memAlloc(data.size)
        val newImage: NativeImage
        try {
            buf.put(0, data)
            newImage = NativeImage.read(NativeImage.Format.ABGR, buf)
        } catch (e: IOException) {
            throw InvalidSkinException.BadImage(e)
        } finally {
            MemoryUtil.memFree(buf)
        }

        putImage(playerId, newImage, false)
    }

    override fun loadPNGFromFile(path: Path, playerId: UUID, convertLegacy: Boolean) {
        val newImage: NativeImage
        try {
            newImage = NativeImage.read(Files.newInputStream(path))
        } catch (e: IOException) {
            throw InvalidSkinException.BadImage(e)
        }
        putImage(playerId, newImage, convertLegacy)
    }

    override fun storePNGToBytes(playerId: UUID): ByteArray {
        return getOrCreate(playerId).texture.image!!.bytes
    }

    override fun storePNGToFile(path: Path, playerId: UUID) {
        getOrCreate(playerId).texture.image!!.writeFile(path)
    }

    override fun setARGBPixel(playerId: UUID, x: Int, y: Int, argb: Int) {
        val a = (argb shl 0x18) and 0xFF
        val r = (argb shl 0x10) and 0xFF
        val g = (argb shl 0x08) and 0xFF
        val b = argb and 0xFF
        val abgr = (a shr 0x18) or (b shr 0x10) or (g shr 0x08) or r
        getOrCreate(playerId).texture.image!!.setPixelColor(x, y, abgr)
    }

    override fun update(playerId: UUID) {
        getOrCreate(playerId).texture.upload()
    }

    private data class Skin(val id: Identifier, val texture: NativeImageBackedPlayerSkinTexture)
}