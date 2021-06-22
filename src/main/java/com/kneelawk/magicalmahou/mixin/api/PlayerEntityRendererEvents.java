package com.kneelawk.magicalmahou.mixin.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;

import java.util.function.Consumer;

public class PlayerEntityRendererEvents {

    public static final Event<AddFeatures> ADD_FEATURES =
            EventFactory.createArrayBacked(AddFeatures.class, (renderer, ctx, consumer) -> {
            }, callbacks -> (renderer, ctx, consumer) -> {
                for (final AddFeatures callback : callbacks) {
                    callback.addFeatures(renderer, ctx, consumer);
                }
            });

    public interface AddFeatures {
        void addFeatures(PlayerEntityRenderer renderer, EntityRendererFactory.Context ctx,
                         Consumer<FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>> consumer);
    }
}
