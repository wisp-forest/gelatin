package io.wispforest.jello.mixins;

import io.wispforest.jello.misc.itemgroup.JelloItemGroupModifier;
import io.wispforest.jello.misc.itemgroup.JelloItemSettings;
import net.minecraft.item.Item;
import net.minecraft.util.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(Item.class)
public abstract class ItemMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private void jello$jelloItemGroup(Item.Settings settings, CallbackInfo ci){
        if(settings instanceof JelloItemSettings jelloItemSettings){
            JelloItemGroupModifier.INSTANCE.ALL_MODIFIERS
                    .computeIfAbsent(jelloItemSettings.group, group -> new ArrayList<>())
                    .add(new Pair<>((Item) (Object) this, jelloItemSettings.visibility));
        }
    }
}
