package io.wispforest.gelatin.common.events;

import io.wispforest.gelatin.common.data.ExtLangInterface;
import io.wispforest.gelatin.common.misc.GelatinConstants;
import io.wispforest.gelatin.common.util.VersatileLogger;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.resource.language.LanguageDefinition;

import java.util.List;
import java.util.Map;

public class TranslationInjectionEvent {

    public static Event<LanguageInjection> AFTER_LANGUAGE_LOAD = EventFactory.createArrayBacked(LanguageInjection.class,
        (listeners) -> (helper) -> {
            VersatileLogger logger = new VersatileLogger("TranslationInjectionEvent", () -> GelatinConstants.DEBUG_ENV_VAR | GelatinConstants.DEBUG_ENV);

            for (LanguageInjection event : listeners) {
                event.generateLanguageTranslations(helper);
            }

            logger.stopTimerPrint("translation event took about ");
        }
    );

    public interface LanguageInjection {
        void generateLanguageTranslations(TranslationMapHelper helper);
    }

    public static class TranslationMapHelper implements ExtLangInterface {
        private final Map<String, String> translationData;
        private final List<LanguageDefinition> loadingDefinitions;

        public TranslationMapHelper(Map<String, String> translationData, List<LanguageDefinition> currentDefinitions){
            this.translationData = translationData;
            this.loadingDefinitions = currentDefinitions;
        }

        public List<LanguageDefinition> getLangDefinitions(){
            return this.loadingDefinitions;
        }

        @Override
        public boolean addTranslation(String key, String value){
            if(!getDataMap().containsKey(key)){
                ExtLangInterface.super.addTranslation(key, value);

                return true;
            }

            return false;
        }

        @Override
        public Map<String, String> getDataMap() {
            return translationData;
        }
    }
}
