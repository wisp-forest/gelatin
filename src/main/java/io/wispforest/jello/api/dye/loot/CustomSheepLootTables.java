package io.wispforest.jello.api.dye.loot;

import com.google.common.collect.ImmutableMap;
import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.jello.api.dye.registry.variants.DyedVariantContainer;
import io.wispforest.jello.api.dye.registry.variants.VanillaBlockVariants;
import net.fabricmc.fabric.api.loot.v1.FabricLootSupplierBuilder;
import net.fabricmc.fabric.api.loot.v1.LootJsonParser;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemConvertible;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class CustomSheepLootTables {

    public static Map<Identifier, LootTable> initSheepLootTables(Map<Identifier, LootTable> originalTable){
        Map<Identifier, LootTable> tables = new HashMap<>(originalTable);

        for(Map.Entry<DyeColorant, DyedVariantContainer> entry : DyedVariantContainer.DYED_VARIANTS.entrySet()){
            DyeColorant dyeColorant = entry.getKey();

            if(!DyeColorantRegistry.Constants.VANILLA_DYES.contains(dyeColorant)) {
                Block woolBlock = DyedVariantContainer.getDyedBlockVariant(dyeColorant, VanillaBlockVariants.WOOL);

                LootTable table = createForSheep(woolBlock).build();
                Identifier lootTableId = createSheepLootTableIdFromColor(dyeColorant);

                tables.put(lootTableId, table);
            }
        }

        return tables;
    }

    public static Identifier createSheepLootTableIdFromColor(DyeColorant dyeColorant){
        return new Identifier("entities/sheep/" + dyeColorant.getName());
    }

    private static FabricLootSupplierBuilder createForSheep(ItemConvertible item) {
        return FabricLootSupplierBuilder.builder()
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).with(ItemEntry.builder(item)))
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).with(LootTableEntry.builder(EntityType.SHEEP.getLootTableId())));
    }

}
