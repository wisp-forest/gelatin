package com.dragon.jello.common.data.providers;

import com.dragon.forge.LanguageProvider;
import com.dragon.jello.common.Jello;
import com.dragon.jello.common.blocks.BlockRegistry;
import com.dragon.jello.common.items.ItemRegistry;
import com.dragon.jello.common.items.SpongeItem;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class JelloLangProvider extends LanguageProvider {

    public JelloLangProvider(DataGenerator gen) {
        super(gen, Jello.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {

        BlockRegistry.SlimeBlockRegistry.COLORED_SLIME_BLOCKS.forEach((block) -> {
            addBlock(() -> block);
        });

        BlockRegistry.SlimeSlabRegistry.COLORED_SLIME_SLABS.forEach((block) -> {
            addBlock(() -> block);
        });

        addBlock(() -> BlockRegistry.SlimeSlabRegistry.SLIME_SLAB);

        ItemRegistry.SlimeBallItemRegistry.SLIME_BALLS.forEach((item) -> {
            addItem(() -> item);
        });

        addItem(() -> ItemRegistry.JelloCupItemRegistry.SUGAR_CUP);

        ItemRegistry.JelloCupItemRegistry.JELLO_CUP.forEach((item) -> {
            addItem(() -> item);
        });

        addItem(() -> ItemRegistry.MainItemRegistry.SPONGE);

        add(SpongeItem.DIRTINESS_TRANSLATION_KEY, "Dirty Sponge");

    }

    //-----------------------------------------------//

    private void addItem(Supplier<? extends Item> item) {
        addItem(item, getAutomaticNameItem(item));
    }

    private void addBlock(Supplier<? extends Block> block) {
        addBlock(block, getAutomaticNameBlock(block));
    }

    private void addEntityType(Supplier<? extends EntityType<?>> entity) {
        addEntityType(entity, getAutomaticNameEntityType(entity));
    }

    private String getAutomaticNameItem(Supplier<? extends Item> sup) {
        return toEnglishName(Registry.ITEM.getId(sup.get()).getPath());
    }

    private String getAutomaticNameBlock(Supplier<? extends Block> sup) {
        return toEnglishName(Registry.BLOCK.getId(sup.get()).getPath());
    }

    private String getAutomaticNameEntityType(Supplier<? extends EntityType<?>> sup) {
        return toEnglishName(Registry.ENTITY_TYPE.getId(sup.get()).getPath());
    }

    public static final String toEnglishName(String internalName) {
        return Arrays.stream(internalName.toLowerCase(Locale.ROOT).split("_"))
                .map(StringUtils::capitalize)
                .collect(Collectors.joining(" "));
    }
}
