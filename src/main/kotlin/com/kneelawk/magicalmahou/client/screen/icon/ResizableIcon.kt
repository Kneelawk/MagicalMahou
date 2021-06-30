package com.kneelawk.magicalmahou.client.screen.icon

import com.kneelawk.magicalmahou.client.screen.icon.EnhancedIcon

interface ResizableIcon : EnhancedIcon {
    val minWidth: Int
    val minHeight: Int

    override val baseWidth: Int
        get() = minWidth
    override val baseHeight: Int
        get() = minHeight
}