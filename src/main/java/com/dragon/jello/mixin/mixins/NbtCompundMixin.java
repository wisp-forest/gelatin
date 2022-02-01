package com.dragon.jello.mixin.mixins;

import com.dragon.jello.Util.DataConstants;
import com.dragon.jello.mixin.ducks.DyeableEntity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Debug(export = true)
@Mixin(NbtCompound.class)
public class NbtCompundMixin {

//    @Inject(method = "getInt", at = @At(value = "RETURN", ordinal = 1),cancellable = true)
//    private void changeReturnValue(String key, CallbackInfoReturnable<Integer> cir){
//        if(key.equals(DataConstants.getDyeColorNbtKey())){
//            cir.setReturnValue(16);
//        }else if(key.equals(DataConstants.getDyeColorNbtKey())){
//
//        }
//    }
}
