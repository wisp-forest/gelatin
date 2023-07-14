package io.wispforest.jello.blockentity;

import io.wispforest.jello.block.JelloBlocks;
import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class JelloBlockEntityTypes implements AutoRegistryContainer<BlockEntityType<?>> {

    public static final BlockEntityType<ColorMixerBlockEntity> COLOR_MIXER = FabricBlockEntityTypeBuilder.create(ColorMixerBlockEntity::new, JelloBlocks.PAINT_MIXER).build();

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
