package com.kneelawk.magicalmahou.mixin.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;

public class PlayerEntityEvents {

    public static final Event<IsInvulnerableTo> IS_INVULNERABLE_TO =
            EventFactory.createArrayBacked(IsInvulnerableTo.class, (player, source) -> false,
                    callbacks -> (player, source) -> {
                        boolean isInvulnerable = false;

                        for (IsInvulnerableTo callback : callbacks) {
                            isInvulnerable |= callback.isInvulnerableTo(player, source);
                        }

                        return isInvulnerable;
                    });

    public interface IsInvulnerableTo {
        boolean isInvulnerableTo(PlayerEntity player, DamageSource source);
    }
}
