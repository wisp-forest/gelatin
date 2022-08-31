package io.wispforest.gelatin.dye_entries.compat.rei;

import io.wispforest.gelatin.common.util.ColorUtil;
import io.wispforest.gelatin.dye_entries.variants.DyeableVariant;
import io.wispforest.gelatin.dye_entries.variants.VanillaItemVariants;
import io.wispforest.gelatin.dye_entries.variants.block.DyeableBlockVariant;
import io.wispforest.gelatin.dye_entries.variants.item.DyeableItemVariant;
import io.wispforest.gelatin.dye_registry.DyeColorant;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import io.wispforest.gelatin.dye_registry.ducks.DyeBlockStorage;
import io.wispforest.gelatin.dye_registry.ducks.DyeItemStorage;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.display.DynamicDisplayGenerator;
import me.shedaniel.rei.api.client.registry.entry.CollapsibleEntryRegistry;
import me.shedaniel.rei.api.client.view.ViewSearchBuilder;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCraftingDisplay;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GelatinREIClientPlugin implements REIClientPlugin {

    @Override
    public void registerCollapsibleEntries(CollapsibleEntryRegistry registry) {
        DyeableBlockVariant.getAllBlockVariants().stream().filter(dyeableBlockVariant -> !dyeableBlockVariant.alwaysReadOnly() && dyeableBlockVariant.createBlockItem()).forEach(dyeableBlockVariant -> {
            List<ItemStack> items = Registry.ITEM.stream().filter(item -> item.getRegistryEntry().isIn(dyeableBlockVariant.blockItemVariant.getPrimaryItemTag())).map(Item::getDefaultStack).collect(Collectors.toList());

            Predicate<ItemStack> getNonVanillaBlocks = stack -> {
                DyeColorant dyeColorant = ((DyeBlockStorage) ((BlockItem) stack.getItem()).getBlock()).getDyeColorant();

                return !DyeColorantRegistry.Constants.VANILLA_DYES.contains(dyeColorant);
            };

            List<ItemStack> nonVanillaStacks = items.stream().filter(getNonVanillaBlocks).collect(Collectors.toList());
            items.removeIf(getNonVanillaBlocks);

            nonVanillaStacks.sort(Comparator.comparingDouble(stack -> {
                float[] hsl = ColorUtil.rgbToHsl(((DyeBlockStorage) ((BlockItem) stack.getItem()).getBlock()).getDyeColorant().getBaseColor());

                return hsl[2];
            }));

            nonVanillaStacks.sort(Comparator.comparingDouble(stack -> {
                float[] hsl = ColorUtil.rgbToHsl(((DyeBlockStorage) ((BlockItem) stack.getItem()).getBlock()).getDyeColorant().getBaseColor());

                return hsl[1];
            }));

            nonVanillaStacks.sort(Comparator.comparingDouble(stack -> {
                float[] hsl = ColorUtil.rgbToHsl(((DyeBlockStorage) ((BlockItem) stack.getItem()).getBlock()).getDyeColorant().getBaseColor());

                return hsl[0];
            }));

            items.addAll(nonVanillaStacks);

            Block defaultBlock = dyeableBlockVariant.getDefaultBlock();

            if(defaultBlock instanceof DyeBlockStorage dyeBlockStorage && dyeBlockStorage.getDyeColorant() != DyeColorantRegistry.WHITE){
                items.add(0, defaultBlock.asItem().getDefaultStack());
            }

            registry.group(dyeableBlockVariant.variantIdentifier, Text.translatable(dyeableBlockVariant.variantIdentifier.getPath() + "_condensed"), items.stream().map(EntryStacks::of).collect(Collectors.toList()));
        });

        DyeableItemVariant.getAllItemVariants().stream().filter(dyeableBlockVariant -> !dyeableBlockVariant.alwaysReadOnly()).forEach(dyeableItemVariant -> {
            List<ItemStack> items = Registry.ITEM.stream().filter(item -> item.getRegistryEntry().isIn(dyeableItemVariant.getPrimaryItemTag())).map(Item::getDefaultStack).collect(Collectors.toList());

            Predicate<ItemStack> getNonVanillaBlocks = stack -> {
                DyeColorant dyeColorant = ((DyeItemStorage) stack.getItem()).getDyeColorant();

                return !DyeColorantRegistry.Constants.VANILLA_DYES.contains(dyeColorant);
            };

            List<ItemStack> nonVanillaStacks = items.stream().filter(getNonVanillaBlocks).collect(Collectors.toList());
            items.removeIf(getNonVanillaBlocks);

            nonVanillaStacks.sort(Comparator.comparingDouble(stack -> {
                float[] hsl = ColorUtil.rgbToHsl(((DyeItemStorage) stack.getItem()).getDyeColorant().getBaseColor());

                return hsl[2];
            }));

            nonVanillaStacks.sort(Comparator.comparingDouble(stack -> {
                float[] hsl = ColorUtil.rgbToHsl(((DyeItemStorage) stack.getItem()).getDyeColorant().getBaseColor());

                return hsl[1];
            }));

            nonVanillaStacks.sort(Comparator.comparingDouble(stack -> {
                float[] hsl = ColorUtil.rgbToHsl(((DyeItemStorage) stack.getItem()).getDyeColorant().getBaseColor());

                return hsl[0];
            }));

            items.addAll(nonVanillaStacks);

            Item defaultItem = dyeableItemVariant.getDefaultItem();

            if(defaultItem instanceof DyeBlockStorage dyeBlockStorage && dyeBlockStorage.getDyeColorant() != DyeColorantRegistry.WHITE){
                items.add(0, defaultItem.getDefaultStack());
            }

            registry.group(dyeableItemVariant.variantIdentifier, Text.translatable(dyeableItemVariant.variantIdentifier.getPath() + "_condensed"), items.stream().map(EntryStacks::of).collect(Collectors.toList()));
        });
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerDisplayGenerator(CategoryIdentifier.of("minecraft", "plugins/crafting"), new DynamicDyeBlockVaraintDisplay());
    }

    private static class DynamicDyeBlockVaraintDisplay implements DynamicDisplayGenerator<DefaultCraftingDisplay<ShapedRecipe>>{
        @Override
        public Optional<List<DefaultCraftingDisplay<ShapedRecipe>>> getRecipeFor(EntryStack<?> entry) {
            if(entry.getValue() instanceof ItemStack itemStack && itemStack.getItem() instanceof BlockItem blockItem){
                DyeableVariant<?, ? extends ItemConvertible> dyeableVariant = DyeableVariant.getVariantFromEntry(blockItem.getBlock());

                if(dyeableVariant instanceof DyeableBlockVariant dyeableBlockVariant && dyeableBlockVariant.createBlockItem() && blockItem.getBlock().getRegistryEntry().isIn(dyeableBlockVariant.getPrimaryBlockTag())){
                    return Optional.of(List.of(DyeBlockVaraintDisplay.recipeForBlock(dyeableBlockVariant, blockItem.getBlock())));
                }
            }

            return DynamicDisplayGenerator.super.getRecipeFor(entry);
        }

        @Override
        public Optional<List<DefaultCraftingDisplay<ShapedRecipe>>> getUsageFor(EntryStack<?> entry) {
            if(entry.getValue() instanceof ItemStack itemStack && itemStack.getItem() instanceof BlockItem blockItem){
                DyeableVariant<?, ? extends ItemConvertible> dyeableVariant = DyeableVariant.getVariantFromEntry(blockItem.getBlock());

                if(dyeableVariant instanceof DyeableBlockVariant dyeableBlockVariant && dyeableBlockVariant.createBlockItem()){
                    return Optional.of(List.of(DyeBlockVaraintDisplay.usageOfBlock(dyeableBlockVariant, blockItem.getBlock())));
                }
            }

            return DynamicDisplayGenerator.super.getUsageFor(entry);
        }

        @Override
        public Optional<List<DefaultCraftingDisplay<ShapedRecipe>>> generate(ViewSearchBuilder builder) {
            //TODO: IMPLEMENT IN THE FUTURE?
            return DynamicDisplayGenerator.super.generate(builder);
        }
    }

    private static class DyeBlockVaraintDisplay extends DefaultCraftingDisplay<ShapedRecipe> {
        public DyeBlockVaraintDisplay(TagKey<Item> inputBlockTag, Item dyeTag, Block outputBlock){
            super(createRecipeInputList(inputBlockTag, dyeTag),
                    Collections.singletonList(EntryIngredients.of(outputBlock)),
                    Optional.empty()
            );
        }

        public DyeBlockVaraintDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs){
            super(inputs, outputs, Optional.empty());
        }

        public static DyeBlockVaraintDisplay recipeForBlock(DyeableBlockVariant variant, Block orginblock){
            DyeColorant dyeColorant = variant.getColorFromEntry(orginblock);

            return new DyeBlockVaraintDisplay(createRecipeInputList(variant.blockItemVariant.getPrimaryItemTag(), VanillaItemVariants.DYE.getColoredEntry(dyeColorant)), Collections.singletonList(EntryIngredients.of(orginblock)));
        }

        public static DyeBlockVaraintDisplay usageOfBlock(DyeableBlockVariant variant, Block orginblock){
            List<ItemConvertible> dyeItems = new ArrayList<>();
            List<ItemConvertible> outputBlock = new ArrayList<>();

            for(DyeColorant dyeColorant : DyeColorantRegistry.DYE_COLOR){
                dyeItems.add(VanillaItemVariants.DYE.getColoredEntry(dyeColorant));
                outputBlock.add(variant.blockItemVariant.getColoredEntry(dyeColorant));
            }

            return new DyeBlockVaraintDisplay(createUsageInputList(orginblock, EntryIngredients.ofItems(dyeItems)), Collections.singletonList(EntryIngredients.ofItems(outputBlock)));
        }

        @Override
        public int getWidth() {
            return 3;
        }

        @Override
        public int getHeight() {
            return 3;
        }

        private static List<EntryIngredient> createRecipeInputList(TagKey<Item> blockTag, Item dyeItem){
            List<EntryIngredient> inputs = new ArrayList<>(Collections.nCopies(8, EntryIngredients.ofItemTag(blockTag)));
            inputs.add(4, EntryIngredients.of(dyeItem));

            return inputs;
        }

        private static List<EntryIngredient> createUsageInputList(Block usageBlock, EntryIngredient dyeItems){
            List<EntryIngredient> inputs = new ArrayList<>(Collections.nCopies(8, EntryIngredients.of(usageBlock)));
            inputs.add(4, dyeItems);

            return inputs;
        }
    }
}
