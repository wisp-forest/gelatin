package io.wispforest.jello.api.events;

import io.wispforest.jello.Jello;
import io.wispforest.jello.api.data.LangInterface;
import io.wispforest.jello.api.util.VersatileLogger;
import io.wispforest.jello.data.providers.JelloLangProvider;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.Block;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;

import java.util.List;
import java.util.Map;

public class TranslationInjectionEvent {

    public static Event<LanguageInjection> AFTER_LANGUAGE_LOAD = EventFactory.createArrayBacked(LanguageInjection.class,
        (listeners) -> (helper) -> {
            VersatileLogger logger = new VersatileLogger("TranslationInjectionEvent", () -> Jello.DEBUG_ENV_VAR | Jello.DEBUG_ENV);

            for (LanguageInjection event : listeners) {
                event.generateLanguageTranslations(helper);
            }

            logger.stopTimerPrint("translation event took about ");
        }
    );

    public interface LanguageInjection {
        void generateLanguageTranslations(TranslationMapHelper helper);
    }

    public static class TranslationMapHelper implements LangInterface {
        private final Map<String, String> translationData;
        private final List<LanguageDefinition> loadingDefinitions;

        public TranslationMapHelper(Map<String, String> translationData, List<LanguageDefinition> currentDefinitions){
            this.translationData = translationData;
            this.loadingDefinitions = currentDefinitions;
        }

        public List<LanguageDefinition> getLangDefinitions(){
            return List.copyOf(loadingDefinitions);
        }

        public void addItem(Item item) {
            addItem(item, JelloLangProvider.getAutomaticNameForEntry(item));
        }

        public void addBlock(Block block) {
            addBlock(block, JelloLangProvider.getAutomaticNameForEntry(block));
        }

        public void addEntityType(EntityType<?> entity) {
            addEntityType(entity, JelloLangProvider.getAutomaticNameForEntry(entity));
        }

        @Override
        public boolean addTranslation(String key, String value){
            if(!getDataMap().containsKey(key)){
                LangInterface.super.addTranslation(key, value);

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
