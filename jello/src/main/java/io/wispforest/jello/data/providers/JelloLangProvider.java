package io.wispforest.jello.data.providers;

import io.wispforest.gelatin.common.data.providers.AbstractLanguageProvider;
import io.wispforest.gelatin.common.util.WordMagic;
import io.wispforest.gelatin.dye_registry.DyeColorant;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import io.wispforest.jello.block.JelloBlocks;
import io.wispforest.jello.item.JelloItems;
import io.wispforest.jello.item.SpongeItem;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
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

        addTranslation(SpongeItem.DIRTINESS_TRANSLATION_KEY, "Dirty Sponge");

        addOCToolTipAndNameEntry("addCustomJsonColors", "Enable Json Colors", "Whether or not Jello will add it's included 1822 colors to Minecraft internally.");

        addOCCategoryName("common", "Main Config");

        addTranslation("text.jello.dye_bundle_pattern", "%1$s [%2$s]");

        addTranslation("itemGroup.misc.tab.dyes", "Custom Dyes");
        addTranslation("itemGroup.misc.tab.block_vars", "Colored Block Variants");

        addTranslation("item.jello.sponge.desc", "Use on a block to remove dye");
        addTranslation("item.jello.sponge.desc.dirty", "Clean by using on water cauldron");

        addTranslation("slime_slabs_condensed", "Slime Slabs");
        addTranslation("slime_blocks_condensed", "Slime Blocks");

//        addTranslation("tooltip.vanilla_slime_slabs_condensed", "Only contains Vanilla Colors");
//        addTranslation("tooltip.vanilla_slime_blocks_condensed", "Only contains Vanilla Colors");

        addTranslation("itemGroup.jello.jello_group", "Jello");

        addTranslation("itemGroup.jello.jello_group.tab.jello_tools", "Jello Stuff");
        addTranslation("itemGroup.jello.jello_group.tab.dyed_item_variants", "Jello Item Variants");
        addTranslation("itemGroup.jello.jello_group.tab.dyed_block_variants", "Jello Block Variants");

        addTranslation("jello.gui.dye_mixer", "Dye Mixer");
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

    private static final Map<Predicate<Object>, Registry<?>> predicteToRegistryMap = new HashMap<>();

    static {
        predicteToRegistryMap.put(o -> o instanceof Item, Registry.ITEM);
        predicteToRegistryMap.put(o -> o instanceof Block, Registry.BLOCK);
        predicteToRegistryMap.put(o -> o instanceof DyeColorant, DyeColorantRegistry.DYE_COLOR);
        predicteToRegistryMap.put(o -> o instanceof EntityType, Registry.ENTITY_TYPE);
    }

    public static <T> String getAutomaticNameForEntry(T entry) {
        Registry<T> registry = null;

        for(Map.Entry<Predicate<Object>, Registry<?>> mapEntry : predicteToRegistryMap.entrySet()){
            if(mapEntry.getKey().test(entry)){
                registry = (Registry<T>) mapEntry.getValue();
            }
        }

        if (registry == null) {
            throw new InvalidEntry(entry + ": Is a invaild entry and could not be found within the get entryClassToRegistryMap.");
        }

        Identifier identifier = registry.getId(entry);

        if (identifier == null) {
            throw new NullPointerException(entry + ": Dose not have a identifier from the found registry it matches!");
        }

        return toEnglishName(identifier.getPath());
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

    private void addOCCategoryName(String keyName, String nameTranslation) {
        addTranslation("text.config.jello.section." + keyName, nameTranslation);
    }

    private void addOCToolTipAndNameEntry(String keyName, String nameTranslation, String tooltipTranslation) {
        addTranslation("text.config.jello.option." + keyName + ".tooltip", tooltipTranslation);
        addTranslation("text.config.jello.option." + keyName, nameTranslation);
    }
}
