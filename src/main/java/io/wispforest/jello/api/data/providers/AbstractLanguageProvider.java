package io.wispforest.jello.api.data.providers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.wispforest.jello.api.data.LangInterface;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

/**
 * Based on Forge LanguageProvider
 */
public abstract class AbstractLanguageProvider implements DataProvider, LangInterface {
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().setLenient().create();

    private final Map<String, String> langData = new TreeMap<>();
    private final FabricDataGenerator dataGenerator;
    private final String locale;

    public AbstractLanguageProvider(FabricDataGenerator dataGenerator, String locale) {
        this.dataGenerator = dataGenerator;
        this.locale = locale;
    }

    protected abstract void addTranslations();

    @Override
    public void run(DataWriter writer) throws IOException {
        addTranslations();

        if (!langData.isEmpty()) {
            DataProvider.writeToPath(writer, GSON.toJsonTree(langData), this.dataGenerator.getOutput().resolve("assets/" + dataGenerator.getModId() + "/lang/" + locale + ".json"));
        }
    }

    @Override
    public String getName() {
        return "Language: " + locale;
    }

    @Override
    public Map<String, String> getDataMap() {
        return this.langData;
    }
}

