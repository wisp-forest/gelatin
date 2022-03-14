package io.wispforest.jello.api.dye.blockentity;

import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.registry.Registry;
import org.checkerframework.checker.units.qual.C;

public class BlockEntityRegistry implements AutoRegistryContainer<BlockEntityType<?>> {

    public static final BlockEntityType<ColorStorageBlockEntity> COLOR_STORAGE = FabricBlockEntityTypeBuilder.create(ColorStorageBlockEntity::new, Blocks.POWDER_SNOW, Blocks.WATER_CAULDRON).build();

    @Override
    public Registry<BlockEntityType<?>> getRegistry() {
        return Registry.BLOCK_ENTITY_TYPE;
    }

    @Override
    public Class getTargetFieldType() {
        return BlockEntityType.class;
    }
}
