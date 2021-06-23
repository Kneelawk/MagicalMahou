package com.kneelawk.magicalmahou.mixin.impl;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MatrixStack.Entry.class)
public interface MatrixStackEntryAccessor {
    @Invoker("<init>")
    static MatrixStack.Entry magicalmahou$create(Matrix4f matrix4f, Matrix3f matrix3f) {
        throw new IllegalStateException("MatrixStackEntryAccessor mixin error");
    }
}
