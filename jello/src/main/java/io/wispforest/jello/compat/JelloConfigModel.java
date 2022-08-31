package io.wispforest.jello.compat;

import io.wispforest.jello.Jello;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.*;

@Modmenu(modId = Jello.MODID)
@Config(name = Jello.MODID, wrapperName = "JelloConfig")
public class JelloConfigModel {

    @SectionHeader("client")
    @RestartRequired
    @Sync(Option.SyncMode.OVERRIDE_CLIENT)
    public boolean addCustomJsonColors = true;
}
