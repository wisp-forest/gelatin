package io.wispforest.jello.mixins;

import io.wispforest.gelatin.dye_registry.DyeColorant;
import io.wispforest.gelatin.dye_registry.ducks.DyeBlockStorage;
import io.wispforest.jello.block.JelloBlocks;
import io.wispforest.jello.data.JelloTags;
import net.minecraft.block.*;
import net.minecraft.block.enums.SlabType;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = PistonHandler.class, priority = 100)
public abstract class PistonHandlerMixin {

    @Shadow
    @Final
    private World world;
    @Shadow
    @Final
    private Direction motionDirection;
    @Shadow
    @Final
    private BlockPos posFrom;

    @Shadow
    protected abstract boolean tryMove(BlockPos pos, Direction dir);

    @Shadow
    private static boolean isAdjacentBlockStuck(BlockState state, BlockState adjacentState) {
        return false;
    }

    //----------------------------------------------------------------------------//

    @Inject(method = "isBlockSticky", at = @At(value = "HEAD"), cancellable = true)
    private static void isBlockStickyExt(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(state.isIn(JelloTags.Blocks.STICKY_BLOCKS));
    }

    //----------------------------------------------------------------------------//

    @Inject(method = "isAdjacentBlockStuck", at = @At(value = "HEAD"), cancellable = true)
    private static void isAdjacentBlockStuckExt(BlockState state, BlockState adjacentState, CallbackInfoReturnable<Boolean> cir) {
        Block block1 = state.getBlock();
        Block block2 = adjacentState.getBlock();

        boolean bl1 = state.isIn(JelloTags.Blocks.STICKY_BLOCKS);
        boolean bl2 = adjacentState.isIn(JelloTags.Blocks.STICKY_BLOCKS);

        boolean returnValue = false;

        if(bl1 && bl2){
            DyeColorant dyeColorant1 = ((DyeBlockStorage)block1).getDyeColorant();
            DyeColorant dyeColorant2 = ((DyeBlockStorage)block2).getDyeColorant();

            if(block1 == block2){
                returnValue = true;
            } else if(dyeColorant1 == dyeColorant2){
                if(!(dyeColorant1.nullColorCheck() && (crossCompareToHoney(state, adjacentState) || crossCompareToHoney(adjacentState, state)))){
                    returnValue = true;
                }
            }
        } else if(bl1 || bl2) {
            returnValue = true;
        }

        cir.setReturnValue(returnValue);
    }

    private static boolean crossCompareToHoney(BlockState state, BlockState adjacentState){
        return state.isOf(Blocks.HONEY_BLOCK) && !adjacentState.isOf(Blocks.HONEY_BLOCK);
    }

    @Unique
    private static boolean isSlimeOrHoneyBlock(BlockState state) {
        return state.isOf(Blocks.HONEY_BLOCK) || state.isOf(Blocks.SLIME_BLOCK) || state.isOf(JelloBlocks.SLIME_SLAB);
    }

    @Unique
    private static boolean isCustomSlimeBlock(BlockState state) {
        return state.isIn(JelloTags.Blocks.COLORED_SLIME_BLOCKS) || state.isIn(JelloTags.Blocks.COLORED_SLIME_SLABS);
    }

    @Unique
    private static boolean isDefaultStickyBlock(BlockState state, BlockState adjacentState) {
        return state.isOf(JelloBlocks.SLIME_SLAB) || state.isOf(Blocks.SLIME_BLOCK)
                && adjacentState.isOf(JelloBlocks.SLIME_SLAB) || adjacentState.isOf(Blocks.SLIME_BLOCK);
    }

    //----------------------------------------------------------------------------//

    @Inject(method = "calculatePush", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;", shift = At.Shift.BY, by = 2, ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void firstBlockCulling(CallbackInfoReturnable<Boolean> cir, BlockState blockState) {
        if (blockState.isIn(JelloTags.Blocks.SLIME_SLABS)) {
            if ((motionDirection == Direction.DOWN && getPistonDirection() == Direction.UP) && blockState.get(SlabBlock.TYPE) == SlabType.TOP) {
                cir.setReturnValue(true);
            }
            if ((motionDirection == Direction.UP && getPistonDirection() == Direction.DOWN) && blockState.get(SlabBlock.TYPE) == SlabType.BOTTOM) {
                cir.setReturnValue(true);
            }
        }
    }

    @Unique
    private Direction getPistonDirection() {
        return world.getBlockState(posFrom).get(FacingBlock.FACING);
    }

