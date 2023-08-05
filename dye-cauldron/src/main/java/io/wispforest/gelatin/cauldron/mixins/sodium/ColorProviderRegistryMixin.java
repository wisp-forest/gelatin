package io.wispforest.gelatin.cauldron.mixins.sodium;

import io.wispforest.gelatin.cauldron.compat.ColoredCauldronWaterColorProvider;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceMap;
import me.jellysquid.mods.sodium.client.model.color.ColorProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "me/jellysquid/mods/sodium/client/model/color/ColorProviderRegistry", remap = false)
@Pseudo
public abstract class ColorProviderRegistryMixin {

    @Shadow protected abstract void registerBlocks(ColorProvider<BlockState> resolver, Block... blocks);

    @Shadow @Final private Reference2ReferenceMap<Block, ColorProvider<BlockState>> blocks;

    @Inject(method = "installOverrides", at = @At("TAIL"))
    private void gelatin$wrapWaterCauldron(CallbackInfo ci){
        ColorProvider<BlockState> provider = this.blocks.get(Blocks.WATER_CAULDRON);

        this.registerBlocks(new ColoredCauldronWaterColorProvider<>(provider), Blocks.WATER_CAULDRON);
    }
}
