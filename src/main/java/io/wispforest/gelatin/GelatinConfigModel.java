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
    public boolean enableDyeingEntities = GelatinDefaultConfig.INSTANCE.entityDyeing();

    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public boolean enableDyeingPlayers = GelatinDefaultConfig.INSTANCE.playerDyeing();

    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public boolean enableDyeingBlocks = GelatinDefaultConfig.INSTANCE.blockDyeing();

    @SectionHeader("client")
    @Hook
    public boolean enableTransparencyFixCauldrons = GelatinDefaultConfig.INSTANCE.cauldronFix();

    public boolean enableGrayScalingOfEntities = GelatinDefaultConfig.INSTANCE.grayScalingOfEntity();

    @Nest
    @Expanded
    public DyeingControls dyeingControls = new DyeingControls();

    public static class DyeingControls {
        @Hook
        @RestartRequired
        public boolean useSeparateKeybinding = true;

        @Hook
        public boolean useToggleMode = true;

        @Hook
        public boolean alwaysOnByDefault = true;
    }

    @Hook
    public List<String> gelatinBlackListModid = GelatinDefaultConfig.INSTANCE.getGelatinBlacklist();
}



