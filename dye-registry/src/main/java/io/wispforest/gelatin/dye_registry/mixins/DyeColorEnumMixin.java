package io.wispforest.gelatin.dye_registry.mixins;

import io.wispforest.gelatin.dye_registry.DyeColorant;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import net.minecraft.block.MapColor;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
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

    @Final
    @Shadow
    @Mutable
    private static net.minecraft.util.DyeColor[] field_7953;

    @Inject(method = "<clinit>", at = @At(value = "FIELD", target = "Lnet/minecraft/util/DyeColor;field_7953:[Lnet/minecraft/util/DyeColor;", shift = At.Shift.AFTER, opcode = Opcodes.PUTSTATIC))
    private static void addNullDyeColorValue(CallbackInfo ci) {
        var dyeColor = new net.minecraft.util.DyeColor[field_7953.length + 1];
        System.arraycopy(field_7953, 0, dyeColor, 0, field_7953.length);

        dyeColor[dyeColor.length - 1] = dyeColorRegistry$invokeNew("_null", net.minecraft.util.DyeColor.values().length, 0, "_null", 0, MapColor.CLEAR, 0, 0);
        DyeColorantRegistry.Constants.NULL_VALUE_OLD = dyeColor[dyeColor.length - 1];

        field_7953 = dyeColor;
    }

    //-------------------------------------------------------------------------

    @Inject(method = "<init>(Ljava/lang/String;IILjava/lang/String;ILnet/minecraft/block/MapColor;II)V", at = @At(value = "TAIL"))
    private void importCreatedEnumDyeColor(String internalName, int ordinal, int id, String name, int color, MapColor mapColor, int fireworkColor, int signColor, CallbackInfo ci) {
        if (!(Objects.equals(name, "_null") || DyeColorantRegistry.Constants.VANILLA_DYES.stream().anyMatch(dyeColor -> Objects.equals(name, dyeColor.getName())))) {
            DyeColorant convertedDyeColor = DyeColorantRegistry.registerDyeColor(new Identifier(DyeColorantRegistry.Constants.ENUM_NAMESPACE, name), mapColor, color, fireworkColor, signColor);
        }
    }

    /**
     * @author Dragon_Slayer
     * @reason Because I have too as to prevent people from getting Jellos null Value for this enum
     */
    @Overwrite
    public static DyeColor[] values() {
        return new DyeColor[]{
                DyeColor.WHITE,
                DyeColor.ORANGE,
                DyeColor.MAGENTA,
                DyeColor.LIGHT_BLUE,
                DyeColor.YELLOW,
                DyeColor.LIME,
                DyeColor.PINK,
                DyeColor.GRAY,
                DyeColor.LIGHT_GRAY,
                DyeColor.CYAN,
                DyeColor.PURPLE,
                DyeColor.BLUE,
                DyeColor.BROWN,
                DyeColor.GREEN,
                DyeColor.RED,
                DyeColor.BLACK,
        };
    }

}
