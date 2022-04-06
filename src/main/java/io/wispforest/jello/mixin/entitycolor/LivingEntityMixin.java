package io.wispforest.jello.mixin.entitycolor;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.jello.api.ducks.entity.DyeableEntity;
import io.wispforest.jello.api.ducks.entity.RainbowEntity;
import io.wispforest.jello.api.util.DataConstants;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements DyeableEntity, RainbowEntity {

    private static final TrackedData<Identifier> DYE_COLOR = DataConstants.DYE_COLOR;
    private static final TrackedData<Byte> RAINBOW_MODE = DataConstants.RAINBOW_MODE;
    private static final TrackedData<Integer> CONSTANT_COLOR = DataConstants.CONSTANT_COLOR;

    @Inject(method = "initDataTracker", at = @At(value = "TAIL"))
    private void initDyeColorTracker(CallbackInfo ci) {
        ((LivingEntity) (Object) this).getDataTracker().startTracking(DYE_COLOR, DyeColorantRegistry.NULL_VALUE_NEW.getId());
        ((LivingEntity) (Object) this).getDataTracker().startTracking(RAINBOW_MODE, (byte) 0);
        ((LivingEntity) (Object) this).getDataTracker().startTracking(CONSTANT_COLOR, DataConstants.DEFAULT_NULL_COLOR_VALUE);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At(value = "TAIL"))
    public void writeDyeColorNBT(NbtCompound nbt, CallbackInfo ci) {
        nbt.putString(DataConstants.getDyeColorNbtKey(), ((LivingEntity) (Object) this).getDataTracker().get(DYE_COLOR).toString());
        nbt.putByte(DataConstants.getRainbowNbtKey(), ((LivingEntity) (Object) this).getDataTracker().get(RAINBOW_MODE));
        nbt.putString(DataConstants.getConstantColorNbtKey(), ((LivingEntity) (Object) this).getDataTracker().get(CONSTANT_COLOR).toString());
    }

    @Inject(method = "readCustomDataFromNbt", at = @At(value = "TAIL"))
    public void readDyeColorNBT(NbtCompound nbt, CallbackInfo ci) {
        String possibleIdentifier = nbt.getString(DataConstants.getDyeColorNbtKey());

        ((LivingEntity) (Object) this).getDataTracker().set(DYE_COLOR, !Objects.equals(possibleIdentifier, "") ? Identifier.tryParse(possibleIdentifier) : DyeColorantRegistry.NULL_VALUE_NEW.getId());
        ((LivingEntity) (Object) this).getDataTracker().set(RAINBOW_MODE, nbt.getByte(DataConstants.getRainbowNbtKey()));
        ((LivingEntity) (Object) this).getDataTracker().set(CONSTANT_COLOR, getOrDefaultNbtColor(DataConstants.getConstantColorNbtKey(), nbt, DataConstants.DEFAULT_NULL_COLOR_VALUE));
    }

    private int getOrDefaultNbtInt(String key, NbtCompound nbt, int defaultValue) {
        return nbt.contains(key) ? nbt.getInt(key) : defaultValue;
    }

    private Integer getOrDefaultNbtColor(String key, NbtCompound nbt, int defaultValue) {
        if (nbt.contains(key)) {
            String string = nbt.getString(key);
            Integer colorValue = null;

            if (string.startsWith("#")) {
                String hexValue = string.replace('#', ' ').trim();

                try {
                    colorValue = Integer.parseInt(hexValue, 16);
                } catch (NumberFormatException ignore) {
                }

                if (colorValue != null) {
                    return colorValue;
                }
            } else {
                try {
                    colorValue = Integer.parseInt(string, 10);
                } catch (NumberFormatException ignore) {
                }

                if (colorValue != null) {
                    return colorValue;
                }
            }
        }

        return defaultValue;

    }

    //---------------------------------------------------------------------------------------------------//
    @Override
    public Identifier getDyeColorID() {
        return ((LivingEntity) (Object) this).getDataTracker().get(DYE_COLOR);
    }

    @Override
    public void setDyeColor(DyeColorant dyeColor) {
        ((LivingEntity) (Object) this).getDataTracker().set(DYE_COLOR, dyeColor.getId());
    }

    @Override
    public boolean dyeColorOverride() {
        return false;
    }

    //---------------------------------------------------------------------------------------------------//
    @Override
    public void setRainbowTime(boolean value) {
        ((LivingEntity) (Object) this).getDataTracker().set(RAINBOW_MODE, value ? (byte) 1 : 0);
    }

    @Override
    public boolean isRainbowTime() {
        return rainbowOverride() || ((LivingEntity) (Object) this).getDataTracker().get(RAINBOW_MODE) == 1;
    }

    @Override
    public boolean rainbowOverride() {
        return false;
    }

    //---------------------------------------------------------------------------------------------------//

    @Override
    public int getConstantColor() {
        return ((LivingEntity) (Object) this).getDataTracker().get(CONSTANT_COLOR);
    }

    //---------------------------------------------------------------------------------------------------//
}
