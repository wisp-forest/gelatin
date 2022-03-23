package io.wispforest.jello.api.mixin.mixins.client.dye;

import io.wispforest.jello.api.dye.block.ColoredCandleBlock;
import io.wispforest.jello.api.dye.block.ColoredGlassPaneBlock;
import io.wispforest.jello.api.dye.client.BlockModelRedirect;
import io.wispforest.jello.main.common.Jello;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(ModelLoader.class)
public class ModelLoaderMixin {

    @Unique public Block cachedBlock = Blocks.AIR;

    @Inject(method = "loadModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/json/ModelVariantMap$DeserializationContext;setStateFactory(Lnet/minecraft/state/StateManager;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void getBlock(Identifier id, CallbackInfo ci, ModelIdentifier modelIdentifier, Identifier identifier, StateManager<Block, BlockState> stateManager){
        this.cachedBlock = stateManager.getOwner();
    }

    @ModifyVariable(method = "loadModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/json/ModelVariantMap$DeserializationContext;setStateFactory(Lnet/minecraft/state/StateManager;)V", shift = At.Shift.BY, by = 2), argsOnly = true)
    private Identifier changeIdentifierForColorGlass(Identifier value){
        for(Map.Entry<BlockModelRedirect.ResourceRedirectEntryPredicate, Identifier> entry : BlockModelRedirect.ResourceRedirectEntryPredicate.BLOCKSTATE_PREDICATES.entrySet()){
            if(entry.getKey().test(this.cachedBlock)){
                return entry.getValue();
            }
        }

        return value;
    }
}
