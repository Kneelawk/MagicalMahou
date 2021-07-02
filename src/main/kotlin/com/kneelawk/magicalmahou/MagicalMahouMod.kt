package com.kneelawk.magicalmahou

import com.kneelawk.magicalmahou.block.MMBlocks
import com.kneelawk.magicalmahou.component.MMComponents
import com.kneelawk.magicalmahou.particle.MMParticles
import com.kneelawk.magicalmahou.screenhandler.MMScreenHandlers

fun init() {
    MMBlocks.init()
    MMScreenHandlers.init()
    MMParticles.init()
    MMComponents.mmInit()
}
