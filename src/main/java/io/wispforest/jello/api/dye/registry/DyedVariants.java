package io.wispforest.jello.api.dye.registry;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.item.ColoredBlockItem;
import io.wispforest.jello.api.dye.registry.builder.BaseBlockBuilder;
import io.wispforest.jello.api.dye.registry.builder.BlockType;
import io.wispforest.jello.api.dye.registry.builder.VanillaBlockBuilder;
import io.wispforest.jello.api.mixin.mixins.accessors.SettingsAccessor;
import io.wispforest.jello.api.registry.ColorBlockRegistry;
import io.wispforest.jello.main.common.Jello;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

public class DyedVariants {

    public static final Map<DyeColorant, DyedVariants> DYED_VARIANTS = new HashMap<>();

    public List<Item> dyedItems;
    public Map<BlockType, Block> dyedBlocks;

    public DyeColorant dyeColorant;

    private Builder builder;

    protected DyedVariants(List<Item> dyedItems, Map<BlockType, Block> dyedBlocks, DyeColorant dyeColorant, Builder builder){
        this.dyedItems = dyedItems;
        this.dyedBlocks = dyedBlocks;
        this.dyeColorant = dyeColorant;
        this.builder = builder;
    }

    @Override
    public String toString() {
        return "[ " + dyeColorant.toString() + " / " + dyedBlocks.toString() + " / " + dyedItems.toString() + " ]";
    }

    public static void addToAlreadyExistingVariants(BaseBlockBuilder baseBlockBuilder){
        if(!DYED_VARIANTS.isEmpty()) {
            for (Map.Entry<DyeColorant, DyedVariants> entry : DYED_VARIANTS.entrySet()) {
                DyedVariants dyedVariants = entry.getValue();
                dyedVariants.builder.addColoredVariant(dyedVariants, baseBlockBuilder);
            }
        }
    }

    public static class Builder {
        public DyeColorant dyeColorant;

        private final BlockBuilder blockBuilder;
        private final ItemBuilder itemBuilder;

        public Builder(DyeColorant dyeColorant, Item.Settings settings){
            this(dyeColorant, settings, settings);
        }

        public Builder(DyeColorant dyeColorant, Item.Settings itemSettings, Item.Settings blockItemSettings){
            this.blockBuilder = new BlockBuilder(dyeColorant, blockItemSettings, dyeColorant.getId().getNamespace());
            this.itemBuilder = new ItemBuilder(dyeColorant, itemSettings, dyeColorant.getId().getNamespace());

            this.dyeColorant = dyeColorant;
        }

        @ApiStatus.Internal
        protected Builder(DyeColorant dyeColorant){
            this.blockBuilder = new BlockBuilder(dyeColorant, new Item.Settings(), dyeColorant.getId().getNamespace()).readOnlyMode();
            this.itemBuilder = new ItemBuilder(dyeColorant, new Item.Settings(), dyeColorant.getId().getNamespace()).readOnlyMode();

            this.dyeColorant = dyeColorant;
        }

        public DyedVariants createColoredVanillaVariants(boolean useJelloAPIModelRedirect){
            Map<BlockType, Block> blockVariants = this.blockBuilder.createBlockVariants(useJelloAPIModelRedirect);
            List<Item> itemVariants;

            if(Objects.equals(dyeColorant.getId().getNamespace(), Jello.MODID)) {
                itemVariants = this.itemBuilder.createItemVariants(new OwoItemSettings().group(ItemGroup.MISC).tab(1));
            }else{
                itemVariants = this.itemBuilder.createItemVariants(useJelloAPIModelRedirect);
            }

            DyedVariants dyedVariants = new DyedVariants(itemVariants, blockVariants, this.dyeColorant, this);

            ColorBlockRegistry.registerDyeColorant(dyeColorant, dyedVariants);

            return DYED_VARIANTS.put(dyeColorant, new DyedVariants(itemVariants, blockVariants, this.dyeColorant, this));
        }

        public void addColoredVariant(DyedVariants dyedVariants, BaseBlockBuilder baseBlockBuilder){
            Map<BlockType, Block> blockVariants = this.blockBuilder.createBlockVariant(baseBlockBuilder);
            dyedVariants.dyedBlocks.putAll(blockVariants);
        }

        private static class BlockBuilder {

            private final DyeColorant dyeColorant;
            private final Item.Settings settings;
            private final String modid;

            private boolean useJelloAPIModelRedirect = true;
            private boolean readOnly = false;

            private Map<BlockType, Block> BLOCK_VARS;

            private BlockBuilder(DyeColorant dyeColorant, Item.Settings settings, String modid){
                VanillaBlockBuilder.init();

                this.dyeColorant = dyeColorant;
                this.settings = settings;
                this.modid = modid;
            }

            public Map<BlockType, Block> createBlockVariants(boolean useJelloAPIModelRedirect) {
                BLOCK_VARS = new HashMap<>();

                this.useJelloAPIModelRedirect = useJelloAPIModelRedirect;

                for(VanillaBlockBuilder builder : VanillaBlockBuilder.VANILLA_BUILDERS){
                    for(BlockType.RegistryHelper registryHelper : builder.build(dyeColorant, readOnly)){
                        BLOCK_VARS.put(registryHelper.blockType, registerBlock(registryHelper, registryHelper.blockType.vanillaItemGroupOverride));

                        if(!readOnly){
                            registryHelper.initTags();
                        }
                    }
                }

                for(BaseBlockBuilder builder : BaseBlockBuilder.ADDITIONAL_BUILDERS){
                    for(BlockType.RegistryHelper registryHelper : builder.build(dyeColorant)){
                        BLOCK_VARS.put(registryHelper.blockType, registerBlock(registryHelper));

                        registryHelper.initTags();
                    }
                }

                readOnly = false;
                return BLOCK_VARS;
            }

