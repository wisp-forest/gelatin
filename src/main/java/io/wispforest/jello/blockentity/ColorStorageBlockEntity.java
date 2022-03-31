package io.wispforest.jello.blockentity;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
import io.wispforest.owo.ops.WorldOps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public class ColorStorageBlockEntity extends BlockEntity {

    private DyeColorant currentColor;

    private static final String DYE_COLOR_KEY = "DyeColor";

    public ColorStorageBlockEntity(BlockPos pos, BlockState state) {
        super(JelloBlockEntityTypes.COLOR_STORAGE, pos, state);
        this.currentColor = DyeColorantRegistry.NULL_VALUE_NEW;
    }

    public void setDyeColorant(DyeColorant dyeColorant) {
        this.currentColor = dyeColorant;
    }

    public DyeColorant getDyeColorant() {
        return this.currentColor;
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        var tag = new NbtCompound();
        this.writeNbt(tag);
        return tag;
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        nbt.putString(DYE_COLOR_KEY, currentColor.getId().toString());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Identifier id = Identifier.tryParse(nbt.getString(DYE_COLOR_KEY));

        this.currentColor = DyeColorantRegistry.DYE_COLOR.get(id);

        if (world != null && world.isClient) {
            world.updateListeners(pos, Blocks.AIR.getDefaultState(), this.getCachedState(), Block.REDRAW_ON_MAIN_THREAD);
            //MinecraftClient.getInstance().worldRenderer.updateBlock(world, pos,  Blocks.AIR.getDefaultState(), this.getCachedState(), Block.REDRAW_ON_MAIN_THREAD);
        }
    }

    @Override
    public void markDirty() {
        super.markDirty();
        WorldOps.updateIfOnServer(world, this.getPos());
    }

    public static boolean isWaterColored(ColorStorageBlockEntity blockEntity) {
        return blockEntity.getDyeColorant() != DyeColorantRegistry.NULL_VALUE_NEW;
    }
}
