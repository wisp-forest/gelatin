package com.dragon.jello.lib.registry;

import com.dragon.jello.dyelib.DyeColorRegistry;
import net.minecraft.block.Block;
import net.minecraft.tag.Tag;
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

    private static final Map<Tag.Identified<Block>, Map<DyeColorRegistry.DyeColor, Block>> REGISTRY = new HashMap<>();

    private static final Logger LOGGER = LogManager.getLogger(ColorBlockRegistry.class);

    private static final DyeColorRegistry.DyeColor[] VANILLA_DYES = DyeColorRegistry.VANILLA_DYES.toArray(new DyeColorRegistry.DyeColor[0]);

    public static void registerBlockSet(List<Block> blockTypes, Block defaultBlock, Tag.Identified<Block> blockTags){
        if(blockTypes.size() > 16){
            loggerError("The Block List is greater than 17! Not Registering these blocks" + blockTypes.toString());
            return;
        }

        Map<DyeColorRegistry.DyeColor, Block> map = new HashMap<>();

        for(int z = 0; z < VANILLA_DYES.length; z++){
            String currentDyeValueName = VANILLA_DYES[z].getName();

            Block colorBlock = blockTypes.get(z);

            if(getDyeColorFromBlock(colorBlock) == VANILLA_DYES[z].getName()){
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

            map.put(VANILLA_DYES[z], colorBlock);
        }

        map.put(null, defaultBlock);

        REGISTRY.put(blockTags, map);
    }

    public static void registerBlockSetUnsafe(List<Block> blockTypes, Block defaultBlock, Tag.Identified<Block> blockTags){
        if(blockTypes.size() > 16){
            loggerError("The Block List is greater than 17! Not Registering these blocks" + blockTypes.toString());
            return;
        }

        Map<DyeColorRegistry.DyeColor, Block> map = new HashMap<>();

        for(int z = 0; z < VANILLA_DYES.length; z++){
            Block colorBlock = blockTypes.get(z);

            map.put(VANILLA_DYES[z], colorBlock);
        }

        map.put(null, defaultBlock);

        REGISTRY.put(blockTags, map);
    }

    public static Block getVariant(Block block, DyeColorRegistry.DyeColor color) {
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
