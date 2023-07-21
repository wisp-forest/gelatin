package io.wispforest.gelatin.dye_entries.mixins.client.model;

import io.wispforest.gelatin.common.misc.GelatinConstants;
import io.wispforest.gelatin.dye_entries.utils.DyeVariantBuilder;
import io.wispforest.gelatin.dye_entries.variants.DyeableVariantRegistry;
import io.wispforest.gelatin.dye_entries.variants.block.DyeableBlockVariant;
import net.fabricmc.fabric.api.client.model.loading.v1.DelegatingUnbakedModel;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.*;

@Mixin(ModelLoader.class)
public abstract class NewModelLoaderMixin {

    @Shadow @Final private Map<Identifier, UnbakedModel> unbakedModels;
    @Shadow @Final private Map<Identifier, UnbakedModel> modelsToBake;

    //---------------------------------------------------------

    @Unique private Identifier cachedBlockStateRedirectID;

    @ModifyVariable(method = "loadModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/json/ModelVariantMap$DeserializationContext;setStateFactory(Lnet/minecraft/state/StateManager;)V", shift = At.Shift.BY, by = 2), argsOnly = true)
    private Identifier changeIdentifierForBlocks(Identifier value) {
        return cachedBlockStateRedirectID != null
                ? cachedBlockStateRedirectID
                : value;
    }

    //---------------------------------------------------------

    // cope
    // @Unique private static final Logger LOGGER_MALD = LogManager.getLogger("Jello");

    @Unique private static final Set<DyeableBlockVariant> ALL_BLOCK_VARIANTS = new HashSet<>();

    @Unique private final Map<String, Pair<Identifier, UnbakedModel>> CACHED_BLOCKSTATE_MODEL_INFO = new HashMap<>();

    @Inject(method = "addModel", at = @At("HEAD"), cancellable = true)
    private void checkIfModelIsRedirectable(ModelIdentifier modelId, CallbackInfo ci) {
        if (!DyeVariantBuilder.shouldRedirectModelResource(new Identifier(modelId.getNamespace(), modelId.getPath()))) {
            cachedBlockStateRedirectID = null;

            return;
        }

        if (ALL_BLOCK_VARIANTS.isEmpty()) ALL_BLOCK_VARIANTS.addAll(DyeableVariantRegistry.getAllBlockVariants());

        boolean isItemVersion = Objects.equals(modelId.getVariant(), "inventory");

        this.cachedBlockStateRedirectID = null;

        if(Objects.equals(modelId.getPath(), "slime_slab") || isItemVersion) return;

        for (DyeableBlockVariant blockVariant : ALL_BLOCK_VARIANTS) {
            if (!blockVariant.isSuchAVariant(modelId, false)) continue;

            String nameSpace = Objects.equals(blockVariant.variantIdentifier.getNamespace(), "minecraft") ? GelatinConstants.MODID : blockVariant.variantIdentifier.getNamespace();

            this.cachedBlockStateRedirectID = new Identifier(nameSpace, "colored_" + blockVariant.variantIdentifier.getPath());

            String key = cachedBlockStateRedirectID + modelId.getVariant();

            var modelInfo = CACHED_BLOCKSTATE_MODEL_INFO.get(key);

            if (modelInfo != null) {
                UnbakedModel model = new DelegatingUnbakedModel(modelInfo.getLeft()); //modelInfo.getRight();

                this.unbakedModels.put(modelId, model);
                this.modelsToBake.put(modelId, model);

                ci.cancel();
            }

            //Fall back to vanilla to get the needed model data
            break;
        }
    }

    @SuppressWarnings("InvalidInjectorMethodSignature")
    @Inject(method = "addModel", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/render/model/ModelLoader;getOrLoadModel(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/model/UnbakedModel;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void cacheBlockModel(ModelIdentifier modelId, CallbackInfo ci, UnbakedModel unbakedModel) {
        if (cachedBlockStateRedirectID == null) return;

        CACHED_BLOCKSTATE_MODEL_INFO.putIfAbsent(cachedBlockStateRedirectID + modelId.getVariant(), new Pair<>(modelId, unbakedModel));
    }

}
