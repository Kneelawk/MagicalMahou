package com.kneelawk.magicalmahou.icon

import com.kneelawk.magicalmahou.MMConstants.id
import com.kneelawk.magicalmahou.client.screen.icon.EnhancedTextureIcon
import com.kneelawk.magicalmahou.client.screen.icon.NinePatchIcon
import net.minecraft.util.Identifier

object MMIcons {
    val BUTTON_DISABLED =
        NinePatchIcon(Identifier("textures/gui/widgets.png"), 256, 256, 0, 46, 200, 20, 2, 2, 2, 2, true)
    val BUTTON_REGULAR =
        NinePatchIcon(Identifier("textures/gui/widgets.png"), 256, 256, 0, 46 + 20, 200, 20, 3, 3, 3, 3, true)
    val BUTTON_HOVERED =
        NinePatchIcon(Identifier("textures/gui/widgets.png"), 256, 256, 0, 46 + 40, 200, 20, 3, 3, 3, 3, true)

    val CAT_EARS_ICON = EnhancedTextureIcon(id("textures/gui/cat_ears_icon.png"), 16, 16)
    val TELEPORTATION_RINGS_ICON = EnhancedTextureIcon(id("textures/gui/teleport_at_icon.png"), 16, 16)
}