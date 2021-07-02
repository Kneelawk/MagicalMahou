package com.kneelawk.magicalmahou.component

import dev.onyxstudios.cca.api.v3.component.Component
import io.github.cottonmc.cotton.gui.widget.icon.Icon
import net.minecraft.text.Text

interface MMAbilityComponent : Component {
    val name: Text
    val icon: Icon

    fun getPlayerHasComponent(): Boolean
}