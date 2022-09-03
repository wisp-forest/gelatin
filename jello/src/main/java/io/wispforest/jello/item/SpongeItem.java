package io.wispforest.jello.item;

import io.wispforest.gelatin.cauldron.blockentity.ColorStorageBlockEntity;
import io.wispforest.gelatin.common.events.CauldronEvent;
import io.wispforest.gelatin.dye_entries.BlockColorManipulators;
import io.wispforest.gelatin.dye_entities.ducks.DyeableEntity;
import io.wispforest.gelatin.dye_entities.misc.EntityColorManipulators;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import io.wispforest.gelatin.dye_entries.ducks.DyeBlockTool;
import io.wispforest.gelatin.dye_entities.ducks.DyeEntityTool;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.LeveledCauldronBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpongeItem extends Item implements DyeBlockTool, DyeEntityTool {

    public static final String DIRTINESS_TRANSLATION_KEY = "item.jello.sponge.dirty";

    public static final String DIRTINESS_KEY = "Dirtiness";
    private static final int MAX_DIRTINESS = 64;

    public SpongeItem(Settings settings) {
        super(settings);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        if (getDirtinessValue(stack) == 64) {
            return DIRTINESS_TRANSLATION_KEY;
        }
        return super.getTranslationKey(stack);
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return getDirtinessValue(stack) > 1;
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        return Math.round(13f - getDirtinessValue(stack) * 13f / MAX_DIRTINESS);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return MathHelper.hsvToRgb(0.16f, 1f, (12 + Math.round(64f - getDirtinessValue(stack) * 64f / MAX_DIRTINESS)) / 100f);
    }

    @Override
    public ActionResult attemptToDyeBlock(World world, PlayerEntity player, BlockPos blockPos, ItemStack stack, Hand hand) {
        if (canClean(stack)) {
            if (!BlockColorManipulators.changeBlockColor(world, blockPos, DyeColorantRegistry.NULL_VALUE_NEW, player, false)) {
                return ActionResult.PASS;
            }

            if (!world.isClient) {
                incrementDirtiness(stack, player);
                world.playSound(null, player.getBlockPos(), SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.PLAYERS, 1.0F, 1.55F);
            }

            return ActionResult.SUCCESS;
        } else {
            return ActionResult.FAIL;
        }
    }

    @Override
    public ActionResult attemptToDyeEntity(World world, PlayerEntity user, LivingEntity entity, ItemStack stack, Hand hand) {
        if (stack.getDamage() != -1 && EntityColorManipulators.washEntityEvent((DyeableEntity) entity)) {
            if (!user.world.isClient) {
                incrementDirtiness(stack, user);

                world.playSound(null, user.getBlockPos(), SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.PLAYERS, 1.0F, 1.55F);
            }

            return ActionResult.SUCCESS;
        } else {
            return ActionResult.FAIL;
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemInHand = user.getStackInHand(hand);

        if (user.shouldCancelInteraction()) {
            if (canClean(itemInHand)) {
                if (user instanceof DyeableEntity dyeableEntity && dyeableEntity.isDyed()) {
                    dyeableEntity.setDyeColor(DyeColorantRegistry.NULL_VALUE_NEW);

                    if (!world.isClient) {
                        incrementDirtiness(itemInHand, user);

                        world.playSound(null, user.getBlockPos(), SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.PLAYERS, 1.0F, 1.55F);
                    }

                    return TypedActionResult.success(itemInHand);
                }
            }
        } else {
            if (itemInHand.getOrCreateNbt().getInt(DIRTINESS_KEY) != 0) {
                BlockHitResult blockHitResult = raycast(world, user, RaycastContext.FluidHandling.SOURCE_ONLY);

                if (blockHitResult.getType() == HitResult.Type.MISS || blockHitResult.getType() != HitResult.Type.BLOCK) {
                    return TypedActionResult.pass(itemInHand);
                } else {
                    BlockPos blockPos = blockHitResult.getBlockPos();

                    if (world.canPlayerModifyAt(user, blockPos) && user.canPlaceOn(blockPos.offset(blockHitResult.getSide()), blockHitResult.getSide(), itemInHand) && world.getBlockState(blockPos).getBlock() instanceof FluidDrainable fluidDrainable) {

                        fluidDrainable.tryDrainFluid(world, blockPos, world.getBlockState(blockPos));
                        fluidDrainable.getBucketFillSound().ifPresent(sound -> user.playSound(sound, 1.0F, 1.0F));

                        user.incrementStat(Stats.USED.getOrCreateStat(this));
                        world.emitGameEvent(user, GameEvent.FLUID_PICKUP, blockPos);

                        if (!world.isClient) {
                            cleanSponge(itemInHand, user);
                        }

                        return TypedActionResult.success(itemInHand, world.isClient());
                    }
                }
            }
        }

        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable(this.getTranslationKey() + ".desc").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable(this.getTranslationKey() + ".desc.dirty").formatted(Formatting.GRAY));
    }

    public static ActionResult cleanSponge(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, CauldronEvent.CauldronType cauldronType) {
        if (cauldronType == CauldronEvent.CauldronType.WATER) {
            ColorStorageBlockEntity blockEntity = (ColorStorageBlockEntity) world.getBlockEntity(pos);

            if (blockEntity != null && ColorStorageBlockEntity.isWaterColored(blockEntity)) {
                return ActionResult.PASS;
            }

            if (stack.getItem() instanceof SpongeItem) {
                if (stack.getOrCreateNbt().getInt(SpongeItem.DIRTINESS_KEY) != 0) {
                    if (!world.isClient) {
                        stack.getOrCreateNbt().putInt(SpongeItem.DIRTINESS_KEY, 0);

                        LeveledCauldronBlock.decrementFluidLevel(state, world, pos);

                        world.playSound((PlayerEntity) null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.4F);
                    }
                    return ActionResult.success(world.isClient);
                }
            }
        }

        return ActionResult.PASS;
    }

    private static void cleanSponge(ItemStack itemStack, PlayerEntity player) {
        if (!player.getAbilities().creativeMode) {
            setDirtiness(itemStack.getOrCreateNbt(), 0);
        }
    }

    private static void incrementDirtiness(ItemStack itemStack, PlayerEntity player) {
        if (!player.getAbilities().creativeMode) {
            int newDirtinessValue = itemStack.getOrCreateNbt().getInt(DIRTINESS_KEY) + 1;

            setDirtiness(itemStack.getOrCreateNbt(), newDirtinessValue);
        }
    }

    private static void setDirtiness(NbtCompound nbt, int amount) {
        nbt.putInt(DIRTINESS_KEY, Math.min(amount, MAX_DIRTINESS));
    }

    private static int getDirtinessValue(ItemStack stack) {
        return stack.getOrCreateNbt().getInt(DIRTINESS_KEY);
    }

    private static boolean canClean(ItemStack itemStack) {
        return getDirtinessValue(itemStack) < MAX_DIRTINESS;
    }

    public static float getDirtinessStage(ItemStack itemStack) {
        int dirtiness = getDirtinessValue(itemStack);

        return dirtiness / 64F;
    }
}
