package io.wispforest.jello.data.loot;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.jello.api.dye.registry.variants.DyeableBlockVariant;
import io.wispforest.jello.api.dye.registry.variants.DyedVariantContainer;
import io.wispforest.jello.api.dye.registry.variants.VanillaBlockVariants;
import io.wispforest.jello.data.CustomSheepLootTables;
import io.wispforest.jello.data.tags.JelloTags;
import io.wispforest.jello.mixin.BlockLootTableGeneratorAccessor;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.enums.BedPart;
import net.minecraft.data.server.BlockLootTableGenerator;
import net.minecraft.item.Item;
import net.minecraft.loot.LootTable;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static io.wispforest.jello.api.dye.registry.variants.VanillaBlockVariants.*;

public class JelloLootTables {

    public static final Event<AddLootTables> ADD_LOOT_TABLES_EVENT = EventFactory.createArrayBacked(AddLootTables.class, callbacks -> (currentMap) -> {
        for(AddLootTables callback : callbacks){
            Map<Identifier, LootTable> callbackMap = new HashMap<>();

            callback.afterResourceLoad(callbackMap);

            currentMap.putAll(callbackMap);
        }
    });

    public static void registerLootTablesGeneration(){
        Set<Item> EXPLOSION_IMMUNE = new HashSet<>();

        for(DyeColorant dyeColorant : DyeColorantRegistry.getAllColorants()){
            EXPLOSION_IMMUNE.add(SHULKER_BOX.getColoredBlock(dyeColorant).asItem());
        }

        BlockLootTableGeneratorAccessor.jello$setEXPLOSION_IMMUNE(Set.copyOf(EXPLOSION_IMMUNE));

        CustomSheepLootTables.init();

        ADD_LOOT_TABLES_EVENT.register(map -> {
            for(DyeableBlockVariant blockVariant : DyeableBlockVariant.getAllVariants()){
                blockVariant.generateAllLootTables(map);
            }
        });
    }

    public interface AddLootTables{
        void afterResourceLoad(Map<Identifier, LootTable> map);
    }
}
