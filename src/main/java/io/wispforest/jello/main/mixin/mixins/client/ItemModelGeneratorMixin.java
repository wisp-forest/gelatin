package io.wispforest.jello.main.mixin.mixins.client;

import net.minecraft.client.render.model.json.ItemModelGenerator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ItemModelGenerator.class)
public class ItemModelGeneratorMixin {

    @Shadow @Final public static List<String> LAYERS;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void addMoreLayers(CallbackInfo ci){
        LAYERS.add("layer5");
        LAYERS.add("layer6");
        LAYERS.add("layer7");
        LAYERS.add("layer8");
        LAYERS.add("layer9");
        LAYERS.add("layer10");
        LAYERS.add("layer11");
        LAYERS.add("layer12");
        LAYERS.add("layer13");
        LAYERS.add("layer14");
        LAYERS.add("layer15");
    }
}
