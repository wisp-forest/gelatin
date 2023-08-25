package io.wispforest.gelatin.dye_entries.client;

import com.google.common.collect.ImmutableMap;
import io.wispforest.gelatin.common.misc.GelatinConstants;
import io.wispforest.gelatin.dye_entries.mixins.client.model.ModelLoaderAccessor;
import io.wispforest.gelatin.dye_entries.utils.DyeVariantBuilder;
import io.wispforest.gelatin.dye_entries.variants.DyeableVariantRegistry;
import io.wispforest.gelatin.dye_entries.variants.block.DyeableBlockVariant;
import io.wispforest.gelatin.dye_registry.DyeColorant;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import net.fabricmc.fabric.api.client.model.loading.v1.BlockStateResolver;
import net.fabricmc.fabric.api.client.model.loading.v1.DelegatingUnbakedModel;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelResolver;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.registry.Registries;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;

public class DyeEntriesModelLoader implements ModelResolver, BlockStateResolver  {

    public static final DyeEntriesModelLoader INSTANCE = new DyeEntriesModelLoader();

    private static final Set<DyeableBlockVariant> ALL_BLOCK_VARIANTS = new HashSet<>();

    private static final Map<String, Identifier> BLOCKSTATE_ID_CACHE = new HashMap<>();

    public static void init(){
        var STATIC_DEFS = ModelLoaderAccessor.gelatin$getSTATIC_DEFINITIONS();

        Map<Identifier, StateManager<Block, BlockState>> mutableMap = new LinkedHashMap<>(STATIC_DEFS);

        if (ALL_BLOCK_VARIANTS.isEmpty()) ALL_BLOCK_VARIANTS.addAll(DyeableVariantRegistry.getAllBlockVariants());

        for (DyeableBlockVariant blockVariant : ALL_BLOCK_VARIANTS) {
            String nameSpace = Objects.equals(blockVariant.variantIdentifier.getNamespace(), "minecraft") ? GelatinConstants.MODID : blockVariant.variantIdentifier.getNamespace();

            Identifier baseId = new Identifier(nameSpace, "colored_" + blockVariant.variantIdentifier.getPath());

            Block defaultEntry = blockVariant.getDefaultEntry();

            Collection<Property<?>> properties = defaultEntry.getStateManager().getProperties();

            StateManager<Block, BlockState> customStateManager = new StateManager.Builder<Block, BlockState>(defaultEntry)
                    .add(properties.toArray(new Property[0]))
                    .build(Block::getDefaultState, BlockState::new);

            customStateManager.getStates().forEach(state -> {
                ModelIdentifier identifier = BlockModels.getModelId(baseId, state);

                BLOCKSTATE_ID_CACHE.put(baseId + identifier.getVariant(), identifier);
            });

            mutableMap.put(baseId, customStateManager);
        }

        ModelLoaderAccessor.gelatin$setSTATIC_DEFINITIONS(ImmutableMap.copyOf(mutableMap));

        //---------------------------------

        ModelLoadingPlugin.register(pluginContext -> {
            pluginContext.resolveModel().register(INSTANCE);

            for (DyeableBlockVariant blockVariant : DyeableVariantRegistry.getAllBlockVariants()) {
                List<Block> blocks = new ArrayList<>();

                Block defaultBlock = blockVariant.getDefaultEntry();

                if(!defaultBlock.isDyed() && VariantModelRedirectStorage.shouldRedirectModelResource(Registries.BLOCK.getId(defaultBlock))){
                    blocks.add(defaultBlock);
                }

                for (DyeColorant allColorant : DyeColorantRegistry.getAllColorants()) {
                    Block coloredBlock = blockVariant.getColoredEntry(allColorant);

                    if(VariantModelRedirectStorage.shouldRedirectModelResource(Registries.BLOCK.getId(coloredBlock))){
                        blocks.add(coloredBlock);
                    }
                }

                blocks.forEach(block -> pluginContext.registerBlockStateResolver(block, DyeEntriesModelLoader.INSTANCE));
            }
        });
    }

    //----------------------------------------------------------------------------------------------------------------------

    private static final Map<String, ModelCacheInfo> ITEMS_MODEL_CACHE = new HashMap<>();

