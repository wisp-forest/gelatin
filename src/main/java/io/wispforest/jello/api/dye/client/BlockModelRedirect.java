package io.wispforest.jello.api.dye.client;

import io.wispforest.jello.api.dye.block.ColoredCandleBlock;
import io.wispforest.jello.api.dye.block.ColoredCandleCakeBlock;
import io.wispforest.jello.api.dye.block.ColoredGlassPaneBlock;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.jello.api.dye.registry.variants.DyeableBlockVariant;
import io.wispforest.jello.api.dye.registry.variants.VanillaBlockVariants;
import io.wispforest.jello.api.util.MessageUtil;
import io.wispforest.jello.main.common.Jello;
import io.wispforest.jello.main.common.blocks.SlimeBlockColored;
import io.wispforest.jello.main.common.blocks.SlimeSlabColored;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelVariantProvider;
import net.minecraft.block.Block;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

public class BlockModelRedirect implements ModelVariantProvider {

    private static final MessageUtil MESSAGE_TOOL = new MessageUtil("Block Model Redirect");

    private static final Set<DyeableBlockVariant> ALL_VARIANTS = new HashSet<>();

    @Override
    public @Nullable UnbakedModel loadModelVariant(ModelIdentifier modelId, ModelProviderContext context) throws ModelProviderException {
//        if(ALL_VARIANTS.isEmpty()){
//            for(DyeableBlockVariant dyeableBlockVariant : VanillaBlockVariants.VANILLA_VARIANTS){
//                addToListWithRecursion(dyeableBlockVariant);
//            }
//
//            for(DyeableBlockVariant dyeableBlockVariant : DyeableBlockVariant.ADDITION_BLOCK_VARIANTS){
//                addToListWithRecursion(dyeableBlockVariant);
//            }
//        }

        if (DyeColorantRegistry.shouldRedirectModelResource(new Identifier(modelId.getNamespace(), modelId.getPath()))) {
            //if(Objects.equals(modelId.getNamespace(), DyeColorantJsonTest.JSON_NAMESPACE)){
            String[] stringParts = modelId.getPath().split("_");

            //MESSAGE_TOOL.infoMessage(Arrays.toString(stringParts));



            if (Objects.equals(stringParts[stringParts.length - 1], "dye")) {
                return context.loadModel(new Identifier("jello", "item/dynamic_dye"));
            }

            String loadFromDirectory;
            boolean isItemVersion;
            if (Objects.equals(modelId.getVariant(), "inventory")) {
                loadFromDirectory = "item";
                isItemVersion = true;
            } else {
                loadFromDirectory = "block";
                isItemVersion = false;

                return null;
            }

            //TODO: GET WORKING WITH BLOCK VARIANTS!
//            for(DyeableBlockVariant blockVariant : ALL_VARIANTS){
//                if (blockVariant.isIdentifierAVariant(modelId, isItemVersion)) {
//                    return context.loadModel(new Identifier(blockVariant.variantIdentifier.getNamespace(), loadFromDirectory + "/" + blockVariant.variantIdentifier.getPath()));
//                }
//
//            }

            if (Objects.equals(stringParts[stringParts.length - 1], "terracotta")) {
                return context.loadModel(new Identifier("jello", loadFromDirectory + "/terracotta"));
            } else if (Objects.equals(stringParts[stringParts.length - 1], "carpet")) {
                return context.loadModel(new Identifier("jello", loadFromDirectory + "/carpet"));
            } else if (Objects.equals(stringParts[stringParts.length - 1], "concrete")) {
                return context.loadModel(new Identifier("jello", loadFromDirectory + "/concrete"));
            } else if (Objects.equals(stringParts[stringParts.length - 1], "powder")) {
                return context.loadModel(new Identifier("jello", loadFromDirectory + "/concrete_powder"));
            } else if (Objects.equals(stringParts[stringParts.length - 1], "wool")) {
                return context.loadModel(new Identifier("jello", loadFromDirectory + "/wool"));
            } else if (Objects.equals(stringParts[stringParts.length - 1], "box")) {
                return context.loadModel(new Identifier(loadFromDirectory + "/shulker_box"));
            } else if (Objects.equals(stringParts[stringParts.length - 1], "bed")) {
                if (loadFromDirectory.equals("item")) {
                    return context.loadModel(new Identifier(loadFromDirectory + "/template_bed"));
                }
                return context.loadModel(new Identifier(loadFromDirectory + "/bed"));
            } else if (Objects.equals(stringParts[stringParts.length - 1], "glass")) {
                return context.loadModel(new Identifier("jello", loadFromDirectory + "/stained_glass"));
            } else if (Objects.equals(stringParts[stringParts.length - 1], "pane")) {
                if (loadFromDirectory.equals("item")) {
                    return context.loadModel(new Identifier("jello", loadFromDirectory + "/stained_glass_pane"));
                }

                return context.loadModel(new Identifier(Jello.MODID, "blockstate" + "/stained_glass_pane"));
            }else if(Objects.equals(stringParts[stringParts.length - 1], "candle")){
                if (loadFromDirectory.equals("item")) {
                    return context.loadModel(new Identifier(loadFromDirectory + "/white_candle"));
                }

                return null;
            }else if(Objects.equals(stringParts[stringParts.length - 1], "cake")){
                return null;
            }else if(Objects.equals(stringParts[stringParts.length - 1], "block")){
                if (loadFromDirectory.equals("item")) {
                    return context.loadModel(new Identifier("jello", loadFromDirectory + "/colored_slime_block"));
                }
            }else if(Objects.equals(stringParts[stringParts.length - 1], "slab")){
                if (loadFromDirectory.equals("item")) {
                    return context.loadModel(new Identifier("jello", loadFromDirectory + "/colored_slime_slab"));
                }
            }
//
////            UnbakedModel possibleModel = context.loadModel(modelId);
////            if(MISSING_MODEL == null){
////                MISSING_MODEL = context.loadModel(ModelLoader.MISSING_ID);
////            }
////
////            if(possibleModel == MISSING_MODEL){
////                MESSAGE_TOOL.infoMessage(Arrays.toString(stringParts));
////                MESSAGE_TOOL.failMessage("{Deetz Nuts} Failed to find model for " + modelId.toString());
////            }
        }

        return null;
    }

