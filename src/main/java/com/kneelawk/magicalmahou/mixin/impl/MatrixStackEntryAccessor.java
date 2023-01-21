package com.kneelawk.magicalmahou.mixin.impl;

import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MatrixStack.Entry.class)
public interface MatrixStackEntryAccessor {
    @Invoker("<init>")
    static MatrixStack.Entry magicalmahou$create(Matrix4f matrix4f, Matrix3f matrix3f) {
        throw new IllegalStateException("MatrixStackEntryAccessor mixin error");
    }
}
