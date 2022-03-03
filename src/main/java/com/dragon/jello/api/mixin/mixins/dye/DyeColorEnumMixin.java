package com.dragon.jello.api.mixin.mixins.dye;

import com.dragon.jello.main.common.Jello;
import com.dragon.jello.api.dye.registry.DyeColorRegistry;
import com.dragon.jello.api.dye.DyeColorant;
import net.minecraft.block.MapColor;
import net.minecraft.util.Identifier;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(net.minecraft.util.DyeColor.class)
public class DyeColorEnumMixin {

    @Invoker("<init>")
    public static net.minecraft.util.DyeColor dyeColorRegistry$invokeNew(String internalName, int ordinal, int id, String name, int color, MapColor mapColor, int fireworkColor, int signColor) {
        throw new IllegalStateException("How did this mixin stub get called conc");
    }

    @Final @Shadow @Mutable private static net.minecraft.util.DyeColor[] field_7953;

    @Inject(method = "<clinit>", at = @At(value = "FIELD", target = "Lnet/minecraft/util/DyeColor;field_7953:[Lnet/minecraft/util/DyeColor;", shift = At.Shift.AFTER, opcode = Opcodes.PUTSTATIC))
    private static void addNullDyeColorValue(CallbackInfo ci) {
        var dyeColor = new net.minecraft.util.DyeColor[field_7953.length + 1];
        System.arraycopy(field_7953, 0, dyeColor, 0, field_7953.length);

        dyeColor[dyeColor.length - 1] = DyeColorEnumMixin.dyeColorRegistry$invokeNew("_null", net.minecraft.util.DyeColor.values().length, 16, "_null", 0, MapColor.CLEAR, 0, 0);
        DyeColorRegistry.NULL_VALUE_OLD = dyeColor[dyeColor.length - 1];

        field_7953 = dyeColor;
    }

    //-------------------------------------------------------------------------

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void importCreatedEnumDyeColor(String internalName, int ordinal, int id, String name, int color, MapColor mapColor, int fireworkColor, int signColor, CallbackInfo ci){
        if(!(Objects.equals(name, "_null") || DyeColorRegistry.VANILLA_DYES.stream().anyMatch(dyeColor -> Objects.equals(name, dyeColor.getName())))){
            DyeColorant convertedDyeColor = DyeColorRegistry.registryDyeColor(new Identifier(Jello.MODID, name), name, mapColor, color, fireworkColor, signColor);
        }
    }
}
