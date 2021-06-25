package com.kneelawk.magicalmahou.mixin.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class PlayerEntityRendererEvents {
    private static final ThreadLocal<Boolean> ENABLE_SKIN_TEXTURE_EVENT = ThreadLocal.withInitial(() -> true);

    public static final Event<AddFeatures> ADD_FEATURES =
            EventFactory.createArrayBacked(AddFeatures.class, (renderer, ctx, slim, consumer) -> {
            }, callbacks -> (renderer, ctx, slim, consumer) -> {
                for (final AddFeatures callback : callbacks) {
                    callback.addFeatures(renderer, ctx, slim, consumer);
                }
            });

    public static void setSkinTextureEventEnabled(boolean enabled) {
        ENABLE_SKIN_TEXTURE_EVENT.set(enabled);
    }

    @Nullable
    public static Identifier getSkinTexture(AbstractClientPlayerEntity player) {
        if (ENABLE_SKIN_TEXTURE_EVENT.get()) {
            return GET_SKIN_TEXTURE.invoker().getSkinTexture(player);
        } else {
            return null;
        }
    }

    public static final Event<SkinTexture> GET_SKIN_TEXTURE =
            EventFactory.createArrayBacked(SkinTexture.class, ctx -> null, callbacks -> player -> {
                Identifier override = null;

                for (final SkinTexture callback : callbacks) {
                    Identifier newOverride = callback.getSkinTexture(player);
                    if (override == null && newOverride != null) {
                        override = newOverride;
                    }
                }

                return override;
            });

    public interface AddFeatures {
        void addFeatures(PlayerEntityRenderer renderer, EntityRendererFactory.Context ctx, boolean slim,
                         Consumer<FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>> consumer);
    }

    public interface SkinTexture {
        @Nullable
        Identifier getSkinTexture(AbstractClientPlayerEntity player);
    }
}