    //----------------------------------------------------------------------------//

    @Unique private boolean setToAir = false;

    @Inject(method = "tryMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;", ordinal = 1, shift = At.Shift.BY, by = 2), locals = LocalCapture.CAPTURE_FAILHARD)
    private void secondBlockCulling(BlockPos pos, Direction dir, CallbackInfoReturnable<Boolean> cir, BlockState blockState, int i, BlockPos blockPos) {
        //Used as a fix for some weird mixin problem with doing and an inject and modify variable in this way
        //It breaks the local capture of the blockstate within the OG equation
        BlockPos blockPos2 = pos.offset(this.motionDirection.getOpposite(), i - 1);
        BlockState blockState2 = world.getBlockState(blockPos2);

        setToAir = false;

        //--------------------------\/--\/--\/------------------------------\\
        if (blockState2.isIn(JelloTags.Blocks.SLIME_SLABS)) {
            if (motionDirection == Direction.DOWN && blockState2.get(SlabBlock.TYPE) == SlabType.BOTTOM) {
                setToAir = true;
            }
            if (motionDirection == Direction.UP && blockState2.get(SlabBlock.TYPE) == SlabType.TOP) {
                setToAir = true;
            }

            if (blockState.isIn(JelloTags.Blocks.SLIME_SLABS)) {
                if (motionDirection.getId() >= 2) {
                    if (blockState2.get(SlabBlock.TYPE) == SlabType.BOTTOM && blockState.get(SlabBlock.TYPE) == SlabType.TOP) {
                        setToAir = true;
                    }
                    if (blockState2.get(SlabBlock.TYPE) == SlabType.TOP && blockState.get(SlabBlock.TYPE) == SlabType.BOTTOM) {
                        setToAir = true;
                    }
                }
                if (motionDirection == Direction.UP && blockState.get(SlabBlock.TYPE) == SlabType.BOTTOM) {
                    setToAir = true;
                }
                if (motionDirection == Direction.DOWN && blockState.get(SlabBlock.TYPE) == SlabType.TOP) {
                    setToAir = true;
                }
            }
        }


    }

    @ModifyVariable(method = "tryMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;", shift = At.Shift.BY, by = 2, ordinal = 1), ordinal = 0)
    private BlockState setBlockState(BlockState state) {
        return setToAir ? Blocks.AIR.getDefaultState() : state;
    }

    //----------------------------------------------------------------------------//

    ThreadLocal<SlabType> slabType = ThreadLocal.withInitial(() -> null);
    ThreadLocal<SlabType> slabType2 = ThreadLocal.withInitial(() -> null);

    /**
     * @author dragon_seeker
     * @reason to make my main comp with carpet
     */
    @Overwrite
    private boolean tryMoveAdjacentBlock(BlockPos pos) {
        BlockState blockState = this.world.getBlockState(pos);

        for (Direction direction : Direction.values()) {
            if (blockState.isIn(JelloTags.Blocks.SLIME_SLABS)) {
                slabType.set(blockState.get(SlabBlock.TYPE));

                if (motionDirection.getId() >= 2) {
                    if (slabType.get() == SlabType.BOTTOM && direction == Direction.UP) {
                        continue;
                    } else if (slabType.get() == SlabType.TOP && direction == Direction.DOWN) {
                        continue;
                    }
                }
            }
            if (direction.getAxis() != this.motionDirection.getAxis()) {
                BlockPos blockPos = pos.offset(direction);
                BlockState blockState2 = this.world.getBlockState(blockPos);

                if (blockState2.isIn(JelloTags.Blocks.SLIME_SLABS)) {
                    slabType2.set(blockState2.get(SlabBlock.TYPE));
                    if (direction.getId() >= 2) {
                        if (slabType.get() == SlabType.TOP && slabType2.get() == SlabType.BOTTOM) {
                            continue;
                        } else if (slabType.get() == SlabType.BOTTOM && slabType2.get() == SlabType.TOP) {
                            continue;
                        }
                    }

                    if (direction == Direction.DOWN && slabType2.get() == SlabType.BOTTOM) {
                        continue;
                    }

                    if (direction == Direction.UP && slabType2.get() == SlabType.TOP) {
                        continue;
                    }
                }
                if (isAdjacentBlockStuck(blockState2, blockState) && !this.tryMove(blockPos, direction)) {
                    return false;
                }
            }
        }

        return true;
    }

    //----------------------------------------------------------------------------//


}