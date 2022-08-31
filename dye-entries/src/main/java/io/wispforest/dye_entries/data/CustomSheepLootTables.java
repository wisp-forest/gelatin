package io.wispforest.dye_entries.data;

import io.wispforest.common.events.LootTableInjectionEvent;
import io.wispforest.common.util.VersatileLogger;
import io.wispforest.dye_entries.variants.DyeableVariantManager;
import io.wispforest.dye_entries.variants.VanillaBlockVariants;
import io.wispforest.dye_registry.DyeColorant;
import io.wispforest.dye_registry.DyeColorantRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemConvertible;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.util.Identifier;

import java.util.Map;

public class CustomSheepLootTables {

    public static void initSheepLootTables(LootTableInjectionEvent.LootTableMapHelper helper) {
        VersatileLogger logger = new VersatileLogger("SheepLootTable");

        for (Map.Entry<DyeColorant, DyeableVariantManager.DyeColorantVariantData> entry : DyeableVariantManager.getVariantMap().entrySet()) {
            DyeColorant dyeColorant = entry.getKey();

            if (!DyeColorantRegistry.Constants.VANILLA_DYES.contains(dyeColorant)) {
                Block woolBlock = DyeableVariantManager.getDyedBlockVariant(dyeColorant, VanillaBlockVariants.WOOL);

                LootTable table = createForSheep(woolBlock).build();
                Identifier lootTableId = createSheepLootTableIdFromColor(dyeColorant);

                if(!helper.addLootTable(lootTableId, table)){
                    logger.failMessage("Seems that a lootTable for a custom Sheep DyeColorant lootTable already exists, will not be added then.");
                }

            }
        }
    }

    public static Identifier createSheepLootTableIdFromColor(DyeColorant dyeColorant) {
        return new Identifier("entities/sheep/" + dyeColorant.getName());
    }

    private static LootTable.Builder createForSheep(ItemConvertible item) {
        return LootTable.builder()
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).with(ItemEntry.builder(item)))
                .pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).with(LootTableEntry.builder(EntityType.SHEEP.getLootTableId())));
    }

    public static void init(){
        LootTableInjectionEvent.ADD_LOOT_TABLES_EVENT.register(CustomSheepLootTables::initSheepLootTables);
    }
}
