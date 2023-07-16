package io.wispforest.gelatin.cauldron.blockentity;

import io.wispforest.gelatin.common.util.WorldFunctions;
import io.wispforest.gelatin.dye_registry.DyeColorant;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
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
import org.jetbrains.annotations.Nullable;

public class ColorStorageBlockEntity extends BlockEntity {

    private DyeColorant currentColor = DyeColorantRegistry.NULL_VALUE_NEW;

    private static final String DYE_COLOR_KEY = "DyeColor";

    public ColorStorageBlockEntity(BlockPos pos, BlockState state) {
        super(GelatinBlockEntityTypes.COLOR_STORAGE, pos, state);
    }

    public void setDyeColorant(DyeColorant dyeColorant) {
        if(dyeColorant == null) {
            this.currentColor = DyeColorantRegistry.NULL_VALUE_NEW;
        } else {
            this.currentColor = dyeColorant;
        }
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

        if(nbt.contains(DYE_COLOR_KEY)){
            Identifier id = Identifier.tryParse(nbt.getString(DYE_COLOR_KEY));

            this.currentColor = DyeColorantRegistry.DYE_COLOR.get(id);
        }

        if (world != null && world.isClient) {
            world.updateListeners(pos, Blocks.AIR.getDefaultState(), this.getCachedState(), Block.REDRAW_ON_MAIN_THREAD);
        }
    }

    @Override
    public void markDirty() {
        super.markDirty();
        WorldFunctions.updateIfOnServer(world, this.getPos());
    }

    public static boolean isWaterColored(ColorStorageBlockEntity blockEntity) {
        return blockEntity.getDyeColorant() != DyeColorantRegistry.NULL_VALUE_NEW;
    }
}
