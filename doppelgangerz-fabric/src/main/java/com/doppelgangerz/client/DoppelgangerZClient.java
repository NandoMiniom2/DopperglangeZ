package com.doppelgangerz.client;

import com.doppelgangerz.client.render.DoppelgangerEntityRenderer;
import com.doppelgangerz.registry.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class DoppelgangerZClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(ModEntities.DOPPELGANGER, DoppelgangerEntityRenderer::new);
    }
}
