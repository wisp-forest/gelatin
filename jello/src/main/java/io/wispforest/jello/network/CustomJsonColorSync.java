package io.wispforest.jello.network;

import com.mojang.logging.LogUtils;
import io.wispforest.jello.Jello;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.text.Text;
import org.slf4j.Logger;

public class CustomJsonColorSync {

    private static Logger LOGGER = LogUtils.getLogger();

    public static void confirmMatchingConfigOptions(boolean isCustomColorsEnabledClient, ServerLoginNetworkHandler handler){
        if(Jello.getConfig().addCustomJsonColors() != isCustomColorsEnabledClient){
            LOGGER.info("[CustomJsonColorSync]: A Player trying to connect seems to have differing config options for Custom Json Colors compared to the server. They have been disconnected.");

            Text disconnectText;
            if(Jello.getConfig().addCustomJsonColors() && !isCustomColorsEnabledClient) {
                disconnectText = Text.of("The Server currently has Custom Json Colors §a§lENABLED!§r\nEither §aenable§r the option within Jello's config or contact the server admin. §l(Will require Restart to apply)§r");
            } else {
                disconnectText = Text.of("The Server currently has Custom Json Colors §4§lDISABLED!§r\nEither §4disable§r the option within Jello's config or contact the server admin. §l(Will require Restart to apply)§r");
            }

            handler.disconnect(disconnectText);
        }else{
            LOGGER.debug("[CustomJsonColorSync]: A Player Passed Custom Json Color config check!");
        }
    }
}
