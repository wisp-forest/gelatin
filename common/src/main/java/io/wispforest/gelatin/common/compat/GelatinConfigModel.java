package io.wispforest.gelatin.common.compat;

import io.wispforest.gelatin.common.misc.GelatinConstants;
import io.wispforest.owo.config.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Modmenu(modId = GelatinConstants.MODID)
@Config(name = GelatinConstants.MODID, wrapperName = "GelatinConfig")
public class GelatinConfigModel {

    @SectionHeader("common")
    public boolean enableDyeingEntities = true;

    public boolean enableDyeingPlayers = true;

    public boolean enableDyeingBlocks = true;

    @SectionHeader("client")
    @Hook
    public boolean enableTransparencyFixCauldrons = true;

    public boolean enableGrayScalingOfEntities = false;

    public boolean enableGrayScaleRainbowEntities = false;

    @Hook
    public List<String> gelatinBlackListModid = new ArrayList<>();
}



