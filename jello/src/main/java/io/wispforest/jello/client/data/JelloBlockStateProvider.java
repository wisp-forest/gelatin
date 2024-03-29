package io.wispforest.jello.client.data;

import io.wispforest.jello.mixins.client.accessors.ItemModelGeneratorAccessor;
import io.wispforest.jello.mixins.client.accessors.ModelsAccessor;
import io.wispforest.jello.Jello;
import io.wispforest.jello.item.JelloItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.*;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class JelloBlockStateProvider extends FabricModelProvider {

    private static final TextureKey TEXTURE0 = TextureKey.of("texture0");
    private static final TextureKey TEXTURE1 = TextureKey.of("texture1");

    private static final TextureKey LAYER0 = TextureKey.LAYER0;
    private static final TextureKey LAYER1 = TextureKey.of("layer1");
    private static final TextureKey LAYER2 = TextureKey.of("layer2");
    private static final TextureKey LAYER3 = TextureKey.of("layer3");

    public JelloBlockStateProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
//        BlockRegistry.SlimeBlockRegistry.COLORED_SLIME_BLOCKS.forEach((block) -> {
//            registerStateWithModelReferenceSlime(block, blockStateModelGenerator);
//        });
//
//        BlockRegistry.SlimeSlabRegistry.COLORED_SLIME_SLABS.forEach((block) -> {
//            BlockStateSupplier stateSupplier = BlockStateModelGenerator.createSlabBlockState(block, new Identifier("jello", "block/slime_slab_multicolor"), new Identifier("jello", "block/slime_slab_top_multicolor"), new Identifier("jello", "block/slime_block_multicolor"));
//
//            blockStateModelGenerator.blockStateCollector.accept(stateSupplier);
//        });
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
//        BlockRegistry.SlimeBlockRegistry.COLORED_SLIME_BLOCKS.forEach((block) -> {
//            Model model = slimeBlockItemModel(block);
//            model.upload(ModelIds.getItemModelId(block.asItem()), new TextureMap(), ((ItemModelGeneratorAccessor)itemModelGenerator).getWriter());
//        });
//
//        BlockRegistry.SlimeSlabRegistry.COLORED_SLIME_SLABS.forEach((block) -> {
//            Model model = slimeSlabItemModel(block);
//            model.upload(ModelIds.getItemModelId(block.asItem()), new TextureMap(), ((ItemModelGeneratorAccessor)itemModelGenerator).getWriter());
//        });

        JelloItems.Slimeballs.SLIME_BALLS.forEach((item) -> {
            Models.GENERATED
                    .upload(ModelIds.getItemModelId(item),
                            new TextureMap().put(LAYER0, Jello.id("item/slime_ball_gray")),
                            ((ItemModelGeneratorAccessor) itemModelGenerator).jello$getWriter());
        });

        //---

        ModelsAccessor.jello$item("generated", LAYER0, LAYER1)
                .upload(Jello.id("item/" + "gelatin_solution"),
                        new TextureMap()
                                .put(LAYER0, Jello.id("item/gelatin_solution/gelatin_solution_bucket"))
                                .put(LAYER1, Jello.id("item/gelatin_solution/gelatin_solution_liquid")),
                        ((ItemModelGeneratorAccessor) itemModelGenerator).jello$getWriter());

        //---

        ModelsAccessor.jello$item("generated", TEXTURE0, TEXTURE1)
                .upload(Jello.id("item/" + "template_cup"),
                        new TextureMap()
                                .put(TEXTURE0, Jello.id("item/jello_cup/cup_outline"))
                                .put(TEXTURE1, Jello.id("item/jello_cup/cup_translucent_front")),
                        ((ItemModelGeneratorAccessor) itemModelGenerator).jello$getWriter());


        jelloItem("template_cup", LAYER0, LAYER1, LAYER2)
                .upload(ModelIds.getItemModelId(JelloItems.JelloCups.SUGAR_CUP),
                        (new TextureMap())
                                .put(LAYER0, new PathOnlyIdentifier(TEXTURE0.getName()))
                                .put(LAYER1, new PathOnlyIdentifier(TEXTURE1.getName()))
                                .put(LAYER2, Jello.id("item/jello_cup/cup_translucent_top")),
                        ((ItemModelGeneratorAccessor) itemModelGenerator).jello$getWriter());

        Model jello_cup = jelloItem("template_cup", LAYER0, LAYER1, LAYER2, LAYER3); //LAYER2, LAYER3); //jelloItem("generated", LAYER2, LAYER3);

        JelloItems.JelloCups.JELLO_CUPS.forEach((item) -> {
            jello_cup.upload(ModelIds.getItemModelId(item),
                    (new TextureMap())
                            .put(LAYER0, new PathOnlyIdentifier(TEXTURE0.getName()))
                            .put(LAYER1, new PathOnlyIdentifier(TEXTURE1.getName()))
                            .put(LAYER2, Jello.id("item/jello_cup/jello_front"))
                            .put(LAYER3, Jello.id("item/jello_cup/jello_top")),
                    ((ItemModelGeneratorAccessor) itemModelGenerator).jello$getWriter());
        });

        for (int i = 1; i < 8; i++) {

            Models.GENERATED
                    .upload(new Identifier(Jello.MODID, "item/sponge_stage_" + i),
                            TextureMap.layer0(Jello.id("item/sponge/sponge_stage_" + i)),
                            ((ItemModelGeneratorAccessor) itemModelGenerator).jello$getWriter());
        }

        for (int i = 1; i < 9; i++) {
            Models.GENERATED
                    .upload(new Identifier(Jello.MODID, "item/dye_texture_var_" + i),
                            TextureMap.layer0(Jello.id("item/dye_item_variant/var_" + i)),
                            ((ItemModelGeneratorAccessor) itemModelGenerator).jello$getWriter());
        }
    }

    private Model slimeBlockItemModel(Block block) {
        return new Model(Optional.of(Jello.id("block/slime_block_multicolor")), Optional.empty());
    }

    private Model slimeSlabItemModel(Block block) {
        return new Model(Optional.of(Jello.id("block/slime_slab_multicolor")), Optional.empty());
    }

    private Model slimeBallItemModel(Item item) {
        return new Model(Optional.of(Jello.id("block/generated")), Optional.empty());
    }

    public final void registerStateWithModelReferenceSlime(Block block, BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, Jello.id("block/slime_block_multicolor")));
    }

    private static Model jelloItem(String parent, TextureKey... requiredTextures) {
        return new Model(Optional.of(Jello.id("item/" + parent)), Optional.empty(), requiredTextures);
    }

    public static class PathOnlyIdentifier extends Identifier {

        public PathOnlyIdentifier(String id) {
            super(id);
        }

        public PathOnlyIdentifier(String namespace, String path) {
            super(namespace, path);
        }

        @Override
        public String toString() {
            return "#" + this.getPath();
        }
    }
}
