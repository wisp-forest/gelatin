package io.wispforest.common.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.wispforest.common.compat.GelatinConfig;
import me.shedaniel.autoconfig.AutoConfig;

public class GelatinModMenuIntegration implements ModMenuApi {

    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> AutoConfig.getConfigScreen(GelatinConfig.class, parent).get();
    }

}
