package com.kneelawk.magicalmahou.mixin.impl;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.PlayerSkinTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.io.InputStream;

@Mixin(PlayerSkinTexture.class)
public interface PlayerSkinTextureAccessor {
    @Invoker
    NativeImage callLoadTexture(InputStream stream);
}
