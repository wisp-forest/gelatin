package io.wispforest.jello.mixins.client.accessors;

import com.google.gson.JsonElement;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

@Mixin(ItemModelGenerator.class)
public interface ItemModelGeneratorAccessor {
    @Accessor("writer") BiConsumer<Identifier, Supplier<JsonElement>> jello$getWriter();
}
