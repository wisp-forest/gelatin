package io.wispforest.jello.main.common.items;

import io.wispforest.jello.main.common.Jello;
import io.wispforest.jello.api.dye.registry.DyeColorRegistry;
import io.wispforest.jello.api.events.ColorBlockUtil;
import io.wispforest.jello.api.events.ColorEntityEvent;
import io.wispforest.jello.api.registry.ColorBlockRegistry;
import io.wispforest.jello.api.registry.ColorizeRegistry;
import io.wispforest.jello.api.mixin.ducks.ConstantColorEntity;
import io.wispforest.jello.api.mixin.ducks.DyeableEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidDrainable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class SpongeItem extends Item {

    public static final String DIRTINESS_TRANSLATION_KEY = "item.jello.sponge.dirty";

    public static final String DIRTINESS_KEY = "Dirtiness";
    private static final int MAX_DIRTINESS = 64;

    public SpongeItem(Settings settings) {
        super(settings);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        if(getDirtinessValue(stack) == 64) {
            return DIRTINESS_TRANSLATION_KEY;
        }
        return super.getTranslationKey(stack);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity user = context.getPlayer();
        World world = context.getWorld();
        BlockState blockState = world.getBlockState(context.getBlockPos());

        if(canClean(context.getStack())) {
            if (!ColorBlockUtil.changeBlockColor(world, context.getBlockPos(), blockState, ColorBlockRegistry.getVariant(blockState.getBlock(), null), user)) {
                return ActionResult.PASS;
            }

            if (!world.isClient) {
                incrementDirtiness(context.getStack(), user);
                world.playSound(null, user.getBlockPos(), SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.PLAYERS, 1.0F, 1.55F);
            }

            return ActionResult.SUCCESS;
        }else{
            return ActionResult.FAIL;
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemInHand = user.getStackInHand(hand);

        if(user.shouldCancelInteraction()){
            if (canClean(itemInHand)) {
                if (user instanceof DyeableEntity dyeableEntity && dyeableEntity.isDyed()) {
                    dyeableEntity.setDyeColor(DyeColorRegistry.NULL_VALUE_NEW);


                    if (!world.isClient) {
                        incrementDirtiness(itemInHand, user);

                        world.playSound(null, user.getBlockPos(), SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.PLAYERS, 1.0F, 1.55F);
                    }


                    return TypedActionResult.success(itemInHand);
                }
            }
        }else{
            if(itemInHand.getOrCreateNbt().getInt(DIRTINESS_KEY) != 0) {
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
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if(!Jello.MAIN_CONFIG.enableDyeingEntitys || (entity instanceof PlayerEntity && !Jello.MAIN_CONFIG.enableDyeingPlayers)){
            return ActionResult.PASS;
        }

        if(ColorizeRegistry.isRegistered(entity)) {
            if(entity instanceof ConstantColorEntity constantColorEntity && constantColorEntity.isColored()){
                return ActionResult.PASS;
            }

            if (stack.getDamage() != -1 && ColorEntityEvent.washEntityEvent(user, entity, user.getMainHandStack())) {

                if (!user.world.isClient) {
                    incrementDirtiness(stack, user);

                    entity.getWorld().playSound(null, user.getBlockPos(), SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.PLAYERS, 1.0F, 1.55F);
                }

                return ActionResult.SUCCESS;
            } else {
                return ActionResult.FAIL;
            }
        }

        return ActionResult.PASS;
    }

    @Override
    public void postProcessNbt(NbtCompound nbt) {
        setDirtiness(nbt, 0);
        super.postProcessNbt(nbt);
    }

    private static void cleanSponge(ItemStack itemStack, PlayerEntity player){
        if(!player.getAbilities().creativeMode) {
            setDirtiness(itemStack.getOrCreateNbt(), 0);
        }
    }

    private static void incrementDirtiness(ItemStack itemStack, PlayerEntity player){
        if(!player.getAbilities().creativeMode) {
            int newDirtinessValue = itemStack.getOrCreateNbt().getInt(DIRTINESS_KEY) + 1;

            setDirtiness(itemStack.getOrCreateNbt(), newDirtinessValue);
        }
    }

    private static void setDirtiness(NbtCompound nbt, int amount){
        nbt.putInt(DIRTINESS_KEY, Math.min(amount, MAX_DIRTINESS));
    }

    private static int getDirtinessValue(ItemStack stack){
        return stack.getOrCreateNbt().getInt(DIRTINESS_KEY);
    }

    private static boolean canClean(ItemStack itemStack){
        return getDirtinessValue(itemStack) < MAX_DIRTINESS;
    }

    public static float getDirtinessStage(ItemStack itemStack){
        int dirtiness = getDirtinessValue(itemStack);

        return dirtiness / 64F;
    }
}
