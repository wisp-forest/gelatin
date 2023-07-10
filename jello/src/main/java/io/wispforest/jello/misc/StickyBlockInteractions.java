package io.wispforest.jello.misc;

import com.mojang.logging.LogUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Property;
import net.minecraft.tag.TagKey;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class StickyBlockInteractions {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final Map<TagKey<Block>, InteractionStorage> TAG_LINKED_INTERACTIONS = new HashMap<>();
    private static final Map<Block, InteractionStorage> BLOCK_LINKED_INTERACTIONS = new HashMap<>();

    public static void registerBlockTag(TagKey<Block> blockTag, Consumer<InteractionStorage> consumer){
        if(TAG_LINKED_INTERACTIONS.containsKey(blockTag)) {
            LOGGER.warn("[StickyBlockInteractions]: A Tag already has the given interaction storage, meaning such wont be created again. [Tag ID: {}]", blockTag.id());
        }

        var storage = TAG_LINKED_INTERACTIONS.computeIfAbsent(blockTag, entry1 -> new InteractionStorage());

        if(consumer != null) consumer.accept(storage);
    }

    public static void registerBlock(Block block, Consumer<InteractionStorage> consumer){
        if(BLOCK_LINKED_INTERACTIONS.containsKey(block)) {
            LOGGER.warn("[StickyBlockInteractions]: A block already has the given interaction storage, meaning such wont be created again. [Block ID: {}]", Registry.BLOCK.getId(block));
        }

        var storage = BLOCK_LINKED_INTERACTIONS.computeIfAbsent(block, entry1 -> new InteractionStorage());

        if(consumer != null) consumer.accept(storage);
    }

    public static class InteractionStorage {
        private final Map<Property<?>, InteractionCheck<BlockState>> propertyBasedInteraction = new HashMap<>();

        public void addStateInteraction(Property<?> property, InteractionCheck<BlockState> state){
            if(propertyBasedInteraction.containsKey(property)){
                LOGGER.error("[StickyBlockInteractions]: A property being registered already has a linked interaction. [Property: {}]", property.getName());

                return;
            }

            propertyBasedInteraction.put(property, state);
        }

        public boolean allowInteraction(BlockState stickBlock, BlockState adjacentBlock, Direction motion, Direction piston){
            for (Property<?> property : propertyBasedInteraction.keySet()) {
                if(!stickBlock.contains(property)) continue;

                if(propertyBasedInteraction.get(property).shouldStick(stickBlock)) return true;
            }

            return false;
        }
    }

    @Nullable
    public InteractionStorage isValidEntry(BlockState state) {
        InteractionStorage storage = BLOCK_LINKED_INTERACTIONS.get(state.getBlock());

        if(storage == null){
            for (Map.Entry<TagKey<Block>, InteractionStorage> entry : TAG_LINKED_INTERACTIONS.entrySet()) {
                if(state.isIn(entry.getKey())){
                    storage = entry.getValue();

                    break;
                }
            }
        }

        return storage;
    }

    public interface InteractionCheck<T> {
        boolean shouldStick(T data);
    }
}
