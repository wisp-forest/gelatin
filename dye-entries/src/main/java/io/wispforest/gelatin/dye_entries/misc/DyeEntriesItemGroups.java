package io.wispforest.gelatin.dye_entries.misc;

import io.wispforest.gelatin.common.misc.GelatinConstants;
import io.wispforest.gelatin.dye_entries.compat.owo.OwoCompat;
import io.wispforest.gelatin.dye_entries.utils.DyeSortUtil;
import io.wispforest.gelatin.dye_entries.variants.DyeableVariantRegistry;
import io.wispforest.gelatin.dye_entries.variants.block.DyeableBlockVariant;
import io.wispforest.gelatin.dye_entries.variants.item.DyeableItemVariant;
import io.wispforest.gelatin.dye_registry.DyeColorant;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import net.fabricmc.fabric.impl.item.group.ItemGroupExtensions;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.*;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DyeEntriesItemGroups {

    //public static TriFunction<ItemGroup, List<ItemStack>, Integer, Boolean> getEntries = DyeEntriesItemGroups::getEntries;

    public static Function<Integer, ItemGroup> getItemGroup = DyeEntriesItemGroups::getGroup;

    public static Function<Integer, Item.Settings> getItemSettings = tab -> {
        return new Item.Settings()
                .group(getItemGroup.apply(tab));
    };

    public static Runnable itemGroupInit = () -> {};

    public static boolean createSeparateGroups = true;

    static {
        if(FabricLoader.getInstance().isModLoaded("owo")) OwoCompat.init();
    }

    public static ItemGroup DYE_ITEMS;
    public static ItemGroup DYE_BLOCKS;

    public static void init() {
        if(createSeparateGroups){
            ((ItemGroupExtensions) ItemGroup.BUILDING_BLOCKS).fabric_expandArray();

            Identifier dyeItemsId = GelatinConstants.id("dye_items");

            DYE_ITEMS = new ItemGroup(ItemGroup.GROUPS.length - 1, String.format("%s.%s", dyeItemsId.getNamespace(), dyeItemsId.getPath())) {
                @Override
                public ItemStack createIcon() {
                    return getIconItems().getLeft().getDefaultStack();
                }

                @Override
                public void appendStacks(DefaultedList<ItemStack> stacks) {
                    super.appendStacks(stacks);

                    ItemGroup group = getItemGroup.apply(0);

                    getEntries(group, stacks, 0);
                }

                @Override
                public Text getDisplayName() {
                    return Text.translatable("itemGroup.gelatin.gelatin_group.tab.dyed_item_variants");
                }
            };

            //---------------------------------------------------------------------

            ((ItemGroupExtensions) ItemGroup.BUILDING_BLOCKS).fabric_expandArray();

            Identifier dyeBlocksId = GelatinConstants.id("dye_blocks");

            DYE_BLOCKS = new ItemGroup(ItemGroup.GROUPS.length - 1, String.format("%s.%s", dyeBlocksId.getNamespace(), dyeBlocksId.getPath())) {
                @Override
                public ItemStack createIcon() {
                    return getIconItems().getRight().getDefaultStack();
                }

                @Override
                public void appendStacks(DefaultedList<ItemStack> stacks) {
                    super.appendStacks(stacks);

                    ItemGroup group = getItemGroup.apply(1);

                    getEntries(group, stacks, 1);
                }

                @Override
                public Text getDisplayName() {
                    return Text.translatable("itemGroup.gelatin.gelatin_group.tab.dyed_block_variants");
                }
            };
        }
    }

    public static Pair<Item, Item> getIconItems(){
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

        return new Pair<>(dyeItem, blockItem);
    }

    public static Boolean getEntries(ItemGroup group, List<ItemStack> stacks, int tab){
        List<ItemStack> sortableEntries = new ArrayList<>();

        Set<DyeableItemVariant> getVariantSet = (tab == 0)
                ? DyeableVariantRegistry.getAllItemVariants()
                : DyeableVariantRegistry.getAllBlockItemVariants();

        for (DyeableItemVariant variant : getVariantSet) {
            for(DyeColorant dyeColorant : DyeColorantRegistry.getAllColorants()){
                sortableEntries.add(variant.getColoredEntry(dyeColorant).getDefaultStack());
            }
        }

        sortEntries(sortableEntries, tab);

        stacks.addAll(sortableEntries);

        return true;
    }

    public static void sortEntries(Collection<ItemStack> stacks, int tabIndex) {
        Function<ItemStack, DyeColorant> colorantFunc;
        Predicate<ItemStack> isDyedVariant;

        if (tabIndex == 0) {
            isDyedVariant = stack -> stack.getItem().isDyed();
            colorantFunc = stack -> stack.getItem().getDyeColorant();
        } else if (tabIndex == 1) {
            isDyedVariant = stack -> stack.getItem() instanceof BlockItem blockItem && blockItem.isDyed();
            colorantFunc = stack -> ((BlockItem) stack.getItem()).getBlock().getDyeColorant();
        } else {
            return;
        }

        List<ItemStack> dyeStacks = stacks.stream().filter(isDyedVariant).collect(Collectors.toList());

        stacks.removeIf(isDyedVariant);

        DyeSortUtil.sortColoredStacks(dyeStacks, colorantFunc);

        stacks.addAll(dyeStacks);
    }

    public static ItemGroup getGroup(int tab){
        if(tab == 0){
            return DYE_ITEMS;
        } else {
            return DYE_BLOCKS;
        }
    }
}
