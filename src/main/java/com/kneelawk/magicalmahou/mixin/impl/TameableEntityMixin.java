package com.kneelawk.magicalmahou.mixin.impl;

import com.kneelawk.magicalmahou.mixin.api.PlayerEntityEvents;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TameableEntity.class)
public class TameableEntityMixin {

    @Inject(method = "setOwner", at = @At("RETURN"))
    private void onSetOwner(PlayerEntity owner, CallbackInfo ci) {
        PlayerEntityEvents.TAME_ENTITY.invoker().tameEntity(owner, (TameableEntity) (Object) this);
    }
}
