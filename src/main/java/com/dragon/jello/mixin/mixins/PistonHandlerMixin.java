package com.dragon.jello.mixin.mixins;

import com.dragon.jello.blocks.DyeableBlock;
import com.dragon.jello.tags.JelloBlockTags;
import net.minecraft.block.*;
import net.minecraft.block.enums.SlabType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

/**
 * Dragon_Seeker:
 * This really needs some optimization and fixes for how bad this code is as I have very little clue what the hell im doing
 */

@Mixin(value = PistonHandler.class, priority = 100)
public abstract class PistonHandlerMixin {

    @Unique private Logger LOGGER = LogManager.getLogger("PISTON_TEST");

    @Unique private boolean setToAir = false;
    @Unique private static final Direction[] VALUES = Direction.values(); //Taken from the PistonHandlerMixin from Lithium due to Incompatibility issue

    @Shadow @Final private World world;
    @Shadow @Final private Direction motionDirection;
    @Shadow @Final private BlockPos posFrom;

    @Shadow protected abstract boolean tryMove(BlockPos pos, Direction dir);
    @Shadow private static boolean isAdjacentBlockStuck(BlockState state, BlockState adjacentState) {
        return false;
    }

    //----------------------------------------------------------------------------//

    @Shadow @Final private List<BlockPos> movedBlocks;

    @Shadow
    protected static boolean isBlockSticky(BlockState state) {
        return false;
    }

    @Shadow protected abstract void setMovedBlocks(int from, int to);

    @Shadow @Final private List<BlockPos> brokenBlocks;

    @Shadow @Final private BlockPos posTo;

    @Shadow @Final private boolean retracted;

    @Shadow @Final private Direction pistonDirection;

    @Inject(method = "isBlockSticky", at = @At(value = "HEAD"), cancellable = true)
    private static void isBlockStickyExt(BlockState state, CallbackInfoReturnable<Boolean> cir){
        cir.setReturnValue(state.isIn(JelloBlockTags.STICKY_BLOCKS));
    }

    //----------------------------------------------------------------------------//

    @Inject(method = "isAdjacentBlockStuck", at = @At(value = "HEAD"), cancellable = true)
    private static void isAdjacentBlockStuckExt(BlockState state, BlockState adjacentState, CallbackInfoReturnable<Boolean> cir){
        if(state.getBlock() instanceof DyeableBlock dyeableBlock1 && adjacentState.getBlock() instanceof DyeableBlock dyeableBlock2){
            cir.setReturnValue(dyeableBlock1.getDyeColor() == dyeableBlock2.getDyeColor());
        }else if(isCustomSlimeBlock(state) && isSlimeOrHoneyBlock(adjacentState)){
            cir.setReturnValue(false);
        }else if(isSlimeOrHoneyBlock(state) && isCustomSlimeBlock(adjacentState)){
            cir.setReturnValue(false);
        }
    }

    @Unique
    private static boolean isSlimeOrHoneyBlock(BlockState state){
        return state.isOf(Blocks.HONEY_BLOCK) || state.isOf(Blocks.SLIME_BLOCK);
    }

    @Unique
    private static boolean isCustomSlimeBlock(BlockState state){
        return state.isIn(JelloBlockTags.COLORED_SLIME_BLOCKS);
    }

    //----------------------------------------------------------------------------//

//    /**
//     * @author
//     */
//    @Overwrite
//    public boolean calculatePush() {
//        this.movedBlocks.clear();
//        this.brokenBlocks.clear();
//        BlockState blockState = this.world.getBlockState(this.posTo);
//        if (!PistonBlock.isMovable(blockState, this.world, this.posTo, this.motionDirection, false, this.pistonDirection)) {
//            if (this.retracted && blockState.getPistonBehavior() == PistonBehavior.DESTROY) {
//                this.brokenBlocks.add(this.posTo);
//                return true;
//            } else {
//                return false;
//            }
//        }
//
//        //--------------------------\/--\/--\/------------------------------\\
//        if(blockState.isIn(JelloBlockTags.COLORED_SLIME_SLABS)){
//            if((motionDirection == Direction.DOWN && getPistonDirection() == Direction.UP) && blockState.get(SlabBlock.TYPE) == SlabType.TOP){
//                //loggerWithChecks("CULLING A BLOCK");
//                return true;
//            }
//            if((motionDirection == Direction.UP && getPistonDirection() == Direction.DOWN)  && blockState.get(SlabBlock.TYPE) == SlabType.BOTTOM){
//                //loggerWithChecks("CULLING A BLOCK");
//                return true;
//            }
//        }
//        //--------------------------/\--/\--/\------------------------------//
//
//        //loggerWithChecks("Piston Cycle Start!");
//
//        if (!this.tryMove(this.posTo, this.motionDirection)) { //else if (!this.tryMove(this.posTo, this.motionDirection)) {
//            //loggerWithChecks("Piston Cycle END: " + "Failed as it was unable to move the block above the piston head" + "[" + "MotionDir: " + motionDirection + "]");
//            return false;
//        } else {
//            //debugNumber = 2; //I created
//            for(int i = 0; i < this.movedBlocks.size(); ++i) {
//                BlockPos blockPos = (BlockPos)this.movedBlocks.get(i);
//                if (isBlockSticky(this.world.getBlockState(blockPos)) && !this.tryMoveAdjacentBlock(blockPos)) {
//                    //loggerWithChecks("Piston Cycle END: " + "Failed as it was unable to move the block above the piston head" + "[" + "MotionDir: " + motionDirection + "]");
//                    return false;
//                }
//            }
//
//            //loggerWithChecks("Piston Cycle END: Success");
//            //loggerWithChecks("");
//            return true;
//        }
//    }

