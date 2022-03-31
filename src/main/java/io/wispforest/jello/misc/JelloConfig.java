package io.wispforest.jello.misc;

import io.wispforest.jello.Jello;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = Jello.MODID)
public class JelloConfig implements ConfigData {

    @ConfigEntry.Category("common")
    @ConfigEntry.Gui.Tooltip
    public boolean enableGrayScalingOfEntities = false;

    @ConfigEntry.Category("common")
    @ConfigEntry.Gui.Tooltip
    public boolean enableDyeingEntities = true;

    @ConfigEntry.Category("common")
    @ConfigEntry.Gui.Tooltip
    public boolean enableDyeingPlayers = true;

    @ConfigEntry.Category("common")
    @ConfigEntry.Gui.Tooltip
    public boolean enableDyeingBlocks = true;
}



