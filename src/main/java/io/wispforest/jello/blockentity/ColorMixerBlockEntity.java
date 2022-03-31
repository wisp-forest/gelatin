package io.wispforest.jello.blockentity;

import io.wispforest.jello.client.render.screen.ColorMixerScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ColorMixerBlockEntity extends BlockEntity implements NamedScreenHandlerFactory {
    public int colorBuffer;

    private final PropertyDelegate properties = new PropertyDelegate() {
        @Override
        public int get(int index) {
            return colorBuffer;
        }

        @Override
        public void set(int index, int value) {
            colorBuffer = value;
        }

        @Override
        public int size() {
            return 1;
        }
    };

    public ColorMixerBlockEntity(BlockPos pos, BlockState state) {
        super(JelloBlockEntityTypes.COLOR_MIXER, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState blockState, ColorMixerBlockEntity mixerBlock) {
        if (mixerBlock.colorBuffer <= 0) {
            mixerBlock.colorBuffer = 255;
        } else {
            mixerBlock.colorBuffer -= 1;
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        if (nbt.contains("colorBuffer")) {
            colorBuffer = nbt.getInt("colorBuffer");
        } else {
            colorBuffer = 0;
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        nbt.putInt("colorBuffer", colorBuffer);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public Text getDisplayName() {
        return Text.of("Deetz nutz");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new ColorMixerScreenHandler(syncId, inv, ScreenHandlerContext.create(this.world, this.pos));
    }
}
