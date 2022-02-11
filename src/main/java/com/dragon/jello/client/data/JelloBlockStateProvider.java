package com.dragon.jello.client.data;

import com.dragon.jello.Jello;
import com.dragon.jello.blocks.BlockRegistry;
import com.dragon.jello.items.ItemRegistry;
import com.dragon.jello.mixin.mixins.ItemModelGeneratorAccessor;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockStateDefinitionProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.model.*;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Optional;

public class JelloBlockStateProvider extends FabricBlockStateDefinitionProvider {
    public JelloBlockStateProvider(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        BlockRegistry.SlimeBlockRegistry.SLIME_BLOCKS.forEach((block) -> {
            registerStateWithModelReferenceSlime(block, blockStateModelGenerator);
        });

        BlockRegistry.SlimeSlabRegistry.SLIME_SLABS.forEach((block) -> {
            BlockStateSupplier stateSupplier = BlockStateModelGenerator.createSlabBlockState(block, new Identifier("jello", "block/slime_slab_multicolor"), new Identifier("jello", "block/slime_slab_top_multicolor"), new Identifier("jello", "block/slime_block_multicolor"));

            blockStateModelGenerator.blockStateCollector.accept(stateSupplier);
        });
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        BlockRegistry.SlimeBlockRegistry.SLIME_BLOCKS.forEach((block) -> {
            Model model = slimeBlockItemModel(block);
            model.upload(ModelIds.getItemModelId(block.asItem()), new Texture(), ((ItemModelGeneratorAccessor)itemModelGenerator).getWriter());
        });

        BlockRegistry.SlimeSlabRegistry.SLIME_SLABS.forEach((block) -> {
            Model model = slimeSlabItemModel(block);
            model.upload(ModelIds.getItemModelId(block.asItem()), new Texture(), ((ItemModelGeneratorAccessor)itemModelGenerator).getWriter());
        });

        ItemRegistry.SlimeBlockItemRegistry.SLIME_BALLS.forEach((item) -> {
            Model model = Models.GENERATED;
            model.upload(ModelIds.getItemModelId(item), (new Texture()).put(TextureKey.LAYER0, new Identifier(Jello.MODID, "item/slime_ball_gray")), ((ItemModelGeneratorAccessor)itemModelGenerator).getWriter());
        });
    }

    private Model slimeBlockItemModel(Block block){
       return new Model(Optional.of(new Identifier(Jello.MODID, "block/slime_block_multicolor")), Optional.empty());
    }
    private Model slimeSlabItemModel(Block block){
        return new Model(Optional.of(new Identifier(Jello.MODID, "block/slime_slab_multicolor")), Optional.empty());
    }

    private Model slimeBallItemModel(Item item){
        return new Model(Optional.of(new Identifier(Jello.MODID, "block/generated")), Optional.empty());
    }

    public final void registerStateWithModelReferenceSlime(Block block, BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, new Identifier(Jello.MODID, "block/slime_block_multicolor")));
    }

}
