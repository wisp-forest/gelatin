package io.wispforest.gelatin.common.compat.owo;

import io.wispforest.gelatin.common.util.ItemFunctions;
import io.wispforest.owo.itemgroup.OwoItemSettings;

public class OwoCompatItemSettings {

    public static void init(){
        ItemFunctions.itemSettingsConstructor = OwoItemSettings::new;

        ItemFunctions.copyMethods.add((settingsNew, settings) -> {
            if(!(settings instanceof OwoItemSettings oldOwoItemSettings)) return;

            ((OwoItemSettings) settingsNew).tab(oldOwoItemSettings.tab());
        });
    }
}
