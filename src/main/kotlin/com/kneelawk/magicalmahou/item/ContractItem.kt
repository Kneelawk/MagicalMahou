package com.kneelawk.magicalmahou.item

import com.kneelawk.magicalmahou.MMConstants.tt
import com.kneelawk.magicalmahou.component.MMAbilityComponent
import com.kneelawk.magicalmahou.component.MMComponents
import dev.onyxstudios.cca.api.v3.component.ComponentKey
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

class ContractItem(
    private val key: ComponentKey<out MMAbilityComponent<*>>, private val acceptName: String, settings: Settings
) : Item(settings) {
    override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        val stack = user.getStackInHand(hand)
        val component = key[user]

        return if (!component.getPlayerHasComponent() && MMComponents.isMagical(user)) {
            if (!world.isClient) {
                component.serverGiveAbility()
                user.sendMessage(tt("message", "accept.$acceptName"), false)
            }

            if (!user.abilities.creativeMode) {
                stack.decrement(1)
            }

            TypedActionResult.success(stack, world.isClient)
        } else {
            TypedActionResult.pass(stack)
        }
    }
}