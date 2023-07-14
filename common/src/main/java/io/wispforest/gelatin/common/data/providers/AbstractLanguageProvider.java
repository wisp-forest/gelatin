package io.wispforest.gelatin.common.data.providers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.wispforest.gelatin.common.data.LangInterface;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

/**
 * Based on Forge LanguageProvider
 */
public abstract class AbstractLanguageProvider implements DataProvider, LangInterface {
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().setLenient().create();

    private final Map<String, String> langData = new TreeMap<>();
    private final FabricDataOutput output;
    private final String locale;

    public AbstractLanguageProvider(FabricDataOutput output, String locale) {
        this.output = output;
        this.locale = locale;
    }

    protected abstract void addTranslations();

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        addTranslations();

        if (!langData.isEmpty()) {
            DataProvider.writeToPath(writer, GSON.toJsonTree(langData), output.resolvePath(DataOutput.OutputType.RESOURCE_PACK).resolve("/lang/" + locale + ".json"));
        }

        return CompletableFuture.completedFuture(null);
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

