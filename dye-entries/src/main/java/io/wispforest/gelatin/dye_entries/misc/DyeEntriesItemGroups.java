package io.wispforest.gelatin.dye_entries.misc;

import io.wispforest.gelatin.common.misc.GelatinConstants;
import io.wispforest.gelatin.dye_entries.DyeEntriesInit;
import io.wispforest.gelatin.dye_entries.compat.owo.OwoCompat;
import io.wispforest.gelatin.dye_entries.utils.DyeSortUtil;
import io.wispforest.gelatin.dye_entries.variants.DyeableVariantRegistry;
import io.wispforest.gelatin.dye_entries.variants.block.DyeableBlockVariant;
import io.wispforest.gelatin.dye_entries.variants.item.DyeableItemVariant;
import io.wispforest.gelatin.dye_registry.DyeColorant;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import io.wispforest.gelatin.dye_registry.ducks.DyeBlockStorage;
import io.wispforest.gelatin.dye_registry.ducks.DyeItemStorage;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.apache.commons.lang3.function.TriFunction;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DyeEntriesItemGroups {

    public static TriFunction<ItemGroup, ItemGroup.Entries, Integer, Boolean> getEntries = DyeEntriesItemGroups::getEntries;

    public static Function<Integer, ItemGroup> getItemGroup = DyeEntriesItemGroups::getGroup;

    public static Function<Integer, Item.Settings> getItemSettings = tab -> {
        return new Item.Settings();
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
            DYE_ITEMS = FabricItemGroup.builder()
                    .entries((displayContext, entries) -> {
                        ItemGroup group = getItemGroup.apply(0);

                        getEntries.apply(group, entries, 0);
                    })
                    .icon(() -> getIconItems().getLeft().getDefaultStack())
                    .displayName(Text.translatable("itemGroup.gelatin.gelatin_group.tab.dyed_item_variants"))
                    .build();

            Registry.register(Registries.ITEM_GROUP, GelatinConstants.id("dye_items"), DYE_ITEMS);

            DYE_BLOCKS = FabricItemGroup.builder()
                    .entries((displayContext, entries) -> {
                        ItemGroup group = getItemGroup.apply(1);

                        getEntries.apply(group, entries, 1);
                    })
                    .icon(() -> getIconItems().getRight().getDefaultStack())
                    .displayName(Text.translatable("itemGroup.gelatin.gelatin_group.tab.dyed_block_variants"))
                    .build();

            Registry.register(Registries.ITEM_GROUP, GelatinConstants.id("dye_blocks"), DYE_BLOCKS);
        }
    }

    public static Pair<Item, Item> getIconItems(){
        DyeColorant color = DyeColorantRegistry.DYE_COLOR.get(new Identifier("jello","international_klein_blue"));//"cold_turkey"

        List<DyeableBlockVariant> allVariants = DyeableVariantRegistry.getAllBlockVariants().stream().filter(dyeableBlockVariant -> !dyeableBlockVariant.vanillaDyesOnly() && dyeableBlockVariant.createBlockItem()).toList();

        DyeableBlockVariant variant = allVariants.get(new Random().nextInt(allVariants.size()));

        Item dyeItem = null, blockItem = null;

        for(boolean validItems = false; !validItems;){
            dyeItem = Registries.ITEM.get(new Identifier(color.getId().getNamespace(), color.getName() + "_dye"));
            blockItem = Registries.ITEM.get(new Identifier(color.getId().getNamespace(), variant.getColoredEntryPath(color)));

            if(color == DyeColorantRegistry.NULL_VALUE_NEW || blockItem == Items.AIR || dyeItem == Items.AIR) {
                color = DyeColorantRegistry.getRandomColorant();
            } else {
                validItems = true;
            }
        }

        return new Pair<>(dyeItem, blockItem);
    }

    public static Boolean getEntries(ItemGroup group, ItemGroup.Entries entries, int tab){
        ItemGroup.EntriesImpl sortableEntries = new ItemGroup.EntriesImpl(group, FeatureSet.empty());

        Set<DyeableItemVariant> getVariantSet = (tab == 0)
                ? DyeableVariantRegistry.getAllItemVariants()
                : DyeableVariantRegistry.getAllBlockItemVariants();

        for (DyeableItemVariant variant : getVariantSet) {
            for(DyeColorant dyeColorant : DyeColorantRegistry.getAllColorants()){
                sortableEntries.add(variant.getColoredEntry(dyeColorant).getDefaultStack());
            }
        }

        sortEntries(sortableEntries.parentTabStacks, tab);

        entries.addAll(sortableEntries.parentTabStacks, ItemGroup.StackVisibility.PARENT_AND_SEARCH_TABS);

        return true;
    }

    public static void sortEntries(Collection<ItemStack> stacks, int tabIndex) {
        Function<ItemStack, DyeColorant> colorantFunc;
        Predicate<ItemStack> isDyedVariant;

        if (tabIndex == 0) {
            isDyedVariant = stack -> stack.getItem() instanceof DyeItemStorage;
            colorantFunc = stack -> stack.getItem().getDyeColorant();
        } else if (tabIndex == 1) {
            isDyedVariant = stack -> stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof DyeBlockStorage;
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
