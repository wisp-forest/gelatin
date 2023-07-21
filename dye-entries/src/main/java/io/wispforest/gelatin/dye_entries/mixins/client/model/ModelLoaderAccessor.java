package io.wispforest.gelatin.dye_entries.mixins.client.model;

import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.io.IOException;

@Mixin(ModelLoader.class)
public interface ModelLoaderAccessor {
    @Invoker("loadModelFromJson") JsonUnbakedModel gelatin$LoadModelFromJson(Identifier id) throws IOException;
}
