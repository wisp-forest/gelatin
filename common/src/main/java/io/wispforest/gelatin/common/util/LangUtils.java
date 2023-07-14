package io.wispforest.gelatin.common.util;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Class used for random String manipulation and auto english conversion from id
 */
public class LangUtils {

    private static final Map<Predicate<Object>, Registry<?>> predicteToRegistryMap = new HashMap<>();

    static {
        predicteToRegistryMap.put(o -> o instanceof Item, Registries.ITEM);
        predicteToRegistryMap.put(o -> o instanceof Block, Registries.BLOCK);
        //predicteToRegistryMap.put(o -> o instanceof DyeColorant, DyeColorantRegistry.DYE_COLOR);
        predicteToRegistryMap.put(o -> o instanceof EntityType, Registries.ENTITY_TYPE);
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

    //---

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

    //---

    private static class InvalidEntry extends RuntimeException {
        public InvalidEntry(String s) {
            super(s);
        }
    }
}
