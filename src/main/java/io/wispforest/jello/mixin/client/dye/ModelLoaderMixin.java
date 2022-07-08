package io.wispforest.jello.mixin.client.dye;

import io.wispforest.jello.Jello;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.jello.api.dye.registry.variants.block.DyeableBlockVariant;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
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

    @Shadow
    @Final
    private Map<Identifier, UnbakedModel> unbakedModels;

    @Shadow
    @Final
    private Map<Identifier, UnbakedModel> modelsToBake;

    //---------------------------------------------------------

    @Shadow
    protected abstract JsonUnbakedModel loadModelFromJson(Identifier id) throws IOException;

    @Shadow
    protected abstract void putModel(Identifier id, UnbakedModel unbakedModel);

    @Unique private Identifier cachedBlockStateRedirectID;
    @Unique private Identifier cachedItemRedirectID;

    @ModifyVariable(method = "loadModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/json/ModelVariantMap$DeserializationContext;setStateFactory(Lnet/minecraft/state/StateManager;)V", shift = At.Shift.BY, by = 2), argsOnly = true)
    private Identifier changeIdentifierForBlocks(Identifier value) {
        if (cachedBlockStateRedirectID != null) {
            return cachedBlockStateRedirectID;
        }

        return value;
    }

    //---------------------------------------------------------

    @Inject(method = "loadModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/ModelLoader;loadModelFromJson(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/model/json/JsonUnbakedModel;", ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void changeIdentifierForItems(Identifier id, CallbackInfo ci, ModelIdentifier modelIdentifier, Identifier identifier) throws Exception {
        if (cachedItemRedirectID != null) {
            //LOGGER.info("Loading an item model using redirect id: [" + cachedRedirectID + "] / [" + id + "]");

            Identifier redirectedID = new Identifier(cachedItemRedirectID.getNamespace(), "item/" + cachedItemRedirectID.getPath());
            JsonUnbakedModel jsonUnbakedModel = this.loadModelFromJson(redirectedID);

            this.putModel(modelIdentifier, jsonUnbakedModel);
            this.unbakedModels.put(identifier, jsonUnbakedModel);

            ci.cancel();
        }
    }

    //---------------------------------------------------------

    // cope
    // @Unique private static final Logger LOGGER_MALD = LogManager.getLogger("Jello");

    @Unique private static final Set<DyeableBlockVariant> ALL_BLOCK_VARIANTS = new HashSet<>();

    @Unique private final Map<String, UnbakedModel> test_map = new HashMap<>();

    @Inject(method = "addModel", at = @At("HEAD"), cancellable = true)
    private void checkIfModelIsRedirectable(ModelIdentifier modelId, CallbackInfo ci) {
        if (DyeColorantRegistry.shouldRedirectModelResource(new Identifier(modelId.getNamespace(), modelId.getPath()))) {
            String[] stringParts = modelId.getPath().split("_");

            if (ALL_BLOCK_VARIANTS.isEmpty()) {
                ALL_BLOCK_VARIANTS.addAll(DyeableBlockVariant.getAllBlockVariants());
            }

            boolean isItemVersion = Objects.equals(modelId.getVariant(), "inventory");

            this.cachedBlockStateRedirectID = null;
            this.cachedItemRedirectID = null;

            if(Objects.equals(modelId.getPath(), "slime_slab")){
                return;
            }

            for (DyeableBlockVariant blockVariant : ALL_BLOCK_VARIANTS) {
                if (blockVariant.isSuchAVariant(modelId, false)) {
                    String nameSpace = Objects.equals(blockVariant.variantIdentifier.getNamespace(), "minecraft") ? Jello.MODID : blockVariant.variantIdentifier.getNamespace();

                    Identifier identifier = new Identifier(nameSpace, "colored_" + blockVariant.variantIdentifier.getPath());

                    if (!isItemVersion) {
                        this.cachedBlockStateRedirectID = identifier;

                        if (test_map.get(cachedBlockStateRedirectID + modelId.getVariant()) != null) {
                            this.unbakedModels.put(modelId, test_map.get(this.cachedBlockStateRedirectID.toString() + modelId.getVariant()));
                            this.modelsToBake.put(modelId, test_map.get(this.cachedBlockStateRedirectID.toString() + modelId.getVariant()));

                            ci.cancel();
                        }

                        this.cachedItemRedirectID = null;
                    } else {
                        this.cachedItemRedirectID = identifier;

                        if (test_map.get(this.cachedItemRedirectID + "/item") != null) {
                            this.unbakedModels.put(modelId, test_map.get(this.cachedItemRedirectID + "/item"));
                            this.modelsToBake.put(modelId, test_map.get(this.cachedItemRedirectID + "/item"));

                            ci.cancel();
                        }

                        this.cachedBlockStateRedirectID = null;
                    }

                    return;
                }
            }

            if (isItemVersion) {
                if (Objects.equals(stringParts[stringParts.length - 1], "dye")) {
                    this.cachedItemRedirectID = Jello.id("dynamic_dye");

                    if (test_map.get(cachedItemRedirectID + "/item") != null) {
                        this.unbakedModels.put(modelId, test_map.get(cachedItemRedirectID + "/item"));
                        this.modelsToBake.put(modelId, test_map.get(cachedItemRedirectID + "/item"));

                        ci.cancel();
                    }

                    this.cachedBlockStateRedirectID = null;
                }
            }

            // mald about it
//            LOGGER_MALD.error(Arrays.toString(stringParts) + " / " + isItemVersion + " / " + cachedBlockStateRedirectID);
        } else {
            cachedBlockStateRedirectID = null;
            cachedItemRedirectID = null;
        }
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @Inject(method = "addModel", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/render/model/ModelLoader;getOrLoadModel(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/model/UnbakedModel;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void cacheBlockModel(ModelIdentifier modelId, CallbackInfo ci, UnbakedModel unbakedModel) {
        if (cachedBlockStateRedirectID != null) {
            test_map.putIfAbsent(cachedBlockStateRedirectID + modelId.getVariant(), unbakedModel);
        } else if (cachedItemRedirectID != null) {
            test_map.putIfAbsent(cachedItemRedirectID + "/item", unbakedModel);
        }
    }

}
