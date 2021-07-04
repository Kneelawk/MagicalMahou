package com.kneelawk.magicalmahou.mixin.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.TameableEntity;
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

    public static final Event<TameEntity> TAME_ENTITY =
            EventFactory.createArrayBacked(TameEntity.class, (player, tamed) -> {
            }, callbacks -> (player, tamed) -> {
                for (TameEntity callback : callbacks) {
                    callback.tameEntity(player, tamed);
                }
            });

    public interface IsInvulnerableTo {
        boolean isInvulnerableTo(PlayerEntity player, DamageSource source);
    }

    public interface TameEntity {
        void tameEntity(PlayerEntity player, TameableEntity tamed);
    }
}
