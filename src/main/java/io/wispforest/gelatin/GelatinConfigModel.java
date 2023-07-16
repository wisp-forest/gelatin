package io.wispforest.gelatin;

import io.wispforest.gelatin.common.compat.GelatinDefaultConfig;
import io.wispforest.gelatin.common.misc.GelatinConstants;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.*;

import java.util.List;

@Modmenu(modId = GelatinConstants.MODID)
@Config(name = GelatinConstants.MODID, wrapperName = "GelatinConfig")
public class GelatinConfigModel {

    @SectionHeader("common")
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public boolean enableDyeingEntities = GelatinDefaultConfig.INSTANCE.isEntityDyeingEnabled();

    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public boolean enableDyeingPlayers = GelatinDefaultConfig.INSTANCE.isPlayerDyeingEnabled();

    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public boolean enableDyeingBlocks = GelatinDefaultConfig.INSTANCE.isBlockDyeingEnabled();

    @SectionHeader("client")
    @Hook
    public boolean enableTransparencyFixCauldrons = GelatinDefaultConfig.INSTANCE.isCauldronFixEnabled();

    public boolean enableGrayScalingOfEntities = GelatinDefaultConfig.INSTANCE.isGrayScalingOfEntityEnabled();

    public boolean enableGrayScalingOfRainbowEntities = GelatinDefaultConfig.INSTANCE.isGrayScalingOfRainbowEntityEnabled();

    @Hook
    public List<String> gelatinBlackListModid = GelatinDefaultConfig.INSTANCE.getGelatinBlacklist();
}



