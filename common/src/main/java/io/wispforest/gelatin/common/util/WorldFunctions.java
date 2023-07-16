package io.wispforest.gelatin.common.util;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldFunctions {

    /**
     * Copy of the owo Function: <a href="https://github.com/wisp-forest/owo-lib/blob/8560ec4b32d8752785d919ce8db1cc0c057c1b8a/src/main/java/io/wispforest/owo/ops/WorldOps.java#L93C39-L93C39"> Link </a>
     */
    public static void updateIfOnServer(World world, BlockPos pos) {
        if (!(world instanceof ServerWorld serverWorld)) return;
        serverWorld.getChunkManager().markForUpdate(pos);
    }
}
