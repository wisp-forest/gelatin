package com.dragon.jello.main.common.compat.modmenu;

import com.dragon.jello.main.common.config.JelloConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;

public class JelloModMenuIntegration implements ModMenuApi {

    public ConfigScreenFactory<?> getModConfigScreenFactory(){
        return parent -> AutoConfig.getConfigScreen(JelloConfig.class, parent).get();
    }

}
