package com.dragon.jello.client.data;

import com.dragon.jello.common.Jello;
import com.dragon.jello.common.blocks.BlockRegistry;
import com.dragon.jello.common.items.ItemRegistry;
import com.dragon.jello.mixin.mixins.client.accessors.ItemModelGeneratorAccessor;
import com.dragon.jello.mixin.mixins.client.accessors.ModelsAccessor;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockStateDefinitionProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.model.*;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class JelloBlockStateProvider extends FabricBlockStateDefinitionProvider {

    private static final TextureKey TEXTURE0 = TextureKey.of("texture0");
    private static final TextureKey TEXTURE1 = TextureKey.of("texture1");

    private static final TextureKey LAYER1 = TextureKey.of("layer1");
    private static final TextureKey LAYER2 = TextureKey.of("layer2");
    private static final TextureKey LAYER3 = TextureKey.of("layer3");

    public JelloBlockStateProvider(FabricDataGenerator dataGenerator) {
        super(dataGenerator);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        BlockRegistry.SlimeBlockRegistry.COLORED_SLIME_BLOCKS.forEach((block) -> {
            registerStateWithModelReferenceSlime(block, blockStateModelGenerator);
        });

        BlockRegistry.SlimeSlabRegistry.COLORED_SLIME_SLABS.forEach((block) -> {
            BlockStateSupplier stateSupplier = BlockStateModelGenerator.createSlabBlockState(block, new Identifier("jello", "block/slime_slab_multicolor"), new Identifier("jello", "block/slime_slab_top_multicolor"), new Identifier("jello", "block/slime_block_multicolor"));

            blockStateModelGenerator.blockStateCollector.accept(stateSupplier);
        });
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        BlockRegistry.SlimeBlockRegistry.COLORED_SLIME_BLOCKS.forEach((block) -> {
            Model model = slimeBlockItemModel(block);
            model.upload(ModelIds.getItemModelId(block.asItem()), new Texture(), ((ItemModelGeneratorAccessor)itemModelGenerator).getWriter());
        });

        BlockRegistry.SlimeSlabRegistry.COLORED_SLIME_SLABS.forEach((block) -> {
            Model model = slimeSlabItemModel(block);
            model.upload(ModelIds.getItemModelId(block.asItem()), new Texture(), ((ItemModelGeneratorAccessor)itemModelGenerator).getWriter());
        });

        ItemRegistry.SlimeBallItemRegistry.SLIME_BALLS.forEach((item) -> {
            Model model = Models.GENERATED;
            model.upload(ModelIds.getItemModelId(item), (new Texture()).put(TextureKey.LAYER0, new Identifier(Jello.MODID, "item/slime_ball_gray")), ((ItemModelGeneratorAccessor)itemModelGenerator).getWriter());
        });

        Model template_cup = ModelsAccessor.callItem("generated", TEXTURE0, TEXTURE1);
        template_cup.upload(new Identifier(Jello.MODID, "item/" + "template_cup"), (new Texture())
                .put(TEXTURE0, new Identifier(Jello.MODID, "item/jello_cup/cup_outline"))
                .put(TEXTURE1, new Identifier(Jello.MODID, "item/jello_cup/cup_translucent_front")), ((ItemModelGeneratorAccessor)itemModelGenerator).getWriter());

        jelloItem("template_cup", TextureKey.LAYER0, LAYER1)
            .upload(ModelIds.getItemModelId(ItemRegistry.JelloCupItemRegistry.SUGAR_CUP), (new Texture())
                .put(TextureKey.LAYER0, new pathOnlyIdentifier(TEXTURE0.getName()))
                .put(LAYER1, new pathOnlyIdentifier(TEXTURE1.getName())), ((ItemModelGeneratorAccessor)itemModelGenerator).getWriter());

        Model jello_cup = jelloItem("template_cup", TextureKey.LAYER0, LAYER1, LAYER2, LAYER3); //LAYER2, LAYER3); //jelloItem("generated", LAYER2, LAYER3);

        ItemRegistry.JelloCupItemRegistry.JELLO_CUP.forEach((item) -> {
            jello_cup.upload(ModelIds.getItemModelId(item), (new Texture())
                    .put(TextureKey.LAYER0, new pathOnlyIdentifier(TEXTURE0.getName()))
                    .put(LAYER1, new pathOnlyIdentifier(TEXTURE1.getName()))
                    .put(LAYER2, new Identifier(Jello.MODID, "item/jello_cup/jello_front"))
                    .put(LAYER3, new Identifier(Jello.MODID, "item/jello_cup/jello_top")), ((ItemModelGeneratorAccessor)itemModelGenerator).getWriter());
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

    private static Model jelloItem(String parent, TextureKey... requiredTextures) {
        return new Model(Optional.of(new Identifier(Jello.MODID, "item/" + parent)), Optional.empty(), requiredTextures);
    }

    public static class pathOnlyIdentifier extends Identifier{

        protected pathOnlyIdentifier(String[] id) {
            super(id);
        }

        public pathOnlyIdentifier(String id) {
            super(id);
        }

        public pathOnlyIdentifier(String namespace, String path) {
            super(namespace, path);
        }

        @Override
        public String toString() {
            return "#" + this.path;
        }
    }
}
