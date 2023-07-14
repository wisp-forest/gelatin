package io.wispforest.gelatin.common.data;

import io.wispforest.gelatin.common.util.LangUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;

/**
 * A version containing automatic english Translations
 */
public interface ExtLangInterface extends LangInterface {

    default void addItem(Item item) {
        addItem(item, LangUtils.getAutomaticNameForEntry(item));
    }

    default void addBlock(Block block) {
        addBlock(block, LangUtils.getAutomaticNameForEntry(block));
    }

    default void addEntityType(EntityType<?> entity) {
        addEntityType(entity, LangUtils.getAutomaticNameForEntry(entity));
    }

    default void addPotion(Potion potion){
        addPotion(potion, LangUtils.toEnglishName(potion.finishTranslationKey("")));
    }
}
