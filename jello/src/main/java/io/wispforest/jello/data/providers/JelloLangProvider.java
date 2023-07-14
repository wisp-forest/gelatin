package io.wispforest.jello.data.providers;

import io.wispforest.gelatin.common.data.ExtLangInterface;
import io.wispforest.gelatin.common.data.providers.AbstractLanguageProvider;
import io.wispforest.jello.block.JelloBlocks;
import io.wispforest.jello.item.JelloItems;
import io.wispforest.jello.item.SpongeItem;
import io.wispforest.jello.misc.JelloPotions;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

public class JelloLangProvider extends AbstractLanguageProvider implements ExtLangInterface {

    public JelloLangProvider(FabricDataOutput output) {
        super(output, "en_us");
    }

    @Override
    protected void addTranslations() {
        addBlock(JelloBlocks.SLIME_SLAB);

        JelloItems.Slimeballs.SLIME_BALLS.forEach(this::addItem);

        addItem(JelloItems.GELATIN);
        addItem(JelloItems.BOWL_OF_SUGAR);

        addItem(JelloItems.GELATIN_SOLUTION);
        addItem(JelloItems.CONCENTRATED_DRAGON_BREATH);

        addPotion(JelloPotions.DRAGON_HEALTH);

        addPotion(JelloPotions.GOLDEN_LIQUID);
        addPotion(JelloPotions.ENCHANTED_GOLDEN_LIQUID);

        addPotion(JelloPotions.NAUTICAL_POWER);

        addPotion(JelloPotions.VILLAGE_HERO);

        addPotion(JelloPotions.DOLPHINS_PACT, "the Dolphins Pact");

        addItem(JelloItems.JelloCups.SUGAR_CUP);

        JelloItems.JelloCups.JELLO_CUPS.forEach(item -> {
            if(item == JelloItems.JelloCups.YELLOW_JELLO_CUP){
                this.addTranslation("item.yellow_jello_cup", "Golden Jello Cup");
                this.addTranslation("item.yellow_jello_cup.enchanted", "Enchanted Golden Jello Cup");
            } else {
                this.addItem(item);
            }
        });

        addItem(JelloItems.SPONGE);
        addItem(JelloItems.DYE_BUNDLE);

        addItem(JelloItems.ARTIST_PALETTE);
        addItem(JelloItems.EMPTY_ARTIST_PALETTE, "Empty Palette");

        addBlock(JelloBlocks.PAINT_MIXER);

        addTranslation(SpongeItem.DIRTINESS_TRANSLATION_KEY, "Dirty Sponge");

        addOCName("Jello Config");

        addOCToolTipAndNameEntry("addCustomJsonColors", "Enable Json Colors", "Whether or not Jello will add it's included 1822 colors to Minecraft internally.");

        addOCCategoryName("common", "Main Config");

        addTranslation("text.jello.dye_bundle_pattern", "%1$s [%2$s]");

        addTranslation("itemGroup.misc.tab.dyes", "Custom Dyes");
        addTranslation("itemGroup.misc.tab.block_vars", "Colored Block Variants");

        addTranslation("item.jello.sponge.desc", "Use on a block to remove dye");
        addTranslation("item.jello.sponge.desc.dirty", "Clean by using on water cauldron");

        addTranslation("slime_slabs_condensed", "Slime Slabs");
        addTranslation("slime_blocks_condensed", "Slime Blocks");
        addTranslation("slime_balls_condensed", "Slime Balls");

//        addTranslation("tooltip.vanilla_slime_slabs_condensed", "Only contains Vanilla Colors");
//        addTranslation("tooltip.vanilla_slime_blocks_condensed", "Only contains Vanilla Colors");

        addTranslation("itemGroup.jello.jello_group", "Jello");

        addTranslation("itemGroup.jello.jello_group.tab.jello_tools", "Jello Stuff");
        addTranslation("itemGroup.jello.jello_group.tab.dyed_item_variants", "Jello Item Variants");
        addTranslation("itemGroup.jello.jello_group.tab.dyed_block_variants", "Jello Block Variants");

        addTranslation("jello.gui.dye_mixer", "Dye Mixer");
    }

    //-----------------------------------------------//

    private void addOCName(String nameTranslation) {
        addTranslation("text.config.jello.title", nameTranslation);
    }

    private void addOCCategoryName(String keyName, String nameTranslation) {
        addTranslation("text.config.jello.section." + keyName, nameTranslation);
    }

    private void addOCToolTipAndNameEntry(String keyName, String nameTranslation, String tooltipTranslation) {
        addTranslation("text.config.jello.option." + keyName + ".tooltip", tooltipTranslation);
        addTranslation("text.config.jello.option." + keyName, nameTranslation);
    }
}
