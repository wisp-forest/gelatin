package io.wispforest.jello.main.client.render.screen;

import io.wispforest.jello.api.dye.registry.variants.DyedVariantContainer;
import io.wispforest.jello.main.common.blocks.JelloBlockRegistry;
import io.wispforest.jello.main.common.items.ItemRegistry;
import io.wispforest.owo.client.screens.ScreenUtils;
import io.wispforest.owo.client.screens.ValidatingSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.*;
import net.minecraft.util.collection.DefaultedList;

import java.util.*;

public class ColorMixerScreenHandler extends ScreenHandler {
    public static final List<ItemStack> ALL_DYE_ITEMS = new ArrayList<>();

    static{
        DyedVariantContainer.getVariantMap().forEach((dyeColorant, dyedVariantContainer) -> {
            ALL_DYE_ITEMS.add(dyedVariantContainer.dyeItem.getDefaultStack());
        });
    }

    public final Inventory DYE_INVENTORY;
    public final Inventory ARTIST_INVENTORY;

    private final ScreenHandlerContext context;

    public final DefaultedList<ItemStack> itemList = DefaultedList.of();

    public ColorMixerScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
    }

    public ColorMixerScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(JelloScreenHandlerRegistry.COLOR_MIXER_TYPE, syncId);

        this.DYE_INVENTORY = new SimpleInventory(30);
        this.ARTIST_INVENTORY = new SimpleInventory(1);

        this.context = context;

        this.addSlot(new ValidatingSlot(ARTIST_INVENTORY, 0, 14, 96, itemStack -> itemStack.isOf(ItemRegistry.MainItemRegistry.ARTIST_PALETTE) || itemStack.isOf(ItemRegistry.MainItemRegistry.EMPTY_ARTIST_PALETTE)));

        for (int row = 0; row < 5; ++row) {
            for (int col = 0; col < 6; ++col) {
                this.addSlot(new ValidatingSlot(DYE_INVENTORY, col + row * 3, 44 + col * 18, 24 + row * 18, itemStack -> false));
            }
        }

        ScreenUtils.generatePlayerSlots(8, 122, playerInventory, this::addSlot);

        this.scrollItems(0.0F);
    }

    public void scrollItems(float position) {
        int i = (itemList.size() + 6 - 1) / 6 - 5;
        int j = (int)((double)(position * (float)i) + 0.5);
        if (j < 0) {
            j = 0;
        }

        for(int k = 0; k < 5; ++k) {
            for(int l = 0; l < 6; ++l) {
                int m = l + (k + j) * 6;
                if (m >= 0 && m < itemList.size()) {
                    DYE_INVENTORY.setStack(l + k * 6, itemList.get(m));
                } else {
                    DYE_INVENTORY.setStack(l + k * 6, ItemStack.EMPTY);
                }
            }
        }
    }

    public void search(String text) {
        this.itemList.clear();
        if (text.isBlank()) {
            this.itemList.addAll(ColorMixerScreenHandler.ALL_DYE_ITEMS);
        } else {
            List<ItemStack> searched = new ArrayList<>();

            for(ItemStack itemStack : ColorMixerScreenHandler.ALL_DYE_ITEMS){
                String itemName = itemStack.getItem().getRegistryEntry().getKey().get().getValue()
                        .getNamespace().replace("_", " ").toLowerCase(Locale.ROOT);

                if(itemName.contains(text)){
                    searched.add(itemStack);
                }
            }

            this.itemList.addAll(searched);
        }

        this.scrollItems(0.0F);
    }

    public boolean shouldShowScrollbar() {
        return this.itemList.size() > 45;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return canUse(context, player, JelloBlockRegistry.PAINT_MIXER);
    }

    // Shift + Player Inv Slot
    @Override
    public ItemStack transferSlot(PlayerEntity player, int invSlot) {
        return ScreenUtils.handleSlotTransfer(this, invSlot, 1);
    }

//    @Override
//    public ItemStack getCursorStack() {
//        return this.parent.getCursorStack();
//    }
//
//    @Override
//    public void setCursorStack(ItemStack stack) {
//        this.parent.setCursorStack(stack);
//    }
}
