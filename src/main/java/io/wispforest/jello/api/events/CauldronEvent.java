package io.wispforest.jello.api.events;

import io.wispforest.jello.mixin.cauldron.AbstractCauldronBlockMixin;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.AbstractCauldronBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Event for custom cauldron interactions before Vanillas system. See {@link AbstractCauldronBlockMixin} for reference to its injection
 */
public class CauldronEvent {

    public enum CauldronType {

        /**
         * An enum of the types of cauldron blocks based on there {@link CauldronBehavior} {@link Map}.
         * <p>
         * Used internally for the event as a way to differentiate what cauldron is being used.
         */

        EMPTY(() -> CauldronBehavior.EMPTY_CAULDRON_BEHAVIOR),
        WATER(() -> CauldronBehavior.WATER_CAULDRON_BEHAVIOR),
        LAVA(() -> CauldronBehavior.LAVA_CAULDRON_BEHAVIOR),
        SNOW(() -> CauldronBehavior.POWDER_SNOW_CAULDRON_BEHAVIOR);

        private final Supplier<Map<Item, CauldronBehavior>> behaviorMap;

        private CauldronType(Supplier<Map<Item, CauldronBehavior>> behaviorMap) {
            this.behaviorMap = behaviorMap;
        }

        public static CauldronType getCauldronType(Map<Item, CauldronBehavior> behaviorMap) {
            CauldronType[] cauldronTypes = CauldronType.values();

            for (CauldronType cauldronType : cauldronTypes) {
                if (cauldronType.getBehaviorMap() == behaviorMap) {
                    return cauldronType;
                }
            }

            return null;
        }

        private Map<Item, CauldronBehavior> getBehaviorMap() {
            return behaviorMap.get();
        }

    }

    public static Event<BeforeCauldronBehavior> BEFORE_CAULDRON_BEHAVIOR = EventFactory.createArrayBacked(BeforeCauldronBehavior.class,
            (listeners) -> (state, world, pos, player, hand, stack, cauldronType) -> {
                for (BeforeCauldronBehavior event : listeners) {
                    ActionResult result = event.interact(state, world, pos, player, hand, stack, cauldronType);

                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });

    public interface BeforeCauldronBehavior {

        /**
         * An event before the cauldron behavior map for the Cauldron block is tested within {@link AbstractCauldronBlock#onUse(BlockState, World, BlockPos, PlayerEntity, Hand, BlockHitResult)}
         *
         * @param state        The BlockState of the cauldron
         * @param world        The world of the cauldron Block
         * @param pos          The BlockPos of the cauldron
         * @param player       The player of the action
         * @param hand         The hand being used by the player
         * @param stack        The itemstack being used
         * @param cauldronType The cauldron type gathered from the map based on the cauldron block
         * @return Whether the cauldron actions will go through or not
         */

        ActionResult interact(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, ItemStack stack, CauldronType cauldronType);
    }
}
