package io.wispforest.gelatin.common.data.providers;

import io.wispforest.gelatin.common.data.ExtLangInterface;
import io.wispforest.gelatin.common.util.LangUtils;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

/**
 * Please use #{@link ExtLangInterface} instead}
 */
@Deprecated(since = "5.0.0")
public abstract class ImplLangProvider extends AbstractLanguageProvider implements ExtLangInterface {

    public ImplLangProvider(FabricDataOutput gen) {
        super(gen, "en_us");
    }

    abstract String modid();

    //-------------------------------------------------

    /**
     * Please use #{@link LangUtils#titleFormatString(String)}  instead}
     */
    @Deprecated(since = "5.0.0")
    public static String titleFormatString(String titleString){
        return LangUtils.titleFormatString(titleString);
    }

    /**
     * Please use #{@link LangUtils#titleFormatString(String[])}  instead}
     */
    @Deprecated(since = "5.0.0")
    public static String titleFormatString(String[] titleStringParts){
        return LangUtils.titleFormatString(titleStringParts);
    }

    /**
     * Please use #{@link LangUtils#titleFormatString(String[], boolean)}  instead}
     */
    @Deprecated(since = "5.0.0")
    public static String titleFormatString(String[] titleString, boolean pluralize){
        return LangUtils.titleFormatString(titleString, pluralize);
    }

    //-----------------------------------------------//

    /**
     * Please use #{@link LangUtils#getAutomaticNameForEntry instead}
     */
    @Deprecated(since = "5.0.0")
    public static <T> String getAutomaticNameForEntry(T entry) {
        return LangUtils.getAutomaticNameForEntry(entry);
    }

    /**
     * Please use #{@link LangUtils#toEnglishName(String)}  instead}
     */
    @Deprecated(since = "5.0.0")
    public static String toEnglishName(String internalName) {
        return LangUtils.capitalizeEachWord(internalName);
    }

    /**
     * Please use #{@link LangUtils#capitalizeEachWord(String)}  instead}
     */
    @Deprecated(since = "5.0.0")
    public static String capitalizeEachWord(String sentence){
        return LangUtils.capitalizeEachWord(sentence);
    }

    /**
     * Please use #{@link LangUtils#capitalizeEachWord(String[])}  instead}
     */
    @Deprecated(since = "5.0.0")
    public static String capitalizeEachWord(String[] internalNameParts) {
        return LangUtils.capitalizeEachWord(internalNameParts);
    }
}
