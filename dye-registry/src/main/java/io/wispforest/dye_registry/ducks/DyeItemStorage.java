package io.wispforest.dye_registry.ducks;

import io.wispforest.common.util.BetterItemOps;
import io.wispforest.dye_registry.DyeColorant;
import io.wispforest.dye_registry.DyeColorantRegistry;
import io.wispforest.owo.ops.ItemOps;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.ApiStatus;

/**
 * Interface used to store Color Data within A Colorable Item.
 * <p>A example of this use is within {@link DyeBlockTool} for Minecrafts {@link DyeItem}</p>
 */
public interface DyeItemStorage {

    /**
     * This will return the {@link DyeColorant} of the Item
     */
    default DyeColorant getDyeColorant(){
        return DyeColorantRegistry.NULL_VALUE_NEW;
    };

    /**
     * Main method call to tell Jello that this item is or isn't a DyeItem.
     */
    default boolean isDyeItem(){
        return false;
    }

    //------------------------------------------

    /**
     * [Warning]: Advise not changing this after the fact and only for initilizing the {@link DyeColorant}
     */
    @ApiStatus.NonExtendable
    default void setDyeColor(DyeColorant dyeColorant){};

}
