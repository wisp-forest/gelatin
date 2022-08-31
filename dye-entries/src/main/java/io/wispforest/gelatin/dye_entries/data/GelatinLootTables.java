package io.wispforest.gelatin.dye_entries.data;

import io.wispforest.gelatin.common.events.LootTableInjectionEvent;
import io.wispforest.gelatin.dye_entries.variants.block.DyeableBlockVariant;
import io.wispforest.gelatin.dye_registry.DyeColorant;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.CandleBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.SlabType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.*;
import net.minecraft.loot.entry.DynamicEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.*;
import net.minecraft.loot.provider.nbt.ContextLootNbtProvider;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.item.EnchantmentPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.state.property.Property;
import net.minecraft.util.StringIdentifiable;

import java.util.HashSet;
import java.util.Set;

import static io.wispforest.gelatin.dye_entries.variants.VanillaBlockVariants.SHULKER_BOX;

public class GelatinLootTables {

    private static final LootCondition.Builder WITH_SILK_TOUCH = MatchToolLootCondition.builder(
            ItemPredicate.Builder.create().enchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, NumberRange.IntRange.atLeast(1)))
    );

    private static final Set<Item> CUSTOM_EXPLOSION_IMMUNE = new HashSet<>();

    public static void registerLootTablesGeneration(){
        for(DyeColorant dyeColorant : DyeColorantRegistry.getAllColorants()){
            CUSTOM_EXPLOSION_IMMUNE.add(SHULKER_BOX.getColoredBlockItem(dyeColorant));
        }

        CustomSheepLootTables.init();

        LootTableInjectionEvent.ADD_LOOT_TABLES_EVENT.register(helper -> {
            for(DyeableBlockVariant blockVariant : DyeableBlockVariant.getAllBlockVariants()){
                blockVariant.generateAllLootTables(helper);
            }
        });
    }

    public static LootTable.Builder drops(ItemConvertible drop) {
        return LootTable.builder()
                .pool(addSurvivesExplosionCondition(drop, LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).with(ItemEntry.builder(drop))));
    }

    public static <T extends LootFunctionConsumingBuilder<T>> T applyExplosionDecay(ItemConvertible drop, LootFunctionConsumingBuilder<T> builder) {
        return (T)(!CUSTOM_EXPLOSION_IMMUNE.contains(drop.asItem()) ? builder.apply(ExplosionDecayLootFunction.builder()) : builder.getThisFunctionConsumingBuilder());
    }

    public static <T extends LootConditionConsumingBuilder<T>> T addSurvivesExplosionCondition(ItemConvertible drop, LootConditionConsumingBuilder<T> builder) {
        return (T)(!CUSTOM_EXPLOSION_IMMUNE.contains(drop.asItem())
                ? builder.conditionally(SurvivesExplosionLootCondition.builder())
                : builder.getThisConditionConsumingBuilder());
    }


    public static LootTable.Builder slabDrops(Block drop) {
        return LootTable.builder().pool(LootPool.builder()
            .rolls(ConstantLootNumberProvider.create(1.0F))
            .with(applyExplosionDecay(drop, ItemEntry.builder(drop).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(2.0F))
                .conditionally(BlockStatePropertyLootCondition.builder(drop).properties(StatePredicate.Builder.create().exactMatch(SlabBlock.TYPE, SlabType.DOUBLE)))))
            )
        );
    }

    public static <T extends Comparable<T> & StringIdentifiable> LootTable.Builder dropsWithProperty(Block drop, Property<T> property, T value) {
        return LootTable.builder().pool(addSurvivesExplosionCondition(drop, LootPool.builder()
            .rolls(ConstantLootNumberProvider.create(1.0F))
            .with(ItemEntry.builder(drop)
                .conditionally(BlockStatePropertyLootCondition.builder(drop).properties(StatePredicate.Builder.create().exactMatch(property, value))))
            )
        );
    }

    public static LootTable.Builder candleDrops(Block candle) {
        return LootTable.builder().pool(LootPool.builder()
            .rolls(ConstantLootNumberProvider.create(1.0F))
            .with(applyExplosionDecay(candle, ItemEntry.builder(candle)
                .apply(
                    SetCountLootFunction.builder(ConstantLootNumberProvider.create(2.0F))
                        .conditionally(BlockStatePropertyLootCondition.builder(candle).properties(StatePredicate.Builder.create().exactMatch(CandleBlock.CANDLES, 2)))
                )
                .apply(
                    SetCountLootFunction.builder(ConstantLootNumberProvider.create(3.0F))
                        .conditionally(BlockStatePropertyLootCondition.builder(candle).properties(StatePredicate.Builder.create().exactMatch(CandleBlock.CANDLES, 3)))
                )
                .apply(
                    SetCountLootFunction.builder(ConstantLootNumberProvider.create(4.0F))
                        .conditionally(BlockStatePropertyLootCondition.builder(candle).properties(StatePredicate.Builder.create().exactMatch(CandleBlock.CANDLES, 4)))
                )
            ))
        );
    }

    public static LootTable.Builder candleCakeDrops(Block candle) {
        return LootTable.builder().pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F)).with(ItemEntry.builder(candle)));
    }

    public static LootTable.Builder shulkerBoxDrops(Block drop) {
        return LootTable.builder().pool(addSurvivesExplosionCondition(drop, LootPool.builder()
            .rolls(ConstantLootNumberProvider.create(1.0F))
            .with(ItemEntry.builder(drop)
                .apply(CopyNameLootFunction.builder(CopyNameLootFunction.Source.BLOCK_ENTITY))
                .apply(CopyNbtLootFunction.builder(ContextLootNbtProvider.BLOCK_ENTITY)
                    .withOperation("Lock", "BlockEntityTag.Lock")
                    .withOperation("LootTable", "BlockEntityTag.LootTable")
                    .withOperation("LootTableSeed", "BlockEntityTag.LootTableSeed"))
                .apply(SetContentsLootFunction.builder(BlockEntityType.SHULKER_BOX).withEntry(DynamicEntry.builder(ShulkerBoxBlock.CONTENTS)))))
        );
    }

    public static LootTable.Builder dropsWithSilkTouch(ItemConvertible drop) {
        return LootTable.builder()
                .pool(LootPool.builder().conditionally(WITH_SILK_TOUCH).rolls(ConstantLootNumberProvider.create(1.0F)).with(ItemEntry.builder(drop)));
    }

}
