package io.wispforest.jello.mixin.plugins;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class JelloMixinPlugin implements IMixinConfigPlugin {

    @Override
    public void onLoad(String mixinPackage) {}

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        String mixinClassNameOnly = getMixinClassName(mixinClassName);

        if(Objects.equals(mixinClassNameOnly, "SheepShearedFeatureRendererMixin")){
            return FabricLoader.getInstance().isModLoaded("sheepconsistency");
        }

        if(Objects.equals(mixinClassNameOnly, "LinearColorBlenderMixin")){
            return FabricLoader.getInstance().isModLoaded("sodium");
        }

        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    private static String getMixinClassName(String mixinClassTarget){
        String[] stringPathParts = mixinClassTarget.split("\\.");

        return stringPathParts[stringPathParts.length - 1];
    }

}
