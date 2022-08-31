package io.wispforest.common.data.providers;

import io.wispforest.common.util.WordMagic;
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

public abstract class ImplLangProvider extends AbstractLanguageProvider {

    private static final Map<Predicate<Object>, Registry<?>> predicteToRegistryMap = new HashMap<>();

    static {
        predicteToRegistryMap.put(o -> o instanceof Item, Registry.ITEM);
        predicteToRegistryMap.put(o -> o instanceof Block, Registry.BLOCK);
        //predicteToRegistryMap.put(o -> o instanceof DyeColorant, DyeColorantRegistry.DYE_COLOR);
        predicteToRegistryMap.put(o -> o instanceof EntityType, Registry.ENTITY_TYPE);
    }

    public ImplLangProvider(FabricDataGenerator gen) {
        super(gen, "en_us");
    }

    abstract String modid();

    private void addItem(Item item) {
        addItem(item, getAutomaticNameForEntry(item));
    }

    private void addBlock(Block block) {
        addBlock(block, getAutomaticNameForEntry(block));
    }

    private void addEntityType(EntityType<?> entity) {
        addEntityType(entity, getAutomaticNameForEntry(entity));
    }

    //-------------------------------------------------

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
        addTranslation("text.autoconfig." + modid() + ".category." + keyName, nameTranslation);
    }

    private void addACToolTipAndNameEntry(String keyName, String nameTranslation, String tooltipTranslation) {
        addTranslation("text.autoconfig." + modid() + ".option." + keyName + ".@Tooltip", tooltipTranslation);
        addTranslation("text.autoconfig." + modid() + ".option." + keyName, nameTranslation);
    }

    private static class InvalidEntry extends RuntimeException {
        public InvalidEntry(String s) {
            super(s);
        }
    }
}
