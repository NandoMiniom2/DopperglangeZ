package com.doppelgangerz.registry;

import com.doppelgangerz.DoppelgangerZ;
import com.doppelgangerz.entity.DoppelgangerEntity;
import net.fabricmc.fabric.api.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModEntities {

    public static EntityType<DoppelgangerEntity> DOPPELGANGER;

    public static void register() {
        DOPPELGANGER = Registry.register(
                Registry.ENTITY_TYPE,
                new Identifier(DoppelgangerZ.MOD_ID, "doppelganger"),
                FabricEntityTypeBuilder.create(SpawnGroup.MISC, DoppelgangerEntity::new)
                        .dimensions(EntityDimensions.fixed(0.6F, 1.8F))
                        .trackRangeChunks(10)
                        .trackedUpdateRate(2)
                        .build()
        );

        // Obrigatorio no Fabric: sem isso a entidade crasha ao ser spawnada.
        FabricDefaultAttributeRegistry.register(DOPPELGANGER, DoppelgangerEntity.createAttributes());
    }
}
