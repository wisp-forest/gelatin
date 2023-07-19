package io.wispforest.gelatin.dye_entities.mixins;

import io.wispforest.gelatin.common.util.ColorUtil;
import io.wispforest.gelatin.dye_entities.client.utils.GrayScaleEntityRegistry;
import io.wispforest.gelatin.dye_entities.ducks.Colorable;
import io.wispforest.gelatin.dye_entities.ducks.Colored;
import io.wispforest.gelatin.dye_entities.misc.DataConstants;
import io.wispforest.gelatin.dye_registry.DyeColorant;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements Colorable {

    @Unique private static final TrackedData<Byte> RAINBOW_MODE = DataConstants.RAINBOW_MODE;
    @Unique private static final TrackedData<Integer> COLOR_VALUE = DataConstants.COLOR_VALUE;

    @Inject(method = "initDataTracker", at = @At(value = "TAIL"))
    private void initDyeColorTracker(CallbackInfo ci) {
        ((LivingEntity) (Object) this).getDataTracker().startTracking(RAINBOW_MODE, (byte) 0);
        ((LivingEntity) (Object) this).getDataTracker().startTracking(COLOR_VALUE, -1);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At(value = "TAIL"))
    public void writeDyeColorNBT(NbtCompound nbt, CallbackInfo ci) {
        nbt.putByte(DataConstants.getRainbowNbtKey(), ((LivingEntity) (Object) this).getDataTracker().get(RAINBOW_MODE));
        nbt.putInt(DataConstants.getColoredNbtkey(), ((LivingEntity) (Object) this).getDataTracker().get(COLOR_VALUE));
    }

    @Inject(method = "readCustomDataFromNbt", at = @At(value = "TAIL"))
    public void readDyeColorNBT(NbtCompound nbt, CallbackInfo ci) {
        int colorValue = nbt.contains(DataConstants.getColoredNbtkey())
                ? nbt.getInt(DataConstants.getColoredNbtkey())
                : -1;

        //----

        if(nbt.contains(DataConstants.getDyeColorNbtKey())) {
            String possibleIdentifier = nbt.getString(DataConstants.getDyeColorNbtKey());

            DyeColorant dyeColorant = Objects.equals(possibleIdentifier, "jello:_null") || Objects.equals(possibleIdentifier, "")
                    ? DyeColorantRegistry.NULL_VALUE_NEW
                    : DyeColorantRegistry.DYE_COLOR.get(Identifier.tryParse(possibleIdentifier));

            nbt.remove(DataConstants.getDyeColorNbtKey());

            int oldColorValue = dyeColorant.getBaseColor();

            if(oldColorValue == -1) colorValue = oldColorValue;
        }

        if(nbt.contains(DataConstants.getConstantColorNbtKey())){
            int oldColorValue = getOrDefaultNbtColor(DataConstants.getConstantColorNbtKey(), nbt, -1);

            nbt.remove(DataConstants.getConstantColorNbtKey());

            if(oldColorValue != 0) colorValue = oldColorValue;
        }

        //----

        ((LivingEntity) (Object) this).getDataTracker().set(RAINBOW_MODE, nbt.getByte(DataConstants.getRainbowNbtKey()));
        ((LivingEntity) (Object) this).getDataTracker().set(COLOR_VALUE, colorValue);
    }

    @Unique
    private int getOrDefaultNbtInt(String key, NbtCompound nbt, int defaultValue) {
        return nbt.contains(key) ? nbt.getInt(key) : defaultValue;
    }

    @Unique
    private Integer getOrDefaultNbtColor(String key, NbtCompound nbt, int defaultValue) {
        if (nbt.contains(key)) {
            String string = nbt.getString(key);
            int radix = 10;

            if (string.startsWith("#")) {
                string = string.replace('#', ' ').trim();
                radix = 16;
            }

            try {
                return Integer.parseInt(string, radix);
            } catch (NumberFormatException ignore) {}
        }

        return defaultValue;
    }

    //------------

    @Override
    public boolean setColor(int color) {
        int currentColor = this.getColor(0);

        if(currentColor == color) return false;

        ((LivingEntity) (Object) this).getDataTracker().set(COLOR_VALUE, color);

        return true;
    }

    @Override
    public boolean isColored() {
        return !isRainbow() && getColor(0) != -1;
    }

    @Override
    public boolean setRainbow(boolean rainbow) {
        boolean isRainbow = this.isRainbow();

        if(isRainbow == rainbow) return false;

        ((LivingEntity) (Object) this).getDataTracker().set(RAINBOW_MODE, rainbow ? (byte) 1 : 0);

        return true;
    }

    @Override
    public boolean isRainbow() {
        return ((LivingEntity) (Object) this).getDataTracker().get(RAINBOW_MODE) == 1;
    }

    @Override
    public int getColor(float delta) {
        if(isRainbow()){
            return ColorUtil.rainbowColorizer(((LivingEntity) (Object) this), delta);
        }

        return ((LivingEntity) (Object) this).getDataTracker().get(COLOR_VALUE);

        //return -1;
    }

    @Override
    public boolean isGrayScaled(Entity entity, RenderType renderType) {
        if(renderType == RenderType.FEATURE_RENDER && (LivingEntity) (Object) this instanceof SheepEntity) return false;

        return !isRainbow() && isColored() && !GrayScaleEntityRegistry.isBlacklisted(entity);
    }
}
