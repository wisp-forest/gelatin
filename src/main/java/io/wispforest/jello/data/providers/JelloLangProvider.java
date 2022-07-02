package io.wispforest.jello.data.providers;

import io.wispforest.jello.api.data.providers.AbstractLanguageProvider;
import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.jello.api.dye.registry.variants.DyeableVariantManager;
import io.wispforest.jello.api.dye.registry.variants.block.DyeableBlockVariant;
import io.wispforest.jello.api.dye.registry.variants.item.DyeableItemVariant;
import io.wispforest.jello.api.util.WordMagic;
import io.wispforest.jello.block.JelloBlocks;
import io.wispforest.jello.item.JelloItems;
import io.wispforest.jello.item.SpongeItem;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class JelloLangProvider extends AbstractLanguageProvider {

    public JelloLangProvider(FabricDataGenerator gen) {
        super(gen, "en_us");
    }

    @Override
    protected void addTranslations() {

        addBlock(JelloBlocks.SLIME_SLAB);

        JelloItems.Slimeballs.SLIME_BALLS.forEach(this::addItem);

        addItem(JelloItems.JelloCups.SUGAR_CUP);

        JelloItems.JelloCups.JELLO_CUP.forEach(this::addItem);

        addItem(JelloItems.SPONGE);
        addItem(JelloItems.DYE_BUNDLE);

        addItem(JelloItems.ARTIST_PALETTE);
        addItem(JelloItems.EMPTY_ARTIST_PALETTE, "Empty Palette");

        addBlock(JelloBlocks.PAINT_MIXER);

        add(SpongeItem.DIRTINESS_TRANSLATION_KEY, "Dirty Sponge");

        addACToolTipAndNameEntry("enableGrayScalingOfEntities", "Enable GrayScaling of Entities", "[Warning: Will break texturepacks!] Used to allow for true color when a entity is dyed or color.");

        addACToolTipAndNameEntry("enableDyeingEntities", "Enable Dyeing of Entities", "Allow for the dyeing of entities using any dye.");
        addACToolTipAndNameEntry("enableDyeingPlayers", "Enable Dyeing of Players", "Allow for the dyeing of players using any dye.");
        addACToolTipAndNameEntry("enableDyeingBlocks", "Enable Dyeing of Blocks", "Allow for the dyeing of blocks using any vanilla dye.");

        addACToolTipAndNameEntry("addCustomJsonColors", "Enable Json Colors", "Whether or not Jello will add it's included 1822 colors to Minecraft internally.");
        addACToolTipAndNameEntry("enableTransparencyFixCauldrons", "Enable Transparency Fix for Cauldrons", "Enables a fix for water within cauldrons just being Opaque rather than translucent.");

        addACCategoryName("common", "Main Config");
        addACCategoryName("client", "Client Config");

        add("text.jello.dye_bundle_pattern", "%1$s [%2$s]");

        add("itemGroup.misc.tab.dyes", "Custom Dyes");
        add("itemGroup.misc.tab.block_vars", "Colored Block Variants");

        add("item.jello.sponge.desc", "Use on a block to remove dye");
        add("item.jello.sponge.desc.dirty", "Clean by using on water cauldron");

        add("vanilla_slime_slabs_condensed", "Slime Slabs");
        add("vanilla_slime_blocks_condensed", "Slime Blocks");

        add("tooltip.vanilla_slime_slabs_condensed", "Only contains Vanilla Colors");
        add("tooltip.vanilla_slime_blocks_condensed", "Only contains Vanilla Colors");

        add("itemGroup.jello.jello_group", "Jello");

        add("itemGroup.jello.jello_group.tab.jello_tools", "Jello Stuff");
        add("itemGroup.jello.jello_group.tab.dyed_item_variants", "Jello Item Variants");
        add("itemGroup.jello.jello_group.tab.dyed_block_variants", "Jello Block Variants");

        DyeableBlockVariant.getAllBlockVariants().stream().filter(dyeableBlockVariant -> !dyeableBlockVariant.alwaysReadOnly() && dyeableBlockVariant.createBlockItem()).forEach(dyeableBlockVariant -> {
            add(dyeableBlockVariant.variantIdentifier.getPath() + "_condensed", titleFormatString(dyeableBlockVariant.variantIdentifier.getPath().split("_")));
        });

        DyeableItemVariant.getAllItemVariants().stream().filter(dyeableItemVariant -> !dyeableItemVariant.alwaysReadOnly()).forEach(dyeableItemVariant -> {
            add(dyeableItemVariant.variantIdentifier.getPath() + "_condensed", titleFormatString(dyeableItemVariant.variantIdentifier.getPath().split("_")));
        });

        for (DyeableVariantManager.DyeColorantVariantData dyedVariant : DyeableVariantManager.getVariantMap().values()) {
            for (Block block : dyedVariant.dyedBlocks().values()) {
                addBlock(block);
            }

            addItem(dyedVariant.dyeItem());
        }
    }

    public static String titleFormatString(String titleString){
        return titleFormatString(titleString.split(" "), false);
    }

    public static String titleFormatString(String[] titleStringParts){
        return titleFormatString(titleStringParts, false);
    }

    public static String titleFormatString(String[] titleString, boolean pluralize){
        if(pluralize){
            titleString[titleString.length - 1] = WordMagic.INSTANCE.calculateSingularOrPlural(titleString[titleString.length - 1]);
        }

        return capitalizeEachWord(titleString);
    }

    //-----------------------------------------------//

    private void addItem(Item item) {
        addItem(item, getAutomaticNameForEntry(item));
    }

    private void addBlock(Block block) {
        addBlock(block, getAutomaticNameForEntry(block));
    }

    private void addEntityType(EntityType<?> entity) {
        addEntityType(entity, getAutomaticNameForEntry(entity));
    }

    private static final Map<Class<?>, Registry<?>> entryClassToRegistryMap = new HashMap<>();

    static {
        entryClassToRegistryMap.put(Item.class, Registry.ITEM);
        entryClassToRegistryMap.put(Block.class, Registry.BLOCK);
        entryClassToRegistryMap.put(DyeColorant.class, DyeColorantRegistry.DYE_COLOR);
        entryClassToRegistryMap.put(EntityType.class, Registry.ENTITY_TYPE);
    }

    public static <T> String getAutomaticNameForEntry(T entry) {
        Registry<T> registry = (Registry<T>) entryClassToRegistryMap.get(entry.getClass());

        if(registry != null) {
            return toEnglishName(registry.getId(entry).getPath());
        } else {
            throw new InvalidEntry(entry + ": Is a invaild entry and could not be found within the get entryClassToRegistryMap.");
        }
    }

    public static class InvalidEntry extends RuntimeException {
        public InvalidEntry(String s) {
            super(s);
        }
    }

    public static String toEnglishName(String internalName) {
        return capitalizeEachWord(internalName.toLowerCase(Locale.ROOT).split("_"));
    }

    public static String capitalizeEachWord(String sentence){
        return capitalizeEachWord(sentence.split(" "));
    }

    public static String capitalizeEachWord(String[] internalNameParts) {
        return Arrays.stream(internalNameParts)
                .map(WordUtils::capitalize)
                .collect(Collectors.joining(" "));
    }

    private void addACCategoryName(String keyName, String nameTranslation) {
        add("text.autoconfig.jello.category." + keyName, nameTranslation);
    }

    private void addACToolTipAndNameEntry(String keyName, String nameTranslation, String tooltipTranslation) {
        add("text.autoconfig.jello.option." + keyName + ".@Tooltip", tooltipTranslation);
        add("text.autoconfig.jello.option." + keyName, nameTranslation);
    }
}
