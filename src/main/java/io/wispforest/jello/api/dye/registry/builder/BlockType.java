package io.wispforest.jello.api.dye.registry.builder;

import io.wispforest.owo.util.TagInjector;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BlockType {

    public final String blockType;
    public final int namePartCount;
    public final Identifier defaultBlockID;

    @Nullable public TagKey<Block> blockTag = null;
    @Nullable public TagKey<Item> itemTag = null;
    @Nullable public ItemGroup vanillaItemGroupOverride = null;

    public BlockType(String type, int namePartCount, Identifier defaultBlockID) {
        this.blockType = type;
        this.namePartCount = namePartCount;
        this.defaultBlockID = defaultBlockID;
    }

    public BlockType(String type, int namePartCount, Identifier defaultBlockID, @Nullable TagKey<Block> blockTag, @Nullable TagKey<Item> itemTag, @Nullable ItemGroup vanillaItemGroupOverride) {
        this(type, namePartCount, defaultBlockID);

        if (blockTag != null)
            this.blockTag = blockTag;

        if (itemTag != null)
            this.itemTag = itemTag;

        if(vanillaItemGroupOverride != null)
            this.vanillaItemGroupOverride = vanillaItemGroupOverride;
    }

    public static class Builder{

        private final List<BlockType> BUILT_BLOCK_TYPES;

        public Builder(){
            this.BUILT_BLOCK_TYPES = new ArrayList<>();
        }

        public Builder of(String type, String modid, TagKey<Block> blockTag) {
            return of(type, new Identifier(modid,"white_" + type), blockTag);
        }

        public Builder of(String type, Identifier defaultBlockID, TagKey<Block> blockTag) {
            return of(type, defaultBlockID, blockTag, null, null);
        }

        public Builder of(String type, Identifier defaultBlockID, TagKey<Block> blockTag, ItemGroup itemGroup) {
            return of(type, defaultBlockID, blockTag, null, itemGroup);
        }

        public Builder of(String type, Identifier defaultBlockID, TagKey<Block> blockTag, TagKey<Item> itemTag) {
            return of(type, defaultBlockID, blockTag, itemTag, null);
        }

        public Builder of(String type, Identifier defaultBlockID, TagKey<Block> blockTag, TagKey<Item> itemTag, ItemGroup itemGroup) {
            this.BUILT_BLOCK_TYPES.add(new BlockType(type, type.split("_").length, defaultBlockID, blockTag, itemTag, itemGroup));

            return this;
        }


        public List<BlockType> getTypes(){
            return BUILT_BLOCK_TYPES;
        }
    }

    public String toString() {
        return "_" + blockType;
    }

    public String getSuffix() {
        return toString();
    }

    public boolean isVariantType(Identifier blockId) {
        String[] pathParts = blockId.getPath().split("_");

        String blockType = "";
        for (int i = pathParts.length - namePartCount; i < pathParts.length; i++) {
            blockType = blockType.concat(pathParts[i]);
        }

        return blockType.equals(this.blockType);
    }

    public static RegistryHelper createHelper(BlockType blockType, Block block) {
        return createHelper(blockType, block, true);
    }

    public static RegistryHelper createHelper(BlockType blockType, Block block, boolean createBlockItem) {
        return new RegistryHelper(block, blockType, createBlockItem);
    }

    public static class RegistryHelper {
        public final Block block;
        public final BlockType blockType;
        public final boolean createBlockItem;

        public RegistryHelper(Block block, BlockType blockType) {
            this(block, blockType, true);
        }

        public RegistryHelper(Block block, BlockType blockType, boolean createBlockItem) {
            this.block = block;
            this.blockType = blockType;
            this.createBlockItem = createBlockItem;
        }

        public void initTags() {
            if (this.blockType.blockTag != null) {
                TagInjector.injectBlocks(blockType.blockTag.id(), block);
            }

            if (this.blockType.itemTag != null) {
                TagInjector.injectItems(blockType.itemTag.id(), block.asItem());
            }
        }
    }
}
