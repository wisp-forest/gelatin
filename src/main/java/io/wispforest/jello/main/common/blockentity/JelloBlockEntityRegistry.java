package io.wispforest.jello.main.common.blockentity;

import io.wispforest.jello.main.common.Jello;
import io.wispforest.jello.main.common.blocks.JelloBlockRegistry;
import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class JelloBlockEntityRegistry implements AutoRegistryContainer<BlockEntityType<?>> {

    public static BlockEntityType<ColorMixerBlockEntity> COLOR_MIXER = FabricBlockEntityTypeBuilder.create(ColorMixerBlockEntity::new, JelloBlockRegistry.COLOR_MIXER).build();

    @Override
    public Registry<BlockEntityType<?>> getRegistry() {
        return Registry.BLOCK_ENTITY_TYPE;
    }

    @Override
    public Class<BlockEntityType<?>> getTargetFieldType() {
        return (Class<BlockEntityType<?>>)(Object)BlockEntityType.class;
    }
}
