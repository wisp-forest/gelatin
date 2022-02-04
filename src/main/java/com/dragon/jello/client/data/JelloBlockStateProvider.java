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
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        BlockRegistry.SlimeBlockRegistry.SLIME_BLOCKS.forEach((block) -> {
            Model model = slimeBlockItemModel(block);
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

    private Model slimeBallItemModel(Item item){
        return new Model(Optional.of(new Identifier(Jello.MODID, "block/generated")), Optional.empty());
    }

    public final void registerStateWithModelReferenceSlime(Block block, BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, new Identifier(Jello.MODID, "block/slime_block_multicolor")));
    }

}
