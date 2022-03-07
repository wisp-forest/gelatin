package io.wispforest.jello.main.common.compat.modmenu;

import io.wispforest.jello.main.common.config.JelloConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;

public class JelloModMenuIntegration implements ModMenuApi {

    public ConfigScreenFactory<?> getModConfigScreenFactory(){
        return parent -> AutoConfig.getConfigScreen(JelloConfig.class, parent).get();
    }

}
