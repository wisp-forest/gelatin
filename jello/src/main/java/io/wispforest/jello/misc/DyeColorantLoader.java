package io.wispforest.jello.misc;

import com.google.gson.*;
import io.wispforest.gelatin.common.util.VersatileLogger;
import io.wispforest.gelatin.dye_registry.DyeColorant;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import io.wispforest.gelatin.dye_entries.variants.DyeableVariantManager;
import io.wispforest.jello.Jello;
import net.minecraft.block.MapColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.io.InputStreamReader;

public class DyeColorantLoader {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void loadFromJson() {
        VersatileLogger logger = new VersatileLogger("JsonToRegistry");

        try {
            JsonArray names = JsonHelper.getArray(GSON.fromJson(new InputStreamReader(DyeColorantRegistry.class.getClassLoader().getResourceAsStream("assets/jello/other/colorDatabase.json")), JsonObject.class), "colors");

            for (var i = 0; i < names.size(); i++) {
                JsonObject colorJson = names.get(i).getAsJsonObject();

                Identifier colorIdentifier = Jello.id(colorJson.get("identifierSafeName").getAsString());
                int colorValue = Integer.parseInt(colorJson.get("hexValue").getAsString(), 16);

                if (DyeColorantRegistry.DYE_COLOR.containsId(colorIdentifier)) {
                    //continue;
                    colorIdentifier = Jello.id(colorIdentifier.getPath() + "_2");
                }

                if (DyeColorantRegistry.DYE_COLOR.containsId(new Identifier(colorIdentifier.getPath()))) {
                    continue;
                }

                DyeColorant currentDyeColor = DyeColorantRegistry.registerDyeColor(colorIdentifier, MapColor.CLEAR, colorValue);
                DyeableVariantManager.createVariantContainer(currentDyeColor);
            }

            logger.stopTimerPrint("It seems that the registry filling took ");
            logger.infoMessage("Total amount of registered dyes from json are " + DyeColorantRegistry.DYE_COLOR.size());
        } catch (JsonSyntaxException | JsonIOException e) {
            logger.failMessage("Something has gone with the json to Dye Registry method!");
            e.printStackTrace();
        }
    }
}
