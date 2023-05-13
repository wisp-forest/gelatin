package io.wispforest.jello.compat;

import io.wispforest.jello.Jello;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.*;

@Modmenu(modId = Jello.MODID)
@Config(name = Jello.MODID, wrapperName = "JelloConfig")
public class JelloConfigModel {

    @SectionHeader("common")
    @RestartRequired
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public boolean addCustomJsonColors = true;

    @SectionHeader("common")
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public boolean allowVanillaColorsInPaintMixer = true;

    @SectionHeader("client")
    public HudPosition bundlePosition = new HudPosition(-1, -1);

    public record HudPosition(int x, int y) {}
}
