package io.wispforest.jello.misc;

import io.wispforest.jello.Jello;
import io.wispforest.jello.api.ducks.DyeBlockStorage;
import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.jello.api.dye.registry.variants.block.DyeableBlockVariant;
import io.wispforest.jello.api.util.ColorUtil;
import io.wispforest.jello.item.JelloDyeItem;
import io.wispforest.jello.item.JelloItems;
import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.itemgroup.gui.ItemGroupTab;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class JelloItemGroup extends OwoItemGroup {

    public JelloItemGroup(Identifier id) {
        super(id);
    }

    @Override
    public ItemStack getIcon() {
        return JelloItems.ARTIST_PALETTE.getDefaultStack();
    }

    @Override
    protected void setup() {
        this.addTab(Icon.of(JelloItems.DYE_BUNDLE.getDefaultStack()), "jello_tools", null, ItemGroupTab.DEFAULT_TEXTURE);

        if(DyeColorantRegistry.DYE_COLOR.size() > 17) {
            DyeColorant color = DyeColorantRegistry.DYE_COLOR.get(new Identifier(Jello.MODID, "cold_turkey"));

            List<DyeableBlockVariant> allVariants = DyeableBlockVariant.getAllBlockVariants().stream().filter(dyeableBlockVariant -> !dyeableBlockVariant.vanillaDyesOnly()).toList();

            DyeableBlockVariant variant = allVariants.get(new Random().nextInt(allVariants.size()));

            Item dyeItem = null, blockItem = null;

            for(boolean validItems = false; !validItems;){
                dyeItem = Registry.ITEM.get(new Identifier(color.getId().getNamespace(), color.getName() + "_dye"));
                blockItem = Registry.ITEM.get(new Identifier(color.getId().getNamespace(), variant.getColoredBlockPath(color)));

                if(blockItem == Items.AIR || dyeItem == Items.AIR) {
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
            Predicate<ItemStack> isDyeItem = stack -> stack.getItem() instanceof JelloDyeItem;

            List<ItemStack> dyeStacks = stacks.stream().filter(isDyeItem).collect(Collectors.toList());

            stacks.removeIf(isDyeItem);

            dyeStacks.sort(ColorUtil.dyeStackHslComparator(2));
            dyeStacks.sort(ColorUtil.dyeStackHslComparator(1));
            dyeStacks.sort(ColorUtil.dyeStackHslComparator(0));

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
}