    @Override
    @Nullable
    public UnbakedModel resolveModel(ModelResolver.Context context) {
        Identifier id = context.id();

        if(id.getNamespace().equals("minecraft")) return null;

        boolean isItemVersion = id.getPath().contains("item/");

        Identifier baseEntryID = new Identifier(id.getNamespace(), isItemVersion ? id.getPath().replace("item/", "") : id.getPath());

        if (!VariantModelRedirectStorage.shouldRedirectModelResource(baseEntryID) || !isItemVersion) return null;

        String[] stringParts = id.getPath().split("_");

        Identifier baseModelId = null;

        if (ALL_BLOCK_VARIANTS.isEmpty()) ALL_BLOCK_VARIANTS.addAll(DyeableVariantRegistry.getAllBlockVariants());

        if (Objects.equals(stringParts[stringParts.length - 1], "dye")) {
            DyeColorant dyeColorant = Registries.ITEM.get(new Identifier(id.getNamespace(), id.getPath().replace("item/", ""))).getDyeColorant();

            if (dyeColorant == DyeColorantRegistry.NULL_VALUE_NEW) return null;

            baseModelId = GelatinConstants.id("dynamic_dye");
        } else {
            for (DyeableBlockVariant blockVariant : ALL_BLOCK_VARIANTS) {
                if (!blockVariant.isSuchAVariant(id, false)) continue;

                DyeColorant dyeColorant = Registries.BLOCK.get(baseEntryID).getDyeColorant();

                if (dyeColorant == DyeColorantRegistry.NULL_VALUE_NEW) return null;

                String nameSpace = Objects.equals(blockVariant.variantIdentifier.getNamespace(), "minecraft") ? GelatinConstants.MODID : blockVariant.variantIdentifier.getNamespace();

                baseModelId = new Identifier(nameSpace, "colored_" + blockVariant.variantIdentifier.getPath());

                break;
            }
        }

        if (baseModelId != null) {
            UnbakedModel model;

            String key = baseModelId + "/item";

            if (ITEMS_MODEL_CACHE.get(key) != null) {
                model = new DelegatingUnbakedModel(ITEMS_MODEL_CACHE.get(key).id());//ITEMS_MODEL_CACHE.get(key).model();
            } else {
                Identifier modelId = new Identifier(baseModelId.getNamespace(), "item/" + baseModelId.getPath());

                model = loadItemModel(context, modelId);

                ITEMS_MODEL_CACHE.put(key, new ModelCacheInfo(modelId, model));
            }

            return model;
        }

        return null;
    }

    public static UnbakedModel loadItemModel(ModelResolver.Context context, Identifier redirectID) {
        try {
            return ((ModelLoaderAccessor) context.loader()).gelatin$LoadModelFromJson(redirectID);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //----------------------------------------------------------------------------------------------------------------------

    private final Map<String, ModelCacheInfo> BLOCK_MODEL_CACHE = new HashMap<>();

    @Override
    public void resolveBlockStates(BlockStateResolver.Context context) {
        //--
        Block block = context.block();

        if (ALL_BLOCK_VARIANTS.isEmpty()) ALL_BLOCK_VARIANTS.addAll(DyeableVariantRegistry.getAllBlockVariants());

        Map<BlockState, ModelIdentifier> statesToModelIDMap = new LinkedHashMap<>();

        block.getStateManager().getStates().forEach(state -> statesToModelIDMap.put(state, BlockModels.getModelId(state)));

        for (Map.Entry<BlockState, ModelIdentifier> entry : statesToModelIDMap.entrySet()) {
            ModelIdentifier modelId = entry.getValue();

            if (!VariantModelRedirectStorage.shouldRedirectModelResource(new Identifier(modelId.getNamespace(), modelId.getPath()))) {
                return;
            }

            if(Objects.equals(modelId.getPath(), "slime_slab")) return;

            for (DyeableBlockVariant blockVariant : ALL_BLOCK_VARIANTS) {
                if (!blockVariant.isSuchAVariant(block, false)) continue;

                String nameSpace = Objects.equals(blockVariant.variantIdentifier.getNamespace(), "minecraft") ? GelatinConstants.MODID : blockVariant.variantIdentifier.getNamespace();

                Identifier baseModelId = new Identifier(nameSpace, "colored_" + blockVariant.variantIdentifier.getPath());

                UnbakedModel model;

                String key = baseModelId + modelId.getVariant();

                if (BLOCKSTATE_ID_CACHE.get(key) != null) {
                    model = new DelegatingUnbakedModel(BLOCKSTATE_ID_CACHE.get(key));
                } else {
                    throw new IllegalResolverStateException("A Block Variant was found to be missing the needed data to load models, which will cause massive issues! [Variant: " + blockVariant.variantIdentifier + "]");
                }

                context.setModel(entry.getKey(), model);
            }
        }

        //--

    }

    //----------------------------------------------------------------------------------------------------------------------

    public record ModelCacheInfo(Identifier id, UnbakedModel model){
        public boolean isModelID(){
            return this.id() instanceof ModelIdentifier;
        }

        @Nullable
        public ModelIdentifier modelId(){
            return isModelID() ? (ModelIdentifier) this.id() : null;
        }
    }

    public static class IllegalResolverStateException extends IllegalStateException {
        public IllegalResolverStateException(String s) {
            super(s);
        }
    }
}
