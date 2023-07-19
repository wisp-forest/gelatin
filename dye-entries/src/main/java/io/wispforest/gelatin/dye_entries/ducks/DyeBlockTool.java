package io.wispforest.gelatin.dye_entries.ducks;

import io.wispforest.gelatin.common.CommonInit;
import io.wispforest.gelatin.dye_registry.DyeColorant;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import io.wispforest.gelatin.dye_registry.ducks.DyeBaseTool;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

/**
 * Interface used to store Color Data within A Colorable Item.
 */
public interface DyeBlockTool extends DyeBaseTool {
    /**
     * Main method call for Dyeing any block before the items {@link Item#useOnBlock(ItemUsageContext)} method call. Override this for custom DyeColoring for an Item
     *
     * @param context Same usage Context as {@link Item#useOnBlock(ItemUsageContext)}
     * @return {@link ActionResult#PASS} if the event won't cancel or return any other result for the {@link Item#useOnBlock(ItemUsageContext)} to be stopped
     */
    default ActionResult attemptToDyeBlock(ItemUsageContext context){
        if (CommonInit.getConfig().isBlockDyeingEnabled() && context.getPlayer() != null) {
            return this.attemptToDyeBlock(context.getWorld(), context.getPlayer(), context.getBlockPos(), context.getStack(), context.getHand());
        }

        return ActionResult.PASS;
    }

    /**
     * Main method call for Dyeing any block before the items {@link Item#useOnBlock(ItemUsageContext)} method call. Override this for custom DyeColoring for an Item
     *
     * @return {@link ActionResult#PASS} if the event won't cancel or return any other result for the {@link Item#useOnBlock(ItemUsageContext)} to be stopped
     */
    default ActionResult attemptToDyeBlock(World world, PlayerEntity player, BlockPos blockPos, ItemStack stack, Hand hand){
        return ActionResult.PASS;
    }

    //-----------------------------------------------------------------------------------------------------------------------------------

    @NotNull
    default DyeColorant attemptToDyeCauldron(World world, PlayerEntity player, BlockPos blockPos, ItemStack stack, Hand hand){
        return DyeColorantRegistry.NULL_VALUE_NEW;
    }
}
