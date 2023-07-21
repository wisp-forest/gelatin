package io.wispforest.gelatin.dye_entries.mixins;

import io.wispforest.gelatin.dye_entries.mixins.client.dye.ModelLoaderMixin;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class DyeEntriesMixinPlugin implements IMixinConfigPlugin {

    @Override public void onLoad(String mixinPackage) {}
    @Override public String getRefMapperConfig() { return null;}

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        Optional<ModContainer> newModelLoadingAPI = FabricLoader.getInstance().getModContainer("fabric-model-loading-api-v1");

        String[] pathParts = mixinClassName.split("\\.");

        String onlyMixinName = pathParts[pathParts.length - 1];

        boolean bl = (newModelLoadingAPI.isPresent() && onlyMixinName.equals("ModelLoaderMixin"))
                || (newModelLoadingAPI.isEmpty() && onlyMixinName.equals("NewModelLoaderMixin"))
                || (!FabricLoader.getInstance().isModLoaded("sheepconsistency") && onlyMixinName.equals("onlyMixinName"));

        return !bl;
    }

    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}
    @Override public List<String> getMixins() { return null; }
    @Override public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
    @Override public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
