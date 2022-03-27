package io.wispforest.jello.api.mixin.mixins.client.dye;

import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.jello.api.dye.registry.variants.DyeableBlockVariant;
import io.wispforest.jello.api.dye.registry.variants.VanillaBlockVariants;
import io.wispforest.jello.main.common.Jello;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.ItemModelGenerator;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.IOException;
import java.util.*;

@Mixin(ModelLoader.class)
public abstract class ModelLoaderMixin {

    @Shadow public abstract UnbakedModel getOrLoadModel(Identifier id);

    @Shadow @Final public static JsonUnbakedModel GENERATION_MARKER;
    @Shadow private @Nullable SpriteAtlasManager spriteAtlasManager;
    @Shadow @Final private static ItemModelGenerator ITEM_MODEL_GENERATOR;
    @Shadow @Final private Map<Identifier, UnbakedModel> unbakedModels;
    @Shadow @Final public static ModelIdentifier MISSING_ID;
    @Shadow @Final private Map<Identifier, UnbakedModel> modelsToBake;

//    @Unique public Block cachedBlock = Blocks.AIR;
//
//    @Inject(method = "loadModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/json/ModelVariantMap$DeserializationContext;setStateFactory(Lnet/minecraft/state/StateManager;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
//    private void getBlock(Identifier id, CallbackInfo ci, ModelIdentifier modelIdentifier, Identifier identifier, StateManager<Block, BlockState> stateManager){
//        this.cachedBlock = stateManager.getOwner();
//    }

    //---------------------------------------------------------

    @Shadow protected abstract JsonUnbakedModel loadModelFromJson(Identifier id) throws IOException;

    @Shadow protected abstract void putModel(Identifier id, UnbakedModel unbakedModel);

    @Unique private Identifier cachedBlockStateRedirectID;
    @Unique private Identifier cachedItemRedirectID;

    @ModifyVariable(method = "loadModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/json/ModelVariantMap$DeserializationContext;setStateFactory(Lnet/minecraft/state/StateManager;)V", shift = At.Shift.BY, by = 2), argsOnly = true)
    private Identifier changeIdentifierForBlocks(Identifier value){
        if(cachedBlockStateRedirectID != null){
            return cachedBlockStateRedirectID;
        }

        return value;
    }

    //---------------------------------------------------------

