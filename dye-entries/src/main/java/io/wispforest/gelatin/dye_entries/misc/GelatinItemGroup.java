package io.wispforest.gelatin.dye_entries.misc;

import io.wispforest.gelatin.common.util.ColorUtil;
import io.wispforest.gelatin.dye_entries.variants.DyeableVariantRegistry;
import io.wispforest.gelatin.dye_registry.DyeColorant;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import io.wispforest.gelatin.dye_registry.ducks.DyeBlockStorage;
import io.wispforest.gelatin.dye_registry.ducks.DyeItemStorage;
import io.wispforest.gelatin.dye_entries.variants.block.DyeableBlockVariant;
import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.util.pond.OwoItemExtensions;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class GelatinItemGroup extends OwoItemGroup {

    protected GelatinItemGroup(Identifier id, Consumer<OwoItemGroup> initializer, Supplier<Icon> iconSupplier, int tabStackHeight, int buttonStackHeight, @Nullable Identifier customTexture, boolean useDynamicTitle, boolean displaySingleTab) {
        super(id, initializer, iconSupplier, tabStackHeight, buttonStackHeight, customTexture, useDynamicTitle, displaySingleTab);
    }

    public static OwoItemGroup create(Identifier id){
        return OwoItemGroup.builder(id, () -> Icon.of(Items.ORANGE_DYE.getDefaultStack()))
                .initializer(group -> {
                    //this.addTab(Icon.of(JelloItems.DYE_BUNDLE.getDefaultStack()), "jello_tools", null, ItemGroupTab.DEFAULT_TEXTURE);

                    if(DyeColorantRegistry.DYE_COLOR.size() > 17) {
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

                        group.addCustomTab(Icon.of(dyeItem), "dyed_item_variants", (context, entries) -> {
                            getItemEntries(group, entries, true);
                        }, true);
                        group.addCustomTab(Icon.of(blockItem), "dyed_block_variants", (context, entries) -> {
                            getItemEntries(group, entries, true);
                        }, false);
                    }
                }).build();
    }

    public static void getItemEntries(OwoItemGroup group, ItemGroup.Entries entries, boolean matchTab){
        ItemGroup.EntriesImpl sortableEntries = new ItemGroup.EntriesImpl(group, FeatureSet.empty());

        Registries.ITEM.stream()
                .filter(item -> ((OwoItemExtensions) item).owo$group() == group && (!matchTab || ((OwoItemExtensions) item).owo$tab() == group.getSelectedTabIndex()))
                .forEach(item -> ((OwoItemExtensions) item).owo$stackGenerator().accept(item, sortableEntries));

        sortEntries(sortableEntries.parentTabStacks, group.getSelectedTabIndex());
        //sortEntries(sortableEntries.searchTabStacks, group.getSelectedTabIndex());

        entries.addAll(sortableEntries.parentTabStacks, StackVisibility.PARENT_TAB_ONLY);
        //entries.addAll(sortableEntries.searchTabStacks, StackVisibility.SEARCH_TAB_ONLY);
    }

    public static void sortEntries(Collection<ItemStack> stacks, int tabIndex) {
        Function<ItemStack, DyeColorant> colorantFunc;
        Predicate<ItemStack> isDyedVariant;

        if (tabIndex == 0) {
            isDyedVariant = stack -> stack.getItem() instanceof DyeItemStorage;
            colorantFunc = stack -> stack.getItem().getDyeColorant();
        } else if (tabIndex == 1) {
            isDyedVariant = stack -> stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof DyeBlockStorage;
            colorantFunc = stack -> ((DyeBlockStorage) ((BlockItem) stack.getItem()).getBlock()).getDyeColorant();
        } else {
            return;
        }

        List<ItemStack> dyeStacks = stacks.stream().filter(isDyedVariant).collect(Collectors.toList());

        stacks.removeIf(isDyedVariant);

        dyeStacks.sort(dyeStackHslComparator(2, colorantFunc));
        dyeStacks.sort(dyeStackHslComparator(1, colorantFunc));
        dyeStacks.sort(dyeStackHslComparator(0, colorantFunc));

        stacks.addAll(dyeStacks);
    }

    public static Comparator<ItemStack> dyeStackHslComparator(int component, Function<ItemStack, DyeColorant> colorantFunc) {
        return Comparator.comparingDouble(stack -> ColorUtil.rgbToHsl(colorantFunc.apply(stack).getBaseColor())[component]);
    }

}