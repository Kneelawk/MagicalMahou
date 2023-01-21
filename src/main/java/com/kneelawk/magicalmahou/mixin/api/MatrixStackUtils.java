package com.kneelawk.magicalmahou.mixin.api;

import com.kneelawk.magicalmahou.mixin.impl.MatrixStackEntryAccessor;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class MatrixStackUtils {
    public static MatrixStack.Entry copyEntry(MatrixStack.Entry entry) {
        return MatrixStackEntryAccessor.magicalmahou$create(new Matrix4f(entry.getPositionMatrix()),
            new Matrix3f(entry.getNormalMatrix()));
    }
}
