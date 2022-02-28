package com.dragon.jello.common.items;

import com.dragon.jello.common.Jello;
import com.dragon.jello.lib.events.ColorBlockUtil;
import com.dragon.jello.lib.events.ColorEntityEvent;
import com.dragon.jello.lib.registry.ColorBlockRegistry;
import com.dragon.jello.lib.registry.ColorizeRegistry;
import com.dragon.jello.mixin.ducks.DyeableEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

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

        Jello.DEBUG_LOGGER.info(context.getStack() + " / " + context.getStack().getNbt());

        if(canClean(context.getStack())) {
            if (!ColorBlockUtil.changeBlockColor(world, context.getBlockPos(), blockState, ColorBlockRegistry.getVariant(blockState.getBlock(), null), user)) {
                return ActionResult.PASS;
            }

            user.playSound(SoundEvents.ITEM_BUCKET_EMPTY, 1.0F, 1.55F);
            if (!world.isClient) {
                incrementDirtiness(context.getStack());
            }

            return ActionResult.SUCCESS;
        }else{
            return ActionResult.FAIL;
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemInHand = user.getMainHandStack();

        Jello.DEBUG_LOGGER.info(itemInHand + " / " + itemInHand.getNbt());

        if(user.shouldCancelInteraction()){
            if (itemInHand.getDamage() != -1) {
                if (user instanceof DyeableEntity dyeableEntity && dyeableEntity.isDyed()) {
                    dyeableEntity.setDyeColorID(16);


                    if (!world.isClient) {
                        incrementDirtiness(itemInHand);
                    }

                    return TypedActionResult.success(itemInHand);
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

        Jello.DEBUG_LOGGER.info(stack + " / " + stack.getNbt());

        if(ColorizeRegistry.isRegistered(entity)) {
            if (stack.getDamage() != -1 && ColorEntityEvent.washEntityEvent(user, entity, user.getMainHandStack())) {

                if (!user.world.isClient) {
                    incrementDirtiness(stack);
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

    private static void incrementDirtiness(ItemStack itemStack){
        setDirtiness(itemStack.getOrCreateNbt(), 1);
    }

    private static void setDirtiness(NbtCompound nbt, int amount){
        int newDirtinessValue = nbt.getInt(DIRTINESS_KEY) + amount;

        nbt.putInt(DIRTINESS_KEY, Math.min(newDirtinessValue, MAX_DIRTINESS));
    }

    private static int getDirtinessValue(ItemStack stack){
        return getDirtinessValue(stack.getOrCreateNbt());
    }

    private static int getDirtinessValue(NbtCompound nbt){
        return nbt.getInt(DIRTINESS_KEY);
    }

    private static boolean canClean(ItemStack itemStack){
        return getDirtinessValue(itemStack.getOrCreateNbt()) < MAX_DIRTINESS;
    }

    public static float getDirtinessStage(ItemStack itemStack){
        int dirtiness = getDirtinessValue(itemStack.getOrCreateNbt());

        return dirtiness / 64F;
    }
}
