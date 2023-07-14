package io.wispforest.gelatin.dye_entries;

import io.wispforest.gelatin.common.misc.GelatinConstants;
import io.wispforest.gelatin.dye_entries.data.GelatinLootTables;
import io.wispforest.gelatin.dye_entries.data.recipe.GelatinRecipeSerializers;
import io.wispforest.gelatin.dye_entries.misc.GelatinItemGroup;
import io.wispforest.gelatin.dye_entries.misc.GelatinStats;
import io.wispforest.gelatin.dye_entries.utils.DyeVariantBuilder;
import io.wispforest.gelatin.dye_entries.variants.impl.VanillaBlockVariants;
import io.wispforest.gelatin.dye_registry.DyeColorant;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import io.wispforest.gelatin.dye_registry.ducks.DyeBlockStorage;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class DyeEntriesInit implements ModInitializer {

    public static final OwoItemGroup MAIN_ITEM_GROUP = GelatinItemGroup.create(GelatinConstants.id("gelatin_group"));

    @Override
    public void onInitialize() {
        DyeVariantBuilder.registerModidModelRedirect(GelatinConstants.MODID);

        //----------------------------[Independent Api Stuff's]----------------------------
        DyeVariantBuilder.initVanillaDyes();

        setDyeColorantForMinecraftBlocks();
        //---------------------------------------------------------------------------------

        FieldRegistrationHandler.register(GelatinRecipeSerializers.class, GelatinConstants.MODID, false);

        FieldRegistrationHandler.processSimple(GelatinStats.class, false);

        GelatinLootTables.registerLootTablesGeneration();


    }

    public static void setDyeColorantForMinecraftBlocks(){
        List<String> allVanillaBlockVariants = VanillaBlockVariants.ALL_VANILLA_VARIANTS.stream().map(dyeableBlockVariant -> dyeableBlockVariant.variantIdentifier.getPath()).collect(Collectors.toList());

        Set<Map.Entry<RegistryKey<Block>, Block>> coloredBlocks = Registries.BLOCK.getEntrySet().stream().filter(entry -> {
            Identifier entryId = entry.getKey().getValue();

            if(Objects.equals(entryId.getNamespace(), "minecraft")){
                for(String vanillaBlockVariant : allVanillaBlockVariants){
                    if(entryId.getPath().contains(vanillaBlockVariant)){
                        return true;
                    }
                }
            }

            return false;
        }).collect(Collectors.toSet());


        for(Map.Entry<RegistryKey<Block>, Block> entry : coloredBlocks){
            String entryPath = entry.getKey().getValue().getPath();
            Block block = entry.getValue();

            for(DyeColorant vanillaColor : DyeColorantRegistry.Constants.VANILLA_DYES) {
                if (entryPath.contains(vanillaColor.getName())) {
                    ((DyeBlockStorage) block).setDyeColor(vanillaColor);

                    break;
                }
            }
        }
    }
}