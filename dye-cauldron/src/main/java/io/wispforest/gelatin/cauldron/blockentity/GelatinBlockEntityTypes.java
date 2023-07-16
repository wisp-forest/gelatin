package io.wispforest.gelatin.cauldron.blockentity;

import io.wispforest.gelatin.common.misc.GelatinConstants;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class GelatinBlockEntityTypes {

    public static final BlockEntityType<ColorStorageBlockEntity> COLOR_STORAGE = FabricBlockEntityTypeBuilder.create(ColorStorageBlockEntity::new, Blocks.WATER_CAULDRON).build();

    public static void init(){
        Registry.register(Registries.BLOCK_ENTITY_TYPE, GelatinConstants.id("color_storage"), COLOR_STORAGE);
    }
}
