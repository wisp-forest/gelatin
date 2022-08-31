package io.wispforest.common.data;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Map;

/**
 * Inteface with helper methods to make it easy to create a map containing a translation key to name map
 *
 * Idea based off of the forge Language provider
 */

public interface LangInterface {

    Map<String, String> getDataMap();

    default void addBlock(Block key, String name) {
        addTranslation(key.getTranslationKey(), name);
    }

    default void addItem(Item key, String name) {
        addTranslation(key.getTranslationKey(), name);
    }

    default void addItemStack(ItemStack key, String name) {
        addTranslation(key.getTranslationKey(), name);
    }

    default void addEnchantment(Enchantment key, String name) {
        addTranslation(key.getTranslationKey(), name);
    }

    default void addEffect(StatusEffect key, String name) {
        addTranslation(key.getTranslationKey(), name);
    }

    default void addEntityType(EntityType<?> key, String name) {
        addTranslation(key.getTranslationKey(), name);
    }

    default boolean addTranslation(String key, String value) {
        if (getDataMap().put(key, value) != null) {
            throw new IllegalStateException("Duplicate translation key " + key);
        }

        return true;
    }
}