            public Map<BlockType, Block> createBlockVariant(BaseBlockBuilder baseBlockBuilder) {
                BLOCK_VARS = new HashMap<>();

                for(BlockType.RegistryHelper registryHelper : baseBlockBuilder.build(dyeColorant)){
                    BLOCK_VARS.put(registryHelper.blockType, registerBlock(registryHelper, baseBlockBuilder.modid, registryHelper.blockType.vanillaItemGroupOverride));

                    if(!readOnly){
                        registryHelper.initTags();
                    }
                }

                readOnly = false;

                return BLOCK_VARS;
            }


            private Block registerBlock(BlockType.RegistryHelper registryHelper){
                return registerBlock(registryHelper, null,null);
            }

            private Block registerBlock(BlockType.RegistryHelper registryHelper, ItemGroup vanillaItemGroupOverride){
                return registerBlock(registryHelper, null, vanillaItemGroupOverride);
            }

            private Block registerBlock(BlockType.RegistryHelper registryHelper, String modidOverride, ItemGroup vanillaItemGroupOverride) {
                Identifier identifier;
                if(this.modid == "minecraft" && modidOverride != null) {
                    identifier = new Identifier(modidOverride, this.dyeColorant.getId().getPath() + registryHelper.blockType.getSuffix());
                }else{
                    identifier = new Identifier(this.modid, this.dyeColorant.getId().getPath() + registryHelper.blockType.getSuffix());
                }

                if(!readOnly) {
                    ItemGroup groupCache = null;
                    if(vanillaItemGroupOverride != null && this.modid == "minecraft"){
                        groupCache = ((SettingsAccessor)this.settings).getGroup();
                        this.settings.group(vanillaItemGroupOverride);
                    }

                    if(this.useJelloAPIModelRedirect){
                        DyeColorantRegistry.IDENTIFIER_RESOURCE_REDIRECTS.add(identifier);
                    }

                    Block block = registryHelper.block;
                    if (registryHelper.createBlockItem) {
                        if (block instanceof ShulkerBoxBlock) {
                            Registry.register(Registry.ITEM, identifier, new BlockItem(block, this.settings.maxCount(1)));
                        } else {
                            Registry.register(Registry.ITEM, identifier, new ColoredBlockItem(block, this.settings.maxCount(64)));
                        }
                    }

                    if(groupCache != null){
                        this.settings.group(groupCache);
                    }

                    return Registry.register(Registry.BLOCK, identifier, block);
                }else{
                    return Registry.BLOCK.get(identifier);
                }
            }

            public static Item.Settings copySettings(Item.Settings oldSettings){
                SettingsAccessor settingsAccessor = (SettingsAccessor)oldSettings;

                Item.Settings newSettings = new Item.Settings();

                if(settingsAccessor.isFireproof())
                    newSettings.fireproof();

                newSettings.maxDamageIfAbsent(settingsAccessor.getMaxDamage())
                        .maxCount(settingsAccessor.getMaxCount())
                        .group(settingsAccessor.getGroup())
                        .food(settingsAccessor.getFoodComponent())
                        .recipeRemainder(settingsAccessor.getRecipeRemainder());

                return newSettings;
            }

            public BlockBuilder readOnlyMode(){
                this.readOnly = true;

                return this;
            }
        }

        private static class ItemBuilder {

            private final DyeColorant dyeColorant;
            private final Item.Settings settings;
            private final String modid;

            private boolean useJelloAPIModelRedirect = true;
            private boolean readOnly = false;

            private List<Item> itemVariants;

            private ItemBuilder(DyeColorant dyeColorant, Item.Settings settings, String modid){
                this.dyeColorant = dyeColorant;
                this.settings = settings;
                this.modid = modid;
            }

            private DyeItem createDyeItem() {
                return this.createDyeItem(this.settings);
            }

            private DyeItem createDyeItem(Item.Settings settings){
                DyeItem dyeItem;

                if(!readOnly) {
                    dyeItem = (DyeItem) register("_dye", new io.wispforest.jello.api.dye.item.DyeItem(this.dyeColorant, settings));
                }else{
                    dyeItem = (DyeItem) register("_dye", null);
                }

                itemVariants.add(dyeItem);
                return dyeItem;
            }

            @ApiStatus.Internal
            private List<Item> createItemVariants(Item.Settings settings){
                return createItemVariants(settings, false);
            }

            private List<Item> createItemVariants(boolean useJelloAPIModelRedirect){
                return createItemVariants(this.settings, useJelloAPIModelRedirect);
            }

            private List<Item> createItemVariants(Item.Settings settings, boolean useJelloAPIModelRedirect){
                itemVariants = new ArrayList<>();
                this.useJelloAPIModelRedirect = useJelloAPIModelRedirect;

                Item DyeItem = createDyeItem(settings);

                return itemVariants;
            }

            private Item register(String suffix, Item item){
                Identifier id = new Identifier(this.modid ,dyeColorant.getId().getPath() + suffix);

                if(!readOnly) {
                    if(this.useJelloAPIModelRedirect){
                        DyeColorantRegistry.IDENTIFIER_RESOURCE_REDIRECTS.add(id);
                    }

                    return Registry.register(Registry.ITEM, id, item);
                }else{
                    return Registry.ITEM.get(id);
                }
            }

            public ItemBuilder readOnlyMode(){
                this.readOnly = true;

                return this;
            }
        }
    }

}
