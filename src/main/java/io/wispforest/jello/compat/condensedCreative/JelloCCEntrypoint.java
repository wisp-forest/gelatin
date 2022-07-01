package io.wispforest.jello.compat.condensedCreative;

import io.wispforest.condensed_creative.registry.CondensedCreativeInitializer;
import io.wispforest.condensed_creative.registry.CondensedEntryRegistry;
import io.wispforest.jello.Jello;
import io.wispforest.jello.api.ducks.DyeBlockStorage;
import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.jello.api.dye.registry.variants.block.DyeableBlockVariant;
import io.wispforest.jello.api.util.ColorUtil;
import io.wispforest.jello.block.JelloBlocks;
import io.wispforest.jello.misc.dye.JelloBlockVariants;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class JelloCCEntrypoint implements CondensedCreativeInitializer {

    @Override
    public void onInitializeCondensedEntries(boolean refreshed) {
        DyeableBlockVariant.getAllBlockVariants().stream().filter(dyeableBlockVariant -> !dyeableBlockVariant.alwaysReadOnly() && dyeableBlockVariant.createBlockItem()).forEach(dyeableBlockVariant -> {
            CondensedEntryRegistry.fromBlockTag(dyeableBlockVariant.variantIdentifier, dyeableBlockVariant.getDefaultBlock(), dyeableBlockVariant.getPrimaryBlockTag())
                .setTitleString(Text.translatable(dyeableBlockVariant.variantIdentifier.getPath() + "_condensed"))
                .setEntrySorting(allStacks -> {
                    Predicate<ItemStack> getNonVanillaBlocks = stack -> {
                        DyeColorant dyeColorant = ((DyeBlockStorage) ((BlockItem) stack.getItem()).getBlock()).getDyeColor();
                        
                        return !DyeColorantRegistry.Constants.VANILLA_DYES.contains(dyeColorant);
                    };
                    
                    List<ItemStack> nonVanillaStacks = allStacks.stream().filter(getNonVanillaBlocks).collect(Collectors.toList());
                    allStacks.removeIf(getNonVanillaBlocks);

                    nonVanillaStacks.sort(Comparator.comparingDouble(stack -> {
                        float[] hsl = ColorUtil.rgbToHsl(((DyeBlockStorage) ((BlockItem) stack.getItem()).getBlock()).getDyeColor().getBaseColor());

                        return hsl[2];
                    }));

                    nonVanillaStacks.sort(Comparator.comparingDouble(stack -> {
                        float[] hsl = ColorUtil.rgbToHsl(((DyeBlockStorage) ((BlockItem) stack.getItem()).getBlock()).getDyeColor().getBaseColor());

                        return hsl[1];
                    }));

                    nonVanillaStacks.sort(Comparator.comparingDouble(stack -> {
                        float[] hsl = ColorUtil.rgbToHsl(((DyeBlockStorage) ((BlockItem) stack.getItem()).getBlock()).getDyeColor().getBaseColor());

                        return hsl[0];
                    }));
                    
                    allStacks.addAll(nonVanillaStacks);

                    Block defaultBlock = dyeableBlockVariant.getDefaultBlock();

                    Identifier blockId = Registry.BLOCK.getId(defaultBlock);

                    if(!blockId.getPath().contains(DyeColorantRegistry.WHITE.getName())){
                        allStacks.add(0, dyeableBlockVariant.getDefaultBlock().asItem().getDefaultStack());
                    }
                })
                .addItemGroup(Jello.MAIN_ITEM_GROUP, 2);
        });

        //------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

        List<Item> slimeSlabs = DyeColorantRegistry.Constants.VANILLA_DYES.stream().map(dyeColorant -> JelloBlockVariants.SLIME_SLAB.getColoredBlock(dyeColorant).asItem()).collect(Collectors.toList());

        slimeSlabs.add(0, JelloBlocks.SLIME_SLAB.asItem());

        CondensedEntryRegistry.fromItems(Jello.id("vanilla_slime_slabs"), JelloBlocks.SLIME_SLAB, slimeSlabs)
                .setTitleString(Text.translatable("vanilla_slime_slabs_condensed"))
                .setExtraInfoText(Text.translatable("tooltip.vanilla_slime_slabs_condensed").formatted(Formatting.GRAY))
                .addItemGroup(ItemGroup.REDSTONE);

        //--------------------

        List<Item> slimeBlocks = DyeColorantRegistry.Constants.VANILLA_DYES.stream().map(dyeColorant -> JelloBlockVariants.SLIME_BLOCK.getColoredBlock(dyeColorant).asItem()).collect(Collectors.toList());

        slimeBlocks.add(0, Blocks.SLIME_BLOCK.asItem());

        CondensedEntryRegistry.fromItems(Jello.id("vanilla_slime_blocks"), Blocks.SLIME_BLOCK, slimeBlocks)
                .setTitleString(Text.translatable("vanilla_slime_blocks_condensed"))
                .setExtraInfoText(Text.translatable("tooltip.vanilla_slime_blocks_condensed").formatted(Formatting.GRAY))
                .addItemGroup(ItemGroup.REDSTONE);

    }
}
