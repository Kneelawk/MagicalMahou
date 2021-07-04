package com.kneelawk.magicalmahou.component

import dev.onyxstudios.cca.api.v3.component.Component
import dev.onyxstudios.cca.api.v3.component.ComponentKey
import io.github.cottonmc.cotton.gui.widget.icon.Icon
import net.minecraft.text.Text

interface MMAbilityComponent<C : Component> : Component {
    val name: Text
    val icon: Icon
    val key: ComponentKey<C>

    fun getPlayerHasComponent(): Boolean

    fun serverGiveAbility()
}