package com.kneelawk.magicalmahou.image

import java.io.IOException
import java.nio.file.Path
import java.util.*

interface SkinManager {
    /**
     * The width in pixels of the images managed by this manager.
     */
    val imageWidth: Int

    /**
     * The height in pixels of the images managed by this manager.
     */
    val imageHeight: Int

    /**
     * Makes sure that the skin associated with the player id exists, creating a "blank" skin if the skin did not exist
     * already, returning whether the skin already existed.
     *
     * A "blank" skin depends on whether the skin manager is on the server or client. A server-side blank skin is just
     * an image with all pixels set to '0' in all ARGB channels. A client-side blank skin is a copy of the player's
     * current skin.
     *
     * @param playerId the UUID of the player to which this skin is attached.
     * @return true if a skin was already present for the requested player, false if a "blank" one was created.
     */
    fun ensureExists(playerId: UUID): Boolean

    /**
     * Loads a skin from a PNG formatted byte array to the slot for the given player id.
     *
     * @param data the byte array containing PNG data.
     * @param playerId the UUID of the player to which this skin is attached.
     * @throws InvalidImageException if the byte array did not contain PNG data or contained an image of the wrong
     * dimensions.
     */
    @Throws(InvalidImageException::class)
    fun loadPNGFromBytes(data: ByteArray, playerId: UUID)

    /**
     * Loads a skin from a PNG file to the slot for the given player id.
     *
     * @param path the path of the file to load the PNG from.
     * @param playerId the UUID of the player to which this skin is attached.
     * @param convertLegacy if this skin manager manages 64x64 skins this option can be used to convert a 64x32 "legacy"
     * player skin to a 64x64 player skin.
     * @throws InvalidImageException if the requested file does not contain PNG data or contains an image of the wrong
     * dimensions.
     */
    @Throws(InvalidImageException::class)
    fun loadPNGFromFile(path: Path, playerId: UUID, convertLegacy: Boolean)

    /**
     * Stores PNG formatted data of this image to a byte array and returns it.
     *
     * @param playerId the UUID of the player to which the storing skin is attached.
     * @return a byte array containing the image in PNG format.
     */
    fun storePNGToBytes(playerId: UUID): ByteArray

    /**
     * Stores the image associated with the player id into a PNG formatted file.
     *
     * @param path the path of the file to store the PNG formatted data into.
     * @param playerId the UUID of the player to which the skin is attached.
     * @throws IOException if an error occurs while writing the image to the file.
     */
    @Throws(IOException::class)
    fun storePNGToFile(path: Path, playerId: UUID)

    /**
     * Sets a pixel in the skin associated with the player id.
     *
     * @param playerId the UUID of the player to which the skin is attached.
     * @param x the x position of the pixel to set.
     * @param y the y position of the pixel to set.
     * @param argb the ARGB value to set the pixel to.
     */
    fun setARGBPixel(playerId: UUID, x: Int, y: Int, argb: Int)

    /**
     * Updates the skin associated with the player id.
     *
     * This is usually called on the client to upload the skin to the GPU so that its changes take effect.
     *
     * @param playerId the UUID of the player to which the skin is attached.
     */
    fun update(playerId: UUID)
}