    //----------------------------------------------------------------------------//

    @Inject(method = "calculatePush", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;", shift = At.Shift.BY, by = 2, ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void firstBlockCulling(CallbackInfoReturnable<Boolean> cir, BlockState blockState){
        if(blockState.isIn(JelloBlockTags.COLORED_SLIME_SLABS)){
            if((motionDirection == Direction.DOWN && getPistonDirection() == Direction.UP) && blockState.get(SlabBlock.TYPE) == SlabType.TOP){
                cir.setReturnValue(true);
            }
            if((motionDirection == Direction.UP && getPistonDirection() == Direction.DOWN)  && blockState.get(SlabBlock.TYPE) == SlabType.BOTTOM){
                cir.setReturnValue(true);
            }
        }
    }

    @Unique
    private Direction getPistonDirection(){
        return world.getBlockState(posFrom).get(FacingBlock.FACING);
    }

    //----------------------------------------------------------------------------//

    @Inject(method = "tryMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;", ordinal = 1, shift = At.Shift.BY, by = 2), locals = LocalCapture.CAPTURE_FAILHARD)
    private void secondBlockCulling(BlockPos pos, Direction dir, CallbackInfoReturnable<Boolean> cir, BlockState blockState, int i, BlockPos blockPos){
        //Used as a fix for some weird mixin problem with doing and an inject and modify variable in this way
        //It breaks the local capture of the blockstate within the OG equation
        BlockPos blockPos2 = pos.offset(this.motionDirection.getOpposite(), i - 1);
        BlockState blockState2 = world.getBlockState(blockPos2);

        setToAir = false;

        //--------------------------\/--\/--\/------------------------------\\
        if(blockState2.isIn(JelloBlockTags.COLORED_SLIME_SLABS)){
            if(motionDirection == Direction.DOWN && blockState2.get(SlabBlock.TYPE) == SlabType.BOTTOM){
                setToAir = true;
            }
            if(motionDirection == Direction.UP && blockState2.get(SlabBlock.TYPE) == SlabType.TOP){
                setToAir = true;
            }

            if(blockState.isIn(JelloBlockTags.COLORED_SLIME_SLABS)){
                if(motionDirection.getId() >= 2) {
                    if (blockState2.get(SlabBlock.TYPE) == SlabType.BOTTOM && blockState.get(SlabBlock.TYPE) == SlabType.TOP) {
                        setToAir = true;
                    }
                    if (blockState2.get(SlabBlock.TYPE) == SlabType.TOP && blockState.get(SlabBlock.TYPE) == SlabType.BOTTOM) {
                        setToAir = true;
                    }
                }
                if(motionDirection == Direction.UP && blockState.get(SlabBlock.TYPE) == SlabType.BOTTOM){
                    setToAir = true;
                }
                if(motionDirection == Direction.DOWN && blockState.get(SlabBlock.TYPE) == SlabType.TOP){
                    setToAir = true;
                }
            }
        }


    }

    @ModifyVariable(method = "tryMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;", shift = At.Shift.BY, by = 2, ordinal = 1), ordinal = 0)
    private BlockState setBlockState(BlockState state){
        return setToAir ? Blocks.AIR.getDefaultState() : state;
    }

//    /**
//     * @author Dragon_Seeker
//     */
//    @Overwrite
//    private boolean tryMove(BlockPos pos, Direction dir) {
//        BlockState blockState = this.world.getBlockState(pos);
//        if (blockState.isAir()) {
//            return true;
//        } else if (!PistonBlock.isMovable(blockState, this.world, pos, this.motionDirection, false, dir)) {
//            return true;
//        } else if (pos.equals(this.posFrom)) {
//            return true;
//        } else if (this.movedBlocks.contains(pos)) {
//            return true;
//        } else {
//            int i = 1; //loggerWithChecks("Checking Direction: " + dir);
//            //loggerWithChecks(String.format("[%d]:", 1) + "Checking block at: [" + pos + ", " + blockState + "] " + debugNumber);
//            if (i + this.movedBlocks.size() > 12) {
//                return false;
//            } else {
//                while(isBlockSticky(blockState)) {
//                    BlockPos blockPos = pos.offset(this.motionDirection.getOpposite(), i);
//                    BlockState blockState2 = blockState;
//                    blockState = this.world.getBlockState(blockPos);
//                    //loggerWithChecks(String.format("[%d]:", i + 1) + "Checking block at: [" + blockPos + ", " + blockState + "] " + debugNumber);
//                    if (blockState.isAir()
//                            || !isAdjacentBlockStuck(blockState2, blockState)
//                            || !PistonBlock.isMovable(blockState, this.world, blockPos, this.motionDirection, false, this.motionDirection.getOpposite())
//                            || blockPos.equals(this.posFrom)) {
//                        break;
//                    }
//
//                    //--------------------------\/--\/--\/------------------------------\\
//                    if(blockState2.isIn(JelloBlockTags.COLORED_SLIME_SLABS)){
//                        if(motionDirection == Direction.DOWN && blockState2.get(SlabBlock.TYPE) == SlabType.BOTTOM){
//                            //loggerWithChecks("CULLING A BLOCK");
//                            break;
//                        }
//                        if(motionDirection == Direction.UP && blockState2.get(SlabBlock.TYPE) == SlabType.TOP){
//                            //loggerWithChecks("CULLING A BLOCK");
//                            break;
//                        }
//
//                        if(blockState.isIn(JelloBlockTags.COLORED_SLIME_SLABS)){
//                            if(motionDirection.getId() >= 2) {
//                                if (blockState2.get(SlabBlock.TYPE) == SlabType.BOTTOM && blockState.get(SlabBlock.TYPE) == SlabType.TOP) {
//                                    break;
//                                }
//                                if (blockState2.get(SlabBlock.TYPE) == SlabType.TOP && blockState.get(SlabBlock.TYPE) == SlabType.BOTTOM) {
//                                    break;
//                                }
//                            }
//                            if(motionDirection == Direction.UP && blockState.get(SlabBlock.TYPE) == SlabType.BOTTOM){
//                                break;
//                            }
//                            if(motionDirection == Direction.DOWN && blockState.get(SlabBlock.TYPE) == SlabType.TOP){
//                                break;
//                            }
//                        }
//                    }
//                    //--------------------------/\--/\--/\------------------------------//
//
//                    ++i;
//                    if (i + this.movedBlocks.size() > 12) {
//                        return false;
//                    }
//                }
//
//                int j = 0;
//
//                for(int k = i - 1; k >= 0; --k) {
//                    this.movedBlocks.add(pos.offset(this.motionDirection.getOpposite(), k));
//                    ++j;
//                }
//
//                int k = 1;
//
//                while(true) {
//                    BlockPos blockPos2 = pos.offset(this.motionDirection, k);
//                    int l = this.movedBlocks.indexOf(blockPos2);
//                    if (l > -1) {
//                        this.setMovedBlocks(j, l);
//
//                        for(int m = 0; m <= l + j; ++m) {
//                            BlockPos blockPos3 = (BlockPos)this.movedBlocks.get(m);
//                            if (isBlockSticky(this.world.getBlockState(blockPos3)) && !this.tryMoveAdjacentBlock(blockPos3)) {
//                                return false;
//                            }
//                        }
//
//                        return true;
//                    }
//
//                    blockState = this.world.getBlockState(blockPos2);
//                    if (blockState.isAir()) {
//                        return true;
//                    }
//
//                    if (!PistonBlock.isMovable(blockState, this.world, blockPos2, this.motionDirection, true, this.motionDirection) || blockPos2.equals(this.posFrom)) {
//                        return false;
//                    }
//
//                    if (blockState.getPistonBehavior() == PistonBehavior.DESTROY) {
//                        this.brokenBlocks.add(blockPos2);
//                        return true;
//                    }
//
//                    if (this.movedBlocks.size() >= 12) {
//                        return false;
//                    }
//
//                    this.movedBlocks.add(blockPos2);
//                    ++j;
//                    ++k;
//                }
//            }
//        }
//    }

    //----------------------------------------------------------------------------//

    ThreadLocal<SlabType> slabType = ThreadLocal.withInitial(() -> null);
    ThreadLocal<SlabType> slabType2 = ThreadLocal.withInitial(() -> null);

    /**
     * @author dragon_seeker
     *
     * @reason to make my mod comp with carpet
     */
    @Overwrite
    private boolean tryMoveAdjacentBlock(BlockPos pos) {
        BlockState blockState = this.world.getBlockState(pos);

        for(Direction direction : Direction.values()) {
            if (blockState.isIn(JelloBlockTags.COLORED_SLIME_SLABS)) {
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

                if (blockState2.isIn(JelloBlockTags.COLORED_SLIME_SLABS)) {
                    slabType2.set(blockState2.get(SlabBlock.TYPE));
                    if (direction.getId() >= 2) {
                        if (slabType.get() == SlabType.TOP && slabType2.get() == SlabType.BOTTOM) {
                            continue;
                        } else if (slabType.get() == SlabType.BOTTOM && slabType2.get() == SlabType.TOP) {
                            continue;
                        }
                    }

                    if(direction == Direction.DOWN && slabType2.get() == SlabType.BOTTOM){
                        continue;
                    }

                    if(direction == Direction.UP && slabType2.get() == SlabType.TOP){
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



//    //TODO: Bad solution to fix compatibility issue with lithium(Im sorry for this cursed code)
//    @Redirect(method = "calculatePush", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/piston/PistonHandler;tryMoveAdjacentBlock(Lnet/minecraft/util/math/BlockPos;)Z"))//, at = @At(value = "HEAD"))//, locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
//    private boolean useAdjustedTryMoveAdj1(PistonHandler instance, BlockPos pos) {
//        return tryMoveAdjacentBlock(pos);
//    }
//
//    @Redirect(method = "tryMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/piston/PistonHandler;tryMoveAdjacentBlock(Lnet/minecraft/util/math/BlockPos;)Z"))//, at = @At(value = "HEAD"))//, locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
//    private boolean useAdjustedTryMoveAdj2(PistonHandler instance, BlockPos pos) {
//        return tryMoveAdjacentBlock(pos);
//    }
//
//    @Unique
//    private boolean tryMoveAdjacentBlock(BlockPos pos) {
//        BlockState blockState = this.world.getBlockState(pos);
//
//        for(Direction direction : VALUES) {
//            //--------------------------\/--\/--\/------------------------------\\
//            SlabType slabType = null;
//            if (blockState.isIn(JelloBlockTags.COLORED_SLIME_SLABS)) {
//                slabType = blockState.get(SlabBlock.TYPE);
//
//                if (motionDirection.getId() >= 2) {
//                    if (slabType == SlabType.BOTTOM && direction == Direction.UP) {
//                        continue;
//                    } else if (slabType == SlabType.TOP && direction == Direction.DOWN) {
//                        continue;
//                    }
//                }
//            }
//            //--------------------------/\--/\--/\------------------------------//
//            if (direction.getAxis() != this.motionDirection.getAxis()) {
//                BlockPos blockPos = pos.offset(direction);
//                BlockState blockState2 = this.world.getBlockState(blockPos);
//                //--------------------------\/--\/--\/------------------------------\\
//                if (blockState2.isIn(JelloBlockTags.COLORED_SLIME_SLABS)) {
//                    SlabType slabType2 = blockState2.get(SlabBlock.TYPE);
//                    if (direction.getId() >= 2) {
//                        if (slabType == SlabType.TOP && slabType2 == SlabType.BOTTOM) {
//                            continue;
//                        } else if (slabType == SlabType.BOTTOM && slabType2 == SlabType.TOP) {
//                            continue;
//                        }
//                    }
//
//                    if(direction == Direction.DOWN && slabType2 == SlabType.BOTTOM){
//                        continue;
//                    }
//
//                    if(direction == Direction.UP && slabType2 == SlabType.TOP){
//                        continue;
//                    }
//                }
//                //--------------------------/\--/\--/\------------------------------//
//                if (isAdjacentBlockStuck(blockState2, blockState) && !this.tryMove(blockPos, direction)) {
//                    return false;
//                }
//            }
//        }
//
//        return true;
//    }

    //----------------------------------------------------------------------------//


}