    public static void addToListWithRecursion(DyeableBlockVariant parentBlockVariant){
        ALL_VARIANTS.add(parentBlockVariant);

        if(parentBlockVariant.childVariant != null){
            addToListWithRecursion(parentBlockVariant.childVariant.get());
        }
    }

    public static class ResourceRedirectEntryPredicate implements Predicate<Block> {

        public static final Map<ResourceRedirectEntryPredicate, Identifier> BLOCKSTATE_PREDICATES = new HashMap<>();

        private static final ResourceRedirectEntryPredicate GLASS_PANE_PREDICATE = new ResourceRedirectEntryPredicate(ColoredGlassPaneBlock.class, new Identifier(Jello.MODID, "stained_glass_pane"));
        private static final ResourceRedirectEntryPredicate CANDLE_PREDICATE = new ResourceRedirectEntryPredicate(ColoredCandleBlock.class, new Identifier(Jello.MODID, "candle"));
        private static final ResourceRedirectEntryPredicate CANDLE_CAKE_PREDICATE = new ResourceRedirectEntryPredicate(ColoredCandleCakeBlock.class, new Identifier(Jello.MODID, "candle_cake"));

        private static final ResourceRedirectEntryPredicate SLIME_BLOCK_PREDICATE = new ResourceRedirectEntryPredicate(SlimeBlockColored.class, new Identifier(Jello.MODID, "colored_slime_block"));
        private static final ResourceRedirectEntryPredicate SLIME_SLAB_PREDICATE = new ResourceRedirectEntryPredicate(SlimeSlabColored.class, new Identifier(Jello.MODID, "colored_slime_slab"));

        private final Class<? extends Block> klazz;

        public ResourceRedirectEntryPredicate(Class<? extends Block> klazz, Identifier resourceLocation){
            this.klazz = klazz;

            BLOCKSTATE_PREDICATES.put(this, resourceLocation);
        }

        @Override
        public boolean test(Block t) {
            return klazz.isInstance(t);
        }
    }
}