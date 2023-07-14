package io.wispforest.gelatin.cauldron.blockentity;

import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class GelatinBlockEntityTypes implements AutoRegistryContainer<BlockEntityType<?>> {

    public static final BlockEntityType<ColorStorageBlockEntity> COLOR_STORAGE = FabricBlockEntityTypeBuilder.create(ColorStorageBlockEntity::new, Blocks.WATER_CAULDRON).build();

    @Override
    public Registry<BlockEntityType<?>> getRegistry() {
        return Registries.BLOCK_ENTITY_TYPE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<BlockEntityType<?>> getTargetFieldType() {
        return (Class<BlockEntityType<?>>) (Object) BlockEntityType.class;
    }
}
