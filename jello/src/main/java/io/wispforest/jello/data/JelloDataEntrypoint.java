package io.wispforest.jello.data;

import io.wispforest.jello.client.data.JelloBlockStateProvider;
import io.wispforest.jello.data.providers.JelloBlockLootTable;
import io.wispforest.jello.data.providers.JelloLangProvider;
import io.wispforest.jello.data.providers.JelloRecipeProvider;
import io.wispforest.jello.data.providers.JelloTagsProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.loader.api.FabricLoader;

public class JelloDataEntrypoint implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        //fabricDataGenerator.addProvider(new JelloTagsProvider.BlockTagProvider(fabricDataGenerator));

        var pack = fabricDataGenerator.createPack();

        pack.addProvider(JelloTagsProvider.ItemTagProvider::new);

        pack.addProvider(JelloRecipeProvider::new);

        pack.addProvider(JelloLangProvider::new);

        pack.addProvider(JelloBlockLootTable::new);

        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            pack.addProvider(JelloBlockStateProvider::new);
        }
    }
}
