package io.wispforest.gelatin.common.mixins.client.modmenu;

import com.terraformersmc.modmenu.util.mod.fabric.FabricMod;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Pseudo
@Mixin(value = FabricMod.class, remap = false)
public abstract class FabricModMixin {

    @Shadow @NotNull public abstract ModContainer getContainer();

    @ModifyVariable(method = "getIcon", at = @At(value = "INVOKE", target = "Lcom/terraformersmc/modmenu/util/mod/fabric/FabricMod;getId()Ljava/lang/String;", ordinal = 0, shift = At.Shift.BY, by = 2))
    private String adjustIconId(String iconSourceId){
        CustomValue value = this.getContainer().getMetadata().getCustomValue("gelatin");

        if(value != null) {
            CustomValue idValue = value.getAsObject().get("icon-redirect");

            iconSourceId = new Identifier(idValue.getAsString()).getNamespace();
        }

        return iconSourceId;
    }

    @ModifyVariable(method = "getIcon", at = @At(value = "INVOKE", target = "Lcom/terraformersmc/modmenu/util/mod/fabric/FabricMod;getId()Ljava/lang/String;", shift = At.Shift.BY, by = 2), ordinal = 1)
    private String adjustIconPath(String iconPath){
        CustomValue value = this.getContainer().getMetadata().getCustomValue("gelatin");

        if(value != null) {
            CustomValue idValue = value.getAsObject().get("icon-redirect");

            iconPath = new Identifier(idValue.getAsString()).getPath();
        }

        return iconPath;
    }

}
