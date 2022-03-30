package io.wispforest.jello.main.network;

import net.minecraft.util.math.BlockPos;

public record ColorMixerRequestPacket(BlockPos pos, Action action) {
    enum Action {
        GET_BUFFER, PICK_DYE
    }
}
