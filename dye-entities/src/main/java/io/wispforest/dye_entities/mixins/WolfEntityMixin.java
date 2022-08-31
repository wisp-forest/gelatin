package io.wispforest.dye_entities.mixins;

import io.wispforest.common.util.TrackedDataHandlerExtended;
import io.wispforest.dye_registry.ducks.CustomCollarColorStorage;
import io.wispforest.dye_registry.DyeColorant;
import io.wispforest.dye_registry.DyeColorantRegistry;
import io.wispforest.dye_registry.ducks.DyeEntityTool;
import io.wispforest.dye_registry.ducks.DyeItemStorage;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(WolfEntity.class)
public abstract class WolfEntityMixin extends TameableEntity implements CustomCollarColorStorage {

    @Shadow public abstract DyeColor getCollarColor();

    private static final TrackedData<Identifier> GELATIN_COLLAR_COLOR = DataTracker.registerData(WolfEntity.class, TrackedDataHandlerExtended.IDENTIFIER);
    private static final TrackedData<Boolean> GELATIN_RAINBOW_COLLAR = DataTracker.registerData(WolfEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    protected WolfEntityMixin(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initDataTracker", at = @At(value = "TAIL"))
    private void gelatin$initCustomTrackedData(CallbackInfo ci){
        this.dataTracker.startTracking(GELATIN_COLLAR_COLOR, DyeColorantRegistry.NULL_VALUE_NEW.getId());
        this.dataTracker.startTracking(GELATIN_RAINBOW_COLLAR, false);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At(value = "TAIL"))
    private void gelatin$readCustomCollarData(NbtCompound nbt, CallbackInfo ci){
        this.readNbtData(nbt);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At(value = "TAIL"))
    private void gelatin$writeCustomCollarData(NbtCompound nbt, CallbackInfo ci){
        this.writeNbtData(nbt);
    }

    @Inject(method = "interactMob", at = @At(value = "JUMP", opcode = Opcodes.IFEQ, ordinal = 5), cancellable = true)
    private void gelatin$customDyeColor(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir){
        ItemStack stack = player.getStackInHand(hand);

        if(stack.getItem() instanceof DyeEntityTool dyeEntityTool){
            if(player.isSneaky()){
                cir.setReturnValue(super.interactMob(player, hand));

                return;
            }

            ActionResult result = dyeEntityTool.attemptToDyeEntityCollar(this.world, player, hand, this);

            if(result != ActionResult.PASS){
                cir.setReturnValue(result);

                return;
            }
        }

        if(stack.getItem() instanceof DyeItem dyeItem && dyeItem.getColor() == DyeColorantRegistry.Constants.NULL_VALUE_OLD){
            cir.setReturnValue(super.interactMob(player, hand));
        }
    }

    @Inject(method = "interactMob", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/DyeItem;getColor()Lnet/minecraft/util/DyeColor;", shift = At.Shift.BY, by = 2), locals = LocalCapture.CAPTURE_FAILHARD)
    private void gelatin$resetDyeColorant1(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir, ItemStack itemStack, Item item, DyeColor dyeColor){
        if (dyeColor == this.getCollarColor() && (this.getCustomCollarColor() != DyeColorantRegistry.NULL_VALUE_NEW || this.isRainbowCollared())) {
            this.setDefaultValues();

            if(item instanceof DyeEntityTool dyeEntityTool){
                dyeEntityTool.afterInteraction(player, hand, DyeColorantRegistry.NULL_VALUE_NEW);
            }
        }
    }

    @Inject(method = "interactMob", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/WolfEntity;setCollarColor(Lnet/minecraft/util/DyeColor;)V"))
    private void gelatin$resetDyeColorant2(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir){
        this.setDefaultValues();
    }

    //---------------------------------------------

    @Override
    public void setCustomCollarColor(DyeColorant dyeColorant) {
        this.dataTracker.set(GELATIN_COLLAR_COLOR, dyeColorant.getId());
    }

    @Override
    public DyeColorant getCustomCollarColor() {
        return DyeColorant.byId(this.dataTracker.get(GELATIN_COLLAR_COLOR));
    }

    @Override
    public boolean isRainbowCollared() {
        return this.dataTracker.get(GELATIN_RAINBOW_COLLAR);
    }

    @Override
    public void setRainbowCollar(boolean rainbowCollarMode) {
        this.dataTracker.set(GELATIN_RAINBOW_COLLAR, rainbowCollarMode);
    }
}
