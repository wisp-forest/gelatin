package io.wispforest.gelatin.dye_entries.variants.impl;

import io.wispforest.gelatin.dye_entries.item.GelatinDyeItem;
import io.wispforest.gelatin.dye_entries.misc.DyeEntriesItemGroups;
import io.wispforest.gelatin.dye_entries.variants.item.DyeableItemVariant;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import java.util.List;

public class VanillaItemVariants {

    private static final Item.Settings itemSettings = DyeEntriesItemGroups.getItemSettings.apply(0);

    public static final DyeableItemVariant DYE = new DyeableItemVariant(new Identifier("dye"), itemSettings, (dyeColorant, parentEntry, settings) -> new GelatinDyeItem(dyeColorant, itemSettings))
            .register();

    public static final List<DyeableItemVariant> VANILLA_VARIANTS =
            List.of(DYE);
}
