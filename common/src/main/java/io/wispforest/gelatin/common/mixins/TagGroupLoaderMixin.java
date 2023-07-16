package io.wispforest.gelatin.common.mixins;

import io.wispforest.gelatin.common.util.TagInjector;
import net.minecraft.resource.ResourceManager;
import net.minecraft.tag.TagGroupLoader;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Copy owo mixin: <a href="https://github.com/wisp-forest/owo-lib/blob/8560ec4b32d8752785d919ce8db1cc0c057c1b8a/src/main/java/io/wispforest/owo/mixin/TagGroupLoaderMixin.java#L19C1-L19C1">Link</a>
 */
@Mixin(TagGroupLoader.class)
public class TagGroupLoaderMixin {

    @Shadow
    @Final
    private String dataType;

    @Inject(method = "loadTags", at = @At("TAIL"))
    public void injectValues(ResourceManager manager, CallbackInfoReturnable<Map<Identifier, List<TagGroupLoader.TrackedEntry>>> cir) {
        var map = cir.getReturnValue();

        TagInjector.ADDITIONS.forEach((location, entries) -> {
            if (!this.dataType.equals(location.type())) return;

            var list = map.computeIfAbsent(location.tagId(), id -> new ArrayList<>());
            entries.forEach(addition -> list.add(new TagGroupLoader.TrackedEntry(addition, "owo")));
        });
    }

}