//    @Unique private Identifier cachedItemModelID;

    @Inject(method = "loadModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/ModelLoader;loadModelFromJson(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/model/json/JsonUnbakedModel;", ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void changeIdentifierForItems(Identifier id, CallbackInfo ci, ModelIdentifier modelIdentifier, Identifier identifier) throws Exception{
        if(cachedItemRedirectID != null){
            //LOGGER.info("Loading a item model using redirect id: [" + cachedRedirectID + "] / [" + id + "]");

            Identifier redirectedID = new Identifier(cachedItemRedirectID.getNamespace(), "item/" + cachedItemRedirectID.getPath());
            JsonUnbakedModel jsonUnbakedModel = this.loadModelFromJson(redirectedID);

            this.putModel(modelIdentifier, jsonUnbakedModel);
            this.unbakedModels.put(identifier, jsonUnbakedModel);

            ci.cancel();
        }
    }

//    @ModifyVariable(method = "loadModel", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/render/model/ModelLoader;loadModelFromJson(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/model/json/JsonUnbakedModel;", ordinal = 1, shift = At.Shift.BY, by = 2), ordinal = 0)
//    private Identifier changeBackToOriginalID(Identifier value){
//        if(cachedItemModelID != null){
//            return cachedItemModelID;
//        }
//
//        return value;
//    }

    //---------------------------------------------------------

    @Unique private static final Logger LOGGER_MALD = LogManager.getLogger("Jello");

    @Unique private static final Set<DyeableBlockVariant> ALL_VARIANTS = new HashSet<>();

    @Unique private final Map<String, UnbakedModel> test_map = new HashMap<>();

    @Inject(method = "addModel", at = @At("HEAD"), cancellable = true)
    private void checkIfModelIsRedirectable(ModelIdentifier modelId, CallbackInfo ci){
        if (DyeColorantRegistry.shouldRedirectModelResource(new Identifier(modelId.getNamespace(), modelId.getPath()))) {
            String[] stringParts = modelId.getPath().split("_");

            if(ALL_VARIANTS.isEmpty()){
                for(DyeableBlockVariant dyeableBlockVariant : VanillaBlockVariants.VANILLA_VARIANTS){
                    addToListWithRecursion(dyeableBlockVariant);
                }

                for(DyeableBlockVariant dyeableBlockVariant : DyeableBlockVariant.ADDITION_BLOCK_VARIANTS){
                    addToListWithRecursion(dyeableBlockVariant);
                }
            }

            boolean isItemVersion;

            if (Objects.equals(modelId.getVariant(), "inventory")) {
                isItemVersion = true;
            }else{
                isItemVersion = false;
            }

            cachedBlockStateRedirectID = null;
            cachedItemRedirectID = null;

            //TODO: GET WORKING WITH BLOCK VARIANTS!
            for(DyeableBlockVariant blockVariant : ALL_VARIANTS){
                if (blockVariant.isIdentifierAVariant(modelId, isItemVersion)) {
                    String nameSpace = Objects.equals(blockVariant.variantIdentifier.getNamespace(), "minecraft") ? Jello.MODID : blockVariant.variantIdentifier.getNamespace();

                    Identifier identifier = new Identifier(nameSpace, "colored_" + blockVariant.variantIdentifier.getPath());

                    if(!isItemVersion){
                        cachedBlockStateRedirectID = identifier;


                        if(test_map.get(cachedBlockStateRedirectID + modelId.getVariant()) != null){
                            this.unbakedModels.put(modelId, test_map.get(cachedBlockStateRedirectID.toString() + modelId.getVariant()));
                            this.modelsToBake.put(modelId, test_map.get(cachedBlockStateRedirectID.toString() + modelId.getVariant()));

                            ci.cancel();
                        }

                        cachedItemRedirectID = null;
                    }else{
                        cachedItemRedirectID = identifier;

                        if(test_map.get(cachedItemRedirectID + "/item") != null){
                            this.unbakedModels.put(modelId, test_map.get(cachedItemRedirectID + "/item"));
                            this.modelsToBake.put(modelId, test_map.get(cachedItemRedirectID + "/item"));

                            ci.cancel();
                        }

                        cachedBlockStateRedirectID = null;
                    }

                    return;
                }
            }

            if(isItemVersion) {
                if (Objects.equals(stringParts[stringParts.length - 1], "dye")){
                    cachedItemRedirectID = new Identifier(Jello.MODID, "dynamic_dye");

                    if(test_map.get(cachedItemRedirectID + "/item") != null){
                        this.unbakedModels.put(modelId, test_map.get(cachedItemRedirectID + "/item"));
                        this.modelsToBake.put(modelId, test_map.get(cachedItemRedirectID + "/item"));

                        ci.cancel();
                    }

                    cachedBlockStateRedirectID = null;

                    return;
                }
            }

            LOGGER_MALD.error(Arrays.toString(stringParts) + " / " + isItemVersion + " / " + cachedBlockStateRedirectID);
        }else{
            cachedBlockStateRedirectID = null;
            cachedItemRedirectID = null;
        }
    }

    @Inject(method = "addModel", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/render/model/ModelLoader;getOrLoadModel(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/model/UnbakedModel;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void cacheBlockModel(ModelIdentifier modelId, CallbackInfo ci, UnbakedModel unbakedModel){
        if(cachedBlockStateRedirectID != null){
            test_map.putIfAbsent(cachedBlockStateRedirectID + modelId.getVariant(), unbakedModel);
        }else if(cachedItemRedirectID != null){
            test_map.putIfAbsent(cachedItemRedirectID + "/item", unbakedModel);
        }
    }

    private static void addToListWithRecursion(DyeableBlockVariant parentBlockVariant){
        ALL_VARIANTS.add(parentBlockVariant);

        if(parentBlockVariant.childVariant != null){
            addToListWithRecursion(parentBlockVariant.childVariant.get());
        }
    }

}
