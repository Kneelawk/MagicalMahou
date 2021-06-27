package com.kneelawk.magicalmahou.server.image

import java.awt.Graphics2D
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage

object ServerSkinUtils {
    private fun copyFlippedRegion(
        to: Graphics2D, from: BufferedImage, x: Int, y: Int, translateX: Int, translateY: Int, width: Int, height: Int
    ) {
        val transform =
            AffineTransform(-1.0, 0.0, 0.0, 1.0, (2 * x + width + translateX).toDouble(), translateY.toDouble())
        to.setClip(x + translateX, y + translateY, width, height)
        to.drawRenderedImage(from, transform)
    }

    fun convertLegacyPlayerSkin(bufferedImage: BufferedImage): BufferedImage {
        val bufferedImage2 = BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB)
        val graphics = bufferedImage2.createGraphics()

        // Copy original skin to upper half
        graphics.drawRenderedImage(bufferedImage, AffineTransform.getTranslateInstance(0.0, 0.0))

        // Copy flipped versions of arms and legs to new locations
        copyFlippedRegion(graphics, bufferedImage, 4, 16, 16, 32, 4, 4)
        copyFlippedRegion(graphics, bufferedImage, 8, 16, 16, 32, 4, 4)
        copyFlippedRegion(graphics, bufferedImage, 0, 20, 24, 32, 4, 12)
        copyFlippedRegion(graphics, bufferedImage, 4, 20, 16, 32, 4, 12)
        copyFlippedRegion(graphics, bufferedImage, 8, 20, 8, 32, 4, 12)
        copyFlippedRegion(graphics, bufferedImage, 12, 20, 16, 32, 4, 12)
        copyFlippedRegion(graphics, bufferedImage, 44, 16, -8, 32, 4, 4)
        copyFlippedRegion(graphics, bufferedImage, 48, 16, -8, 32, 4, 4)
        copyFlippedRegion(graphics, bufferedImage, 40, 20, 0, 32, 4, 12)
        copyFlippedRegion(graphics, bufferedImage, 44, 20, -8, 32, 4, 12)
        copyFlippedRegion(graphics, bufferedImage, 48, 20, -16, 32, 4, 12)
        copyFlippedRegion(graphics, bufferedImage, 52, 20, -8, 32, 4, 12)

        return bufferedImage2
    }
}