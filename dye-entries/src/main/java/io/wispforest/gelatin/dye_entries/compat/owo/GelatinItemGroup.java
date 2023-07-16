package io.wispforest.gelatin.dye_entries.compat.owo;

import io.wispforest.gelatin.common.misc.GelatinConstants;
import io.wispforest.gelatin.dye_entries.misc.DyeEntriesItemGroups;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import io.wispforest.gelatin.dye_registry.ducks.DyeBlockStorage;
import io.wispforest.gelatin.dye_registry.ducks.DyeItemStorage;
import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GelatinItemGroup extends OwoItemGroup {

    public static final OwoItemGroup MAIN_ITEM_GROUP = new GelatinItemGroup(GelatinConstants.id("gelatin_group"));

    public GelatinItemGroup(Identifier id) {
        super(id);
    }

    @Override
    public ItemStack getIcon() {
        return Items.ORANGE_DYE.getDefaultStack();
    }

    @Override
    protected void setup() {
        //this.addTab(Icon.of(JelloItems.DYE_BUNDLE.getDefaultStack()), "jello_tools", null, ItemGroupTab.DEFAULT_TEXTURE);

        if(DyeColorantRegistry.DYE_COLOR.size() > 17) {
            Pair<Item, Item> iconPair = DyeEntriesItemGroups.getIconItems();

            this.addTab(Icon.of(iconPair.getLeft()), "dyed_item_variants", null, true);
            this.addTab(Icon.of(iconPair.getRight()), "dyed_block_variants", null, false);
        }
    }

    @Override
    public void appendStacks(DefaultedList<ItemStack> stacks) {
        super.appendStacks(stacks);

        Predicate<ItemStack> isDyedEntry;

        if(this.getSelectedTabIndex() == 0){
            isDyedEntry = stack -> stack.getItem() instanceof DyeItemStorage;
        } else {
            isDyedEntry = stack -> stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof DyeBlockStorage;
        }

        List<ItemStack> dyedBlocks = stacks.stream().filter(isDyedEntry).collect(Collectors.toList());

        stacks.removeIf(isDyedEntry);

        DyeEntriesItemGroups.sortEntries(dyedBlocks, this.getSelectedTabIndex());

    }

    @Override
    public ItemStack createIcon() {
        return null;
    }

    /*
    public GelatinItemGroup(Identifier id) {
        super(id);
    }

    @Override
    public ItemStack getIcon() {
        return Items.ORANGE_DYE.getDefaultStack();
    }

    @Override
    protected void setup() {
        //this.addTab(Icon.of(JelloItems.DYE_BUNDLE.getDefaultStack()), "jello_tools", null, ItemGroupTab.DEFAULT_TEXTURE);

        if(DyeColorantRegistry.DYE_COLOR.size() > 17) {
            DyeColorant color = DyeColorantRegistry.DYE_COLOR.get(new Identifier("jello","international_klein_blue"));//"cold_turkey"

            List<DyeableBlockVariant> allVariants = DyeableVariantRegistry.getAllBlockVariants().stream().filter(dyeableBlockVariant -> !dyeableBlockVariant.vanillaDyesOnly() && dyeableBlockVariant.createBlockItem()).toList();

            DyeableBlockVariant variant = allVariants.get(new Random().nextInt(allVariants.size()));

            Item dyeItem = null, blockItem = null;

            for(boolean validItems = false; !validItems;){
                dyeItem = Registry.ITEM.get(new Identifier(color.getId().getNamespace(), color.getName() + "_dye"));
                blockItem = Registry.ITEM.get(new Identifier(color.getId().getNamespace(), variant.getColoredEntryPath(color)));

                if(color == DyeColorantRegistry.NULL_VALUE_NEW || blockItem == Items.AIR || dyeItem == Items.AIR) {
                    color = DyeColorantRegistry.getRandomColorant();
                } else {
                    validItems = true;
                }
            }

            this.addTab(Icon.of(dyeItem), "dyed_item_variants", null, ItemGroupTab.DEFAULT_TEXTURE);
            this.addTab(Icon.of(blockItem), "dyed_block_variants", null, ItemGroupTab.DEFAULT_TEXTURE);
        }
    }

    @Override
    public void appendStacks(DefaultedList<ItemStack> stacks) {
        super.appendStacks(stacks);

        if (this.getSelectedTabIndex() == 1) {
            Predicate<ItemStack> isDyeItem = stack -> stack.getItem() instanceof DyeItemStorage;

            List<ItemStack> dyeStacks = stacks.stream().filter(isDyeItem).collect(Collectors.toList());

            stacks.removeIf(isDyeItem);

            dyeStacks.sort(dyeStackHslComparator(2));
            dyeStacks.sort(dyeStackHslComparator(1));
            dyeStacks.sort(dyeStackHslComparator(0));

            stacks.addAll(dyeStacks);
        } else if (this.getSelectedTabIndex() == 2) {
            Predicate<ItemStack> isDyedBlock = stack -> stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof DyeBlockStorage;

            List<ItemStack> dyedBlocks = stacks.stream().filter(isDyedBlock).collect(Collectors.toList());

            stacks.removeIf(isDyedBlock);

            dyedBlocks.sort(Comparator.comparingDouble(stack -> {
                float[] hsl = ColorUtil.rgbToHsl(((DyeBlockStorage) ((BlockItem) stack.getItem()).getBlock()).getDyeColorant().getBaseColor());

                return hsl[2];
            }));

            dyedBlocks.sort(Comparator.comparingDouble(stack -> {
                float[] hsl = ColorUtil.rgbToHsl(((DyeBlockStorage) ((BlockItem) stack.getItem()).getBlock()).getDyeColorant().getBaseColor());

                return hsl[1];
            }));

            dyedBlocks.sort(Comparator.comparingDouble(stack -> {
                float[] hsl = ColorUtil.rgbToHsl(((DyeBlockStorage) ((BlockItem) stack.getItem()).getBlock()).getDyeColorant().getBaseColor());

                return hsl[0];
            }));
        }
    }

    @Override
    public ItemStack createIcon() {
        return null;
    }

    public static Comparator<ItemStack> dyeStackHslComparator(int component) {
        return Comparator.comparingDouble(stack -> ColorUtil.rgbToHsl(((DyeItemStorage) stack.getItem()).getDyeColorant().getBaseColor())[component]);
    }
     */

}