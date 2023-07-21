package io.wispforest.gelatin.dye_entries.mixins.client.model;

import io.wispforest.gelatin.dye_entries.ducks.ResourceFinderExtension;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.HashMap;
import java.util.Map;

@Mixin(ResourceFinder.class)
public class ResourceFinderMixin implements ResourceFinderExtension {

    @Unique
    private Map<Identifier, Identifier> gelatin$tempPathRedirect = new HashMap<>();

    @Override
    public void putTempRedirect(Identifier key, Identifier value) {
        this.gelatin$tempPathRedirect.put(key, value);
    }

    @ModifyVariable(method = "toResourcePath", at = @At("HEAD"), argsOnly = true)
    private Identifier gelatin$checkToRedirect(Identifier id){
        return gelatin$tempPathRedirect.containsKey(id)
                ? gelatin$tempPathRedirect.remove(id)
                : id;
    }
}
