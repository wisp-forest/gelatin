package com.dragon.jello.lib.registry;

import com.dragon.jello.common.data.tags.JelloTags;
import net.minecraft.block.Block;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ColorBlockRegistry {

    private static final Map<Tag.Identified<Block>, Map<DyeColor, Block>> REGISTRY = new HashMap<>();

    private static final Logger LOGGER = LogManager.getLogger(ColorBlockRegistry.class);

    private static final DyeColor[] DYE_VALUES = DyeColor.values();

    public static void registerBlockSet(List<Block> blockTypes, Block defaultBlock, Tag.Identified<Block> blockTags){
        if(blockTypes.size() > 16){
            loggerError("The Block List is greater than 17! Not Registering these blocks" + blockTypes.toString());
            return;
        }

        Map<DyeColor, Block> map = new HashMap<>();

        for(int z = 0; z < DYE_VALUES.length; z++){
            String currentDyeValueName = DYE_VALUES[z].getName();

            Block colorBlock = blockTypes.get(z);

            if(getDyeColorFromBlock(colorBlock) == DYE_VALUES[z].getName()){
                Optional<Block> possibleBlock = blockTypes.stream().filter((block) -> {
                    String blockDyeColorPrefix = getDyeColorFromBlock(block);

                    return blockDyeColorPrefix == currentDyeValueName;
                }).findAny();

                if(possibleBlock.isPresent()){
                    colorBlock = possibleBlock.get();
                }else{
                    loggerError("The Block List dose not contain a matching dyeColor Prefix! Not Registering these blocks" + blockTypes.toString());
                    return;
                }
            }

            map.put(DYE_VALUES[z], colorBlock);
        }

        map.put(null, defaultBlock);

        REGISTRY.put(blockTags, map);
    }

    public static void registerBlockSetUnsafe(List<Block> blockTypes, Block defaultBlock, Tag.Identified<Block> blockTags){
        if(blockTypes.size() > 16){
            loggerError("The Block List is greater than 17! Not Registering these blocks" + blockTypes.toString());
            return;
        }

        Map<DyeColor, Block> map = new HashMap<>();

        for(int z = 0; z < DYE_VALUES.length; z++){
            Block colorBlock = blockTypes.get(z);

            map.put(DYE_VALUES[z], colorBlock);
        }

        map.put(null, defaultBlock);

        REGISTRY.put(blockTags, map);
    }

    public static Block getVariant(Block block, DyeColor color) {
        var map = REGISTRY.keySet().stream().filter(blockTag -> blockTag.contains(block))
                .map(REGISTRY::get).findAny();

        if (map.isEmpty()) return null;
        return map.get().get(color);
    }

    //-----------------------------------------------------------------------//

    private static String getDyeColorFromBlock(Block block){
        return getDyeColorFromIdentifier(Registry.BLOCK.getId(block));
    }

    private static String getDyeColorFromIdentifier(Identifier identifier) {
        String[] splitName = identifier.getPath().split("_");
        if (splitName.length >= 2) {
            return splitName[0].equals("light")  ? splitName[0] + "_" + splitName[1] : splitName[0];
        } else {
            return "";
        }
    }

    //-----------------------------------------------------------------------//

    @ApiStatus.Internal
    private static void loggerError(String message){
        LOGGER.error("[Color Block Registry] Error: " + message);
    }


}
