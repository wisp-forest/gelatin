package io.wispforest.gelatin.dye_entries.misc;

import io.wispforest.gelatin.common.misc.GelatinConstants;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.stat.StatFormatter;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;

import java.util.List;

public class GelatinStats {

    public static final Identifier CLEAN_BLOCK = GelatinConstants.id("clean_block");

    public static final Identifier DYE_BLOCK = GelatinConstants.id("dye_block");
    public static final Identifier DYE_ARMOR = GelatinConstants.id("dye_armor");
    //public static final Identifier DYE_BANNER = Gelatin.id("dye_banner");
    public static final Identifier DYE_SHULKER_BOX = GelatinConstants.id("dye_shulker_box");

    public static void init(){
        List<Identifier> ids = List.of(CLEAN_BLOCK, DYE_BLOCK, DYE_ARMOR, DYE_SHULKER_BOX);

        for (Identifier id : ids) {
            Registry.register(Registries.CUSTOM_STAT, id, id);
            Stats.CUSTOM.getOrCreateStat(id, StatFormatter.DEFAULT);
        }
    }
}
