package com.dragon.jello.common.data;

import com.dragon.jello.client.data.JelloBlockStateProvider;
import com.dragon.jello.common.compat.consistencyplus.data.ConsistencyPlusTags;
import com.dragon.jello.common.compat.consistencyplus.data.providers.ConsistencyPlusTagProvider;
import com.dragon.jello.common.data.providers.JelloLangProvider;
import com.dragon.jello.common.data.providers.JelloRecipeProvider;
import com.dragon.jello.common.data.providers.JelloTagsProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.FabricLoaderImpl;

public class JelloDataEntrypoint implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        fabricDataGenerator.addProvider(new JelloTagsProvider.BlockTagProvider(fabricDataGenerator));
        fabricDataGenerator.addProvider(new JelloTagsProvider.ItemTagProvider(fabricDataGenerator));

        fabricDataGenerator.addProvider(new JelloRecipeProvider(fabricDataGenerator));

        fabricDataGenerator.addProvider(new JelloLangProvider(fabricDataGenerator));

        if(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT){
            fabricDataGenerator.addProvider(new JelloBlockStateProvider(fabricDataGenerator));
        }

        if(FabricLoaderImpl.INSTANCE.isModLoaded("consistency_plus")){
            System.out.println("TEST TEST TEST");
            ConsistencyPlusTags.init();
            fabricDataGenerator.addProvider(new ConsistencyPlusTagProvider.BlockTagProvider(fabricDataGenerator));
        }
    }
}
