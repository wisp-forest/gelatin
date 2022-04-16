package io.wispforest.jello.api.ducks;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.jello.item.JelloDyeItem;
import io.wispforest.jello.misc.ducks.JelloDyeItemExtension;
import net.minecraft.item.DyeItem;
import org.jetbrains.annotations.ApiStatus;

/**
 * Interface used to store Color Data within A Colorable Item.
 * <p>A example of this use is within {@link JelloDyeItemExtension} combined with {@link DyeTool} for Minecrafts {@link DyeItem} and Jello's {@link JelloDyeItem}</p>
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
