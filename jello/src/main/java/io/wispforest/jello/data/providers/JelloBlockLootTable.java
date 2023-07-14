package io.wispforest.jello.data.providers;

import io.wispforest.jello.block.JelloBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;

public class JelloBlockLootTable extends FabricBlockLootTableProvider {
    public JelloBlockLootTable(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate() {
        this.addDrop(JelloBlocks.PAINT_MIXER);
        this.addDrop(JelloBlocks.SLIME_SLAB);
    }
}
