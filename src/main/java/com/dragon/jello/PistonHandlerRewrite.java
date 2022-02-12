//package com.dragon.jello;
//
//import com.dragon.jello.blocks.DyeableBlock;
//import com.dragon.jello.tags.JelloBlockTags;
//import com.google.common.collect.Lists;
//import java.util.List;
//
//import net.minecraft.block.*;
//import net.minecraft.block.enums.SlabType;
//import net.minecraft.block.piston.PistonBehavior;
//import net.minecraft.block.piston.PistonHandler;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.math.Direction;
//import net.minecraft.world.World;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//public class PistonHandlerRewrite extends PistonHandler {
//
//    private final Logger LOGGER = LogManager.getLogger("PISTON_DEBUG");
//    private int debugNumber = 1;
//
//    public static final int MAX_MOVABLE_BLOCKS = 128;// 12;
//    private final World world;
//    private final BlockPos posFrom;
//    private final boolean retracted;
//    private final BlockPos posTo;
//    private final Direction motionDirection;
//    private final List<BlockPos> movedBlocks = Lists.<BlockPos>newArrayList();
//    private final List<BlockPos> brokenBlocks = Lists.<BlockPos>newArrayList();
//    private final Direction pistonDirection;
//
//    public PistonHandlerRewrite(World world, BlockPos pos, Direction dir, boolean retracted) {
//        super(world, pos, dir, retracted);
//        this.world = world;
//        this.posFrom = pos;
//        this.pistonDirection = dir;
//        this.retracted = retracted;
//        if (retracted) {
//            this.motionDirection = dir;
//            this.posTo = pos.offset(dir);
//        } else {
//            this.motionDirection = dir.getOpposite();
//            this.posTo = pos.offset(dir, 2);
//        }
//
//    }
//    @Override
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
//                loggerWithChecks("CULLING A BLOCK");
//                return true;
//            }
//            if((motionDirection == Direction.UP && getPistonDirection() == Direction.DOWN)  && blockState.get(SlabBlock.TYPE) == SlabType.BOTTOM){
//                loggerWithChecks("CULLING A BLOCK");
//                return true;
//            }
//        }
//        //--------------------------/\--/\--/\------------------------------//
//
//        loggerWithChecks("Piston Cycle Start!");
//
//        if (!this.tryMove(this.posTo, this.motionDirection)) { //else if (!this.tryMove(this.posTo, this.motionDirection)) {
//            loggerWithChecks("Piston Cycle END: " + "Failed as it was unable to move the block above the piston head" + "[" + "MotionDir: " + motionDirection + "]");
//            return false;
//        } else {
//            debugNumber = 2; //I created
//            for(int i = 0; i < this.movedBlocks.size(); ++i) {
//                BlockPos blockPos = (BlockPos)this.movedBlocks.get(i);
//                if (isBlockSticky(this.world.getBlockState(blockPos)) && !this.tryMoveAdjacentBlock(blockPos)) {
//                    loggerWithChecks("Piston Cycle END: " + "Failed as it was unable to move the block above the piston head" + "[" + "MotionDir: " + motionDirection + "]");
//                    return false;
//                }
//            }
//
//            loggerWithChecks("Piston Cycle END: Success");
//            loggerWithChecks("");
//            return true;
//        }
//    }
//
//    private static boolean isBlockSticky(BlockState state) {
//        //--------------------------\/--\/--\/------------------------------\\
//        return state.isIn(JelloBlockTags.STICKY_BLOCKS);
//        //--------------------------/\--/\--/\------------------------------//
//        //return state.isOf(Blocks.SLIME_BLOCK) || state.isOf(Blocks.HONEY_BLOCK);
//    }
//
//    private static boolean isAdjacentBlockStuck(BlockState state, BlockState adjacentState) {
//        if(state.getBlock() instanceof DyeableBlock dyeableBlock1 && adjacentState.getBlock() instanceof DyeableBlock dyeableBlock2){
//            return dyeableBlock1.getDyeColor() == dyeableBlock2.getDyeColor();
//        }else if(isCustomSlimeBlock(state) && isSlimeOrHoneyBlock(adjacentState)){
//            return false;
//        }else if(isSlimeOrHoneyBlock(state) && isCustomSlimeBlock(adjacentState)){
//            return false;
//        }
//        //--------------------------\/--\/--\/------------------------------\\
//        if (state.isOf(Blocks.HONEY_BLOCK) && adjacentState.isOf(Blocks.SLIME_BLOCK)) {
//            return false;
//        } else if (state.isOf(Blocks.SLIME_BLOCK) && adjacentState.isOf(Blocks.HONEY_BLOCK)) {
//            return false;
//        } else {
//            return isBlockSticky(state) || isBlockSticky(adjacentState);
//        }
//        //--------------------------/\--/\--/\------------------------------//
//    }
//
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
//            int i = 1; loggerWithChecks("Checking Direction: " + dir);
//            loggerWithChecks(String.format("[%d]:", 1) + "Checking block at: [" + pos + ", " + blockState + "] " + debugNumber);
//            if (i + this.movedBlocks.size() > MAX_MOVABLE_BLOCKS) {
//                return false;
//            } else {
//                while(isBlockSticky(blockState)) {
//                    BlockPos blockPos = pos.offset(this.motionDirection.getOpposite(), i);
//                    BlockState blockState2 = blockState;
//                    blockState = this.world.getBlockState(blockPos);
//                    loggerWithChecks(String.format("[%d]:", i + 1) + "Checking block at: [" + blockPos + ", " + blockState + "] " + debugNumber);
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
//                            loggerWithChecks("CULLING A BLOCK");
//                            break;
//                        }
//                        if(motionDirection == Direction.UP && blockState2.get(SlabBlock.TYPE) == SlabType.TOP){
//                            loggerWithChecks("CULLING A BLOCK");
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
//                    if (i + this.movedBlocks.size() > MAX_MOVABLE_BLOCKS) {
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
//                    if (this.movedBlocks.size() >= MAX_MOVABLE_BLOCKS) {
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
//
//    private void setMovedBlocks(int from, int to) {
//        List<BlockPos> list = Lists.<BlockPos>newArrayList();
//        List<BlockPos> list2 = Lists.<BlockPos>newArrayList();
//        List<BlockPos> list3 = Lists.<BlockPos>newArrayList();
//        list.addAll(this.movedBlocks.subList(0, to));
//        list2.addAll(this.movedBlocks.subList(this.movedBlocks.size() - from, this.movedBlocks.size()));
//        list3.addAll(this.movedBlocks.subList(to, this.movedBlocks.size() - from));
//        this.movedBlocks.clear();
//        this.movedBlocks.addAll(list);
//        this.movedBlocks.addAll(list2);
//        this.movedBlocks.addAll(list3);
//    }
//
//    // [>= 2 means North, South, West, East : < 2 means Down, Up]
//    private boolean tryMoveAdjacentBlock(BlockPos pos) {
//        BlockState blockState = this.world.getBlockState(pos);
//
//        for(Direction direction : Direction.values()) {
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
//                            //loggerWithChecks("1: Test :1");
//                            continue;
//                        } else if (slabType == SlabType.BOTTOM && slabType2 == SlabType.TOP) {
//                            //loggerWithChecks("2: Test :2");
//                            continue;
//                        }
//                    }
//
//                    if(direction == Direction.DOWN && slabType2 == SlabType.BOTTOM){
//                        //loggerWithChecks("3: Test :3");
//                        continue;
//                    }
//
//                    if(direction == Direction.UP && slabType2 == SlabType.TOP){
//                        loggerWithChecks("4: Test :4");
//                        continue;
//                    }
//                }
//                loggerWithChecks(String.format("      [%d]->Sub Block Check", direction.getId()) + ": [" + blockPos + ", " + blockState2 + "] " + debugNumber);
//                //--------------------------/\--/\--/\------------------------------//
//                if (isAdjacentBlockStuck(blockState2, blockState) && !this.tryMove(blockPos, direction)) {
//                    return false;
//                }
//            }
//        }
//
//        return true;
//    }
//
//    public Direction getMotionDirection() {
//        return this.motionDirection;
//    }
//
//    public List<BlockPos> getMovedBlocks() {
//        return this.movedBlocks;
//    }
//
//    public List<BlockPos> getBrokenBlocks() {
//        return this.brokenBlocks;
//    }
//
//    //--------------------------\/--\/--\/------------------------------\\
//
//    private static boolean isSlimeOrHoneyBlock(BlockState state){
//        return state.isOf(Blocks.HONEY_BLOCK) || state.isOf(Blocks.SLIME_BLOCK);
//    }
//
//    private static boolean isCustomSlimeBlock(BlockState state){
//        return state.isIn(JelloBlockTags.COLORED_SLIME_BLOCKS);
//    }
//
//    private void loggerWithChecks(String string){
//        if(!world.isClient && motionDirection.getId() >= 2){
//            LOGGER.info(string);
//        }
//    }
//
//    private Direction getPistonDirection(){
//        return world.getBlockState(posFrom).get(FacingBlock.FACING);
//    }
//
//    //--------------------------/\--/\--/\------------------------------//
//}
