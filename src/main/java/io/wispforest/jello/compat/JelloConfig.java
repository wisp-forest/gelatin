package io.wispforest.jello.compat;

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

    @ConfigEntry.Category("Client")
    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.Gui.RequiresRestart
    public boolean addCustomJsonColors = true;

    @ConfigEntry.Category("Client")
    @ConfigEntry.Gui.Tooltip
    public boolean enableTransparencyFixCauldrons = true;

}



