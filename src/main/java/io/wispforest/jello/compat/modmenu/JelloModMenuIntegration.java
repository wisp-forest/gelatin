package io.wispforest.jello.compat.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.wispforest.jello.misc.JelloConfig;
import me.shedaniel.autoconfig.AutoConfig;

public class JelloModMenuIntegration implements ModMenuApi {

    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> AutoConfig.getConfigScreen(JelloConfig.class, parent).get();
    }

}
