package io.wispforest.jello.api.dye.registry.variants;

import io.wispforest.jello.Jello;
import io.wispforest.jello.api.dye.registry.variants.item.DyeableItemVariant;
import io.wispforest.jello.item.JelloDyeItem;
import io.wispforest.owo.itemgroup.OwoItemSettings;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import java.util.List;

public class VanillaItemVariants {

    private static final Item.Settings itemSettings = new OwoItemSettings()
            .group(Jello.MAIN_ITEM_GROUP)
            .tab(1);

    public static final DyeableItemVariant DYE = DyeableItemVariant.Builder.of(new Identifier("dye"), itemSettings, (dyeColorant, parentEntry, settings) -> new JelloDyeItem(dyeColorant, itemSettings))
            .register();

    public static final List<DyeableItemVariant> VANILLA_VARIANTS =
            List.of(DYE);
}
