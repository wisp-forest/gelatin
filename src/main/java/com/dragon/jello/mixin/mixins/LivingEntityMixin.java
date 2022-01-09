package com.dragon.jello.mixin.mixins;

import com.dragon.jello.mixin.ducks.DyeableEntity;
import com.dragon.jello.mixin.ducks.RainbowEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import static com.dragon.jello.Util.DataConstants.*;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements DyeableEntity, RainbowEntity {

    private static final TrackedData<Integer> DYE_COLOR = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Byte> GRAY_SCALE_MODE = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Byte> RAINBOW_MODE = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.BYTE);

    @Inject(method = "initDataTracker", at = @At(value = "TAIL"))
    private void initDyeColorTracker(CallbackInfo ci){
        ((LivingEntity) (Object)this).getDataTracker().startTracking(DYE_COLOR, 16);
        ((LivingEntity) (Object)this).getDataTracker().startTracking(RAINBOW_MODE, (byte)0);
        ((LivingEntity) (Object)this).getDataTracker().startTracking(GRAY_SCALE_MODE, (byte)0);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At(value = "TAIL"))
    public void writeDyeColorNBT(NbtCompound nbt, CallbackInfo ci){
        nbt.putInt(getDyeColorNbtKey(), ((LivingEntity) (Object)this).getDataTracker().get(DYE_COLOR));
        nbt.putByte(getRainbowNbtKey(), ((LivingEntity) (Object)this).getDataTracker().get(RAINBOW_MODE));
        nbt.putByte(getGrayScaleNbtKey(), ((LivingEntity) (Object)this).getDataTracker().get(GRAY_SCALE_MODE));
    }

    @Inject(method = "readCustomDataFromNbt", at = @At(value = "TAIL"))
    public void readDyeColorNBT(NbtCompound nbt, CallbackInfo ci){
        ((LivingEntity) (Object)this).getDataTracker().set(DYE_COLOR, nbt.getInt(getDyeColorNbtKey()));
        ((LivingEntity) (Object)this).getDataTracker().set(RAINBOW_MODE, nbt.getByte(getRainbowNbtKey()));
        ((LivingEntity) (Object)this).getDataTracker().set(GRAY_SCALE_MODE, nbt.getByte(getGrayScaleNbtKey()));
    }

    //---------------------------------------------------------------------------------------------------//
    @Override
    public int getDyeColorID() {
        return ((LivingEntity) (Object)this).getDataTracker().get(DYE_COLOR);
    }

    @Override
    public void setDyeColorID(int dyeColorID){
        ((LivingEntity) (Object)this).getDataTracker().set(DYE_COLOR, dyeColorID);
        if(this.trueColorOverride()){
            setGrayScaleMode(isDyed());
        }
    }

    @Override
    public boolean dyeColorOverride(){
        return false;
    }

    @Override
    public boolean trueColorOverride(){
        return true;
    }

    //---------------------------------------------------------------------------------------------------//
    @Override
    public void setRainbowTime(boolean value){
        ((LivingEntity) (Object)this).getDataTracker().set(RAINBOW_MODE, value ? (byte) 1 : 0);
    }

    @Override
    public boolean isRainbowTime(){
        return rainbowOverride() || ((LivingEntity) (Object) this).getDataTracker().get(RAINBOW_MODE) == 1;
    }

    @Override
    public boolean rainbowOverride(){
        return false;
    }

    //---------------------------------------------------------------------------------------------------//
    @Override
    public void setGrayScaleMode(boolean value){
        if(this.trueColorOverride() && isDyed()){
            ((LivingEntity) (Object)this).getDataTracker().set(GRAY_SCALE_MODE, (byte) 1);
        }else{
            ((LivingEntity) (Object)this).getDataTracker().set(GRAY_SCALE_MODE, value ? (byte) 1 : 0);
        }
    }

    @Override
    public boolean isGrayScaled() {
        return grayScaleOverride() || ((LivingEntity) (Object) this).getDataTracker().get(GRAY_SCALE_MODE) == 1;
    }

    @Override
    public boolean grayScaleOverride(){
        return false;
    }

    //---------------------------------------------------------------------------------------------------//
}
