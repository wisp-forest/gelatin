package io.wispforest.jello.compat.rei;

import io.wispforest.jello.api.ducks.DyeBlockStorage;
import io.wispforest.jello.api.ducks.DyeItemStorage;
import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.jello.api.dye.registry.variants.block.DyeableBlockVariant;
import io.wispforest.jello.api.dye.registry.variants.item.DyeableItemVariant;
import io.wispforest.jello.api.util.ColorUtil;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.CollapsibleEntryRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class JelloREIClientPlugin implements REIClientPlugin {

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
}
