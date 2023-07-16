package io.wispforest.gelatin.dye_entries.compat.owo;

import io.wispforest.gelatin.dye_entries.misc.DyeEntriesItemGroups;
import io.wispforest.owo.itemgroup.OwoItemSettings;

public class OwoCompat {

    public static void init(){
        DyeEntriesItemGroups.createSeparateGroups = true;
        DyeEntriesItemGroups.itemGroupInit = GelatinItemGroup.MAIN_ITEM_GROUP::initialize;
        DyeEntriesItemGroups.getItemGroup = i -> GelatinItemGroup.MAIN_ITEM_GROUP;
        DyeEntriesItemGroups.getItemSettings = i -> {
            return new OwoItemSettings()
                    .group(GelatinItemGroup.MAIN_ITEM_GROUP)
                    .tab(i);
        };
    }
}
