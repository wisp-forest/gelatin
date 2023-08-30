package io.wispforest.gelatin.dye_entries.mixins.client.diagonal_windows;

import fuzs.diagonalwindows.client.model.MultipartAppender;
import io.wispforest.gelatin.dye_entries.block.ColoredGlassPaneBlock;
import net.fabricmc.fabric.api.client.model.loading.v1.DelegatingUnbakedModel;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Pseudo
@Mixin(value = MultipartAppender.class)
public class MultipartAppenderMixin {

    @Unique private static boolean gelatin$idReplacedForModelModification = false;
    @Unique private static boolean gelatin$cancelModelModification = false;

    @Unique private static ModelLoader gelatin$cachedModelLoader;
    @Unique private static BlockState getaltin$cachedState;

    @Inject(method = "lambda$onPrepareModelBaking$2", at = @At(value = "HEAD"))
    private static void gelatin$gatherMethodParameters(ModelLoader modelLoader, BlockState state, CallbackInfo ci){
        gelatin$cachedModelLoader = modelLoader;
        getaltin$cachedState = state;
    }

    @ModifyArg(method = "lambda$onPrepareModelBaking$2", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/ModelLoader;getOrLoadModel(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/model/UnbakedModel;"))
    private static Identifier gelatin$attemptRedirectID(Identifier id){
        if(getaltin$cachedState.getBlock() instanceof ColoredGlassPaneBlock) {
            UnbakedModel model = gelatin$cachedModelLoader.getOrLoadModel(id);

            if (model instanceof DelegatingUnbakedModel delegatingUnbakedModel) {
                return ((List<Identifier>) delegatingUnbakedModel.getModelDependencies()).get(0);
            }

            if(!gelatin$cancelModelModification) gelatin$idReplacedForModelModification = true;
        }

        return id;
    }

    @Inject(method = "lambda$onPrepareModelBaking$2", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/ModelLoader;getOrLoadModel(Lnet/minecraft/util/Identifier;)Lnet/minecraft/client/render/model/UnbakedModel;", shift = At.Shift.BY, by = 2), cancellable = true)
    private static void gelatin$cancelFutherModifcations(ModelLoader modelBakery, BlockState state, CallbackInfo ci){
        if(gelatin$idReplacedForModelModification){
            gelatin$idReplacedForModelModification = false;

            gelatin$cancelModelModification = true;

            return;
        }

        if(gelatin$cancelModelModification) ci.cancel();
    }
}
