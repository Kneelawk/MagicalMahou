package com.kneelawk.magicalmahou.mixin.api;

import com.kneelawk.magicalmahou.mixin.impl.MatrixStackEntryAccessor;
import net.minecraft.client.util.math.MatrixStack;

public class MatrixStackUtils {
    public static MatrixStack.Entry copyEntry(MatrixStack.Entry entry) {
        return MatrixStackEntryAccessor.magicalmahou$create(entry.getPositionMatrix().copy(), entry.getNormalMatrix().copy());
    }
}
