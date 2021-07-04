package com.kneelawk.magicalmahou.mixin.impl;

import com.kneelawk.magicalmahou.mixin.api.PlayerEntityEvents;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
    private void onIsInvulnerableTo(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        if (PlayerEntityEvents.IS_INVULNERABLE_TO.invoker().isInvulnerableTo((PlayerEntity) (Object) this, source)) {
            cir.setReturnValue(true);
        }
    }
}
