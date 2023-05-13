package io.wispforest.gelatin.dye_entries.mixins;

import io.wispforest.gelatin.common.util.TrackedDataHandlerExtended;
import io.wispforest.gelatin.dye_entries.data.CustomSheepLootTables;
import io.wispforest.gelatin.dye_entries.ducks.SheepDyeColorStorage;
import io.wispforest.gelatin.dye_entries.variants.DyeableVariantManager;
import io.wispforest.gelatin.dye_entries.variants.impl.VanillaBlockVariants;
import io.wispforest.gelatin.dye_registry.DyeColorant;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Shearable;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SheepEntity.class)
public abstract class SheepEntityMixin extends AnimalEntity implements SheepDyeColorStorage, Shearable {

    @Shadow
    public abstract DyeColor getColor();

    @Shadow
    public abstract boolean isSheared();

    @Shadow
    public abstract void setColor(DyeColor color);

    @Unique private static final TrackedData<Identifier> OVERRIDE_WOOL_COLOR = DataTracker.registerData(SheepEntity.class, TrackedDataHandlerExtended.IDENTIFIER);

    protected SheepEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void jello$initDataTracker(CallbackInfo ci) {
        this.dataTracker.startTracking(OVERRIDE_WOOL_COLOR, DyeColorantRegistry.NULL_VALUE_NEW.getId());
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void jello$writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putString("JelloWoolColor", this.getWoolDyeColor().getId().toString());
    }

    @Inject(method = "getLootTableId", at = @At("HEAD"), cancellable = true)
    private void useProperLootTableId(CallbackInfoReturnable<Identifier> cir) {
        if (this.isSheared()) return;

        DyeColorant color = this.getWoolDyeColor();

        if (color != DyeColorantRegistry.NULL_VALUE_NEW)
            cir.setReturnValue(CustomSheepLootTables.createSheepLootTableIdFromColor(this.getWoolDyeColor()));
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
    private void readJelloWoolData(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("Color")) {
            this.setWoolDyeColor(DyeColorant.byOldDyeColor(this.getColor()));
        }

        if (nbt.contains("JelloWoolColor")) {
            this.setWoolDyeColor(Identifier.tryParse(nbt.getString("JelloWoolColor")));
//            this.setColor(DyeColor.byId(17));
        }
    }

    @ModifyArg(method = "sheared", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/SheepEntity;dropItem(Lnet/minecraft/item/ItemConvertible;I)Lnet/minecraft/entity/ItemEntity;"))
    private ItemConvertible useProperDropItem(ItemConvertible par1) {
        if (this.getWoolDyeColor() != DyeColorantRegistry.NULL_VALUE_NEW) {
            return DyeableVariantManager.getDyedBlockVariant(this.getWoolDyeColor(), VanillaBlockVariants.WOOL);
        }

        return par1;
    }

    @Inject(method = "initialize", at = @At("RETURN"))
    private void setWoolDyeColorOnInit(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, EntityData entityData, NbtCompound entityNbt, CallbackInfoReturnable<EntityData> cir) {
        this.setWoolDyeColor(generateDefaultDyeColorColorant(world.getRandom()));
    }

    @Inject(method = "createChild(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/PassiveEntity;)Lnet/minecraft/entity/passive/SheepEntity;", at = @At("RETURN"))
    private void setChildColor(ServerWorld serverWorld, PassiveEntity passiveEntity, CallbackInfoReturnable<SheepEntity> cir) {
        ((SheepDyeColorStorage) cir.getReturnValue()).setWoolDyeColor(this.getChildDyeColorant(this, (AnimalEntity) passiveEntity));
    }

    @Unique
    private static DyeColorant generateDefaultDyeColorColorant(Random random) {
        int i = random.nextInt(100);
        if (i < 5) {
            return DyeColorantRegistry.BLACK;
        } else if (i < 10) {
            return DyeColorantRegistry.GRAY;
        } else if (i < 15) {
            return DyeColorantRegistry.LIGHT_GRAY;
        } else if (i < 18) {
            return DyeColorantRegistry.BROWN;
        } else if (i < 80) {
            return random.nextInt(500) == 0 ? DyeColorantRegistry.PINK : DyeColorantRegistry.WHITE;
        } else {
            return DyeColorantRegistry.getRandomColorant();
        }
    }

    @Unique
    private DyeColorant getChildDyeColorant(AnimalEntity firstParent, AnimalEntity secondParent) {
        DyeColorant dyeColor = ((SheepDyeColorStorage) firstParent).getWoolDyeColor();
        DyeColorant dyeColor2 = ((SheepDyeColorStorage) secondParent).getWoolDyeColor();

        int blendedColors = DyeColorant.blendDyeColors(dyeColor, dyeColor2);

        return DyeColorantRegistry.getNearestColorant(blendedColors);
    }

    @Override
    public DyeColorant getWoolDyeColor() {
        return DyeColorantRegistry.DYE_COLOR.get(this.dataTracker.get(OVERRIDE_WOOL_COLOR));
    }

    @Override
    public void setWoolDyeColor(DyeColorant dyeColorant) {
        this.dataTracker.set(OVERRIDE_WOOL_COLOR, dyeColorant.getId());
    }

    public void setWoolDyeColor(Identifier identifier) {
        this.dataTracker.set(OVERRIDE_WOOL_COLOR, identifier);
    }
}
