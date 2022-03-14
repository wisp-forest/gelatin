package io.wispforest.jello.api.dye.item;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

public class DyedItemVariants {

    public static final List<DyeItem> ALL_DEFAULT_DYES = new ArrayList<>();

    public static DyeItem createDyeColorant(Identifier id, DyeColorant dyeColorant, Item.Settings settings){
        DyeItem dyeItem = (DyeItem) register(id, new DyeItem(dyeColorant, settings));
        ALL_DEFAULT_DYES.add(dyeItem);

        return dyeItem;
    }

    private static Item register(Identifier id, Item item){
        return Registry.register(Registry.ITEM, id, item);
    }
}
