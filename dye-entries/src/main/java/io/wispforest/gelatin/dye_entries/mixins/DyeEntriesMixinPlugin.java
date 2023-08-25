package io.wispforest.gelatin.dye_entries.mixins;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class DyeEntriesMixinPlugin implements IMixinConfigPlugin {

    @Override public void onLoad(String mixinPackage) {}
    @Override public String getRefMapperConfig() { return null;}

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        String[] pathParts = mixinClassName.split("\\.");

        String onlyMixinName = pathParts[pathParts.length - 1];

        String dependentModID = switch (onlyMixinName) {
            case "SheepShearedFeatureRendererMixin" -> "sheepconsistency";
            case "MultipartAppenderMixin" -> "diagonalwindows";
            default -> null;
        };

        return dependentModID == null || FabricLoader.getInstance().isModLoaded(dependentModID);
    }

    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}
    @Override public List<String> getMixins() { return null; }
    @Override public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
    @Override public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
