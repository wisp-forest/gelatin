package io.wispforest.jello.mixin.dye;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.misc.CustomSheepLootTables;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.jello.api.dye.registry.variants.DyedVariantContainer;
import io.wispforest.jello.api.dye.registry.variants.VanillaBlockVariants;
import io.wispforest.jello.misc.ducks.SheepDyeColorStorage;
import io.wispforest.jello.api.util.ColorUtil;
import io.wispforest.jello.api.util.TrackedDataHandlerExtended;
import net.minecraft.entity.*;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Random;

@Mixin(SheepEntity.class)
public abstract class SheepEntityMixin extends AnimalEntity implements SheepDyeColorStorage, Shearable {

    @Shadow
    @Final
    private static TrackedData<Byte> COLOR;

    @Shadow
    public abstract void setSheared(boolean sheared);

    @Shadow
    @Final
    private static Map<DyeColor, ItemConvertible> DROPS;

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

    /**
     * @author Dragon_seeker
     */
    @Overwrite
    public Identifier getLootTableId() {
        if (this.isSheared()) {
            return this.getType().getLootTableId();
        } else {
            if (this.getWoolDyeColor() != DyeColorantRegistry.NULL_VALUE_NEW) {
                return CustomSheepLootTables.createSheepLootTableIdFromColor(this.getWoolDyeColor());
            } else {
                return LootTables.WHITE_SHEEP_ENTITY;
            }
        }
    }

    /**
     * @author Dragon_seeker
     */
    @Overwrite
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setSheared(nbt.getBoolean("Sheared"));

        if (nbt.contains("Color")) {
            var color = nbt.getByte("Color");

            this.setWoolDyeColor(DyeColorant.byOldIntId(color));
            this.setColor(DyeColor.byId(color));
        }

        if (nbt.contains("JelloWoolColor")) {
            this.setWoolDyeColor(Identifier.tryParse(nbt.getString("JelloWoolColor")));
//            this.setColor(DyeColor.byId(17));
        }
    }

    /**
     * @author Dragon_seeker
     */
    @Overwrite
    public void sheared(SoundCategory shearedSoundCategory) {
        this.world.playSoundFromEntity((PlayerEntity) null, this, SoundEvents.ENTITY_SHEEP_SHEAR, shearedSoundCategory, 1.0F, 1.0F);
        this.setSheared(true);
        int i = 1 + this.random.nextInt(3);

        for (int j = 0; j < i; ++j) {
            ItemEntity itemEntity;

            if (this.getWoolDyeColor() != DyeColorantRegistry.NULL_VALUE_NEW) {
                itemEntity = this.dropItem((ItemConvertible) DyedVariantContainer.getDyedBlockVariant(this.getWoolDyeColor(), VanillaBlockVariants.WOOL), 1);
            } else {
                itemEntity = this.dropItem((ItemConvertible) DROPS.get(this.getColor()), 1);
            }

            if (itemEntity != null) {
                itemEntity.setVelocity(
                        itemEntity.getVelocity()
                                .add(
                                        (double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.1F),
                                        (double) (this.random.nextFloat() * 0.05F),
                                        (double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.1F)
                                )
                );
            }
        }
    }

    /**
     * @author Dragon_seeker
     */
    @Nullable
    @Overwrite
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.setWoolDyeColor(generateDefaultDyeColorColorant(world.getRandom()));
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    /**
     * @author Dragon_seeker
     */
    @Nullable
    @Overwrite
    public SheepEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
        SheepEntity sheepEntity = (SheepEntity) passiveEntity;
        SheepEntity sheepEntity2 = EntityType.SHEEP.create(serverWorld);
        ((SheepDyeColorStorage) sheepEntity2).setWoolDyeColor(this.getChildDyeColorant(this, sheepEntity));
        return sheepEntity2;
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

        int blendedColors = ColorUtil.blendDyeColors(dyeColor, dyeColor2);

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
