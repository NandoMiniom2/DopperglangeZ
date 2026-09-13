package com.doppelgangerz.client.render;

import com.doppelgangerz.entity.DoppelgangerEntity;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerSkinProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.util.Identifier;

/**
 * Renderiza o Doppelganger usando a skin do jogador copiado (Secao 6).
 *
 * Nesta Beta nao ha corrupcao visual progressiva - a textura usada e sempre
 * a skin normal do jogador. Corrupcao gradual (Secoes 6 e 38) fica para a
 * proxima iteracao, ver README_BETA.md.
 */
public class DoppelgangerEntityRenderer extends MobEntityRenderer<DoppelgangerEntity, PlayerEntityModel<DoppelgangerEntity>> {

    private static final Identifier FALLBACK_SKIN = new Identifier("minecraft", "textures/entity/steve.png");

    public DoppelgangerEntityRenderer(EntityRenderDispatcher dispatcher) {
        super(dispatcher, new PlayerEntityModel<>(0.0F, false), 0.5F);
    }

    @Override
    public Identifier getTexture(DoppelgangerEntity entity) {
        if (entity.getCopiedProfile() == null) return FALLBACK_SKIN;

        PlayerSkinProvider skinProvider = MinecraftClient.getInstance().getSkinProvider();
        MinecraftProfileTexture texture = skinProvider.getTextures(entity.getCopiedProfile())
                .get(MinecraftProfileTexture.Type.SKIN);
        if (texture == null) return FALLBACK_SKIN;

        Identifier cached = skinProvider.loadSkin(texture, MinecraftProfileTexture.Type.SKIN);
        return cached == null ? FALLBACK_SKIN : cached;
    }
}
