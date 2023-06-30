package io.wispforest.jello.misc;

import com.mojang.logging.LogUtils;
import io.wispforest.gelatin.dye_entries.variants.impl.VanillaBlockVariants;
import io.wispforest.gelatin.dye_registry.DyeColorant;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import io.wispforest.owo.ui.core.Color;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ColorDebugHelper {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static ColorDebugHelper INSTANCE = new ColorDebugHelper();

    public Map<Vec3i, ColorDataStorage> defaultColorDataStorageMap = new LinkedHashMap<>();

    public int cubeLength = 0;

    public Map<Vec3i, List<Vec3i>> groupedColorMap = new LinkedHashMap<>();
    public Map<Vec3i, ColorDataStorage> filterdColorDataStorageMap = new LinkedHashMap<>();

    public int maxGroupingSize = 1;

    public BlockPos workSpacePoint = new BlockPos(0, 0 ,0);

    public BetterBlockBox workSpace = new BetterBlockBox(0,0,0,0,0,0);

    //------------------------

    public World world;

    //------------------------

    public ColorDebugHelper(){}

    public void computeData(int cubeLength, int maxGroupingSize){
        defaultColorDataStorageMap.clear();
        filterdColorDataStorageMap.clear();
        groupedColorMap.clear();

        buildColorPositionMap();

        this.cubeLength = cubeLength;
        this.maxGroupingSize = maxGroupingSize;

        basicFilterColors(256);
    }

    public void buildColorPositionMap(){
        for(DyeColorant dyeColorant : DyeColorantRegistry.getAllColorants()) {
            Block woolBlock = VanillaBlockVariants.WOOL.getColoredEntry(dyeColorant);

            if (woolBlock == Blocks.AIR) continue;

            Color color = Color.ofRgb(dyeColorant.getBaseColor());

            int x = Math.round(color.red() * 255);
            int y = Math.round(color.green() * 255);
            int z = Math.round(color.blue() * 255);

            Vec3i rgbValues = new Vec3i(x, y, z);

            defaultColorDataStorageMap.put(rgbValues, new ColorDataStorage(dyeColorant, color, woolBlock));
        }
    }

    public void basicFilterColors(int maxColorValue){
        if(cubeLength <= 1) {
            LOGGER.error("[ColorDebugBuilder]: Cube Length within filter Colors was shorter than or equal to 1 which isn't allowed!");

            return;
        }

        int totalCubes = MathHelper.ceil(maxColorValue / (float) cubeLength);

        loopThruXYZ(0,0,0, totalCubes * cubeLength, cubeLength, cubePos -> {
            int xEnd = Math.min(cubePos.getX() + cubeLength, maxColorValue + 1);
            int yEnd = Math.min(cubePos.getY() + cubeLength, maxColorValue + 1);
            int zEnd = Math.min(cubePos.getZ() + cubeLength, maxColorValue + 1);


            loopThruXYZ(cubePos.getX(), cubePos.getY(), cubePos.getZ(), xEnd, yEnd, zEnd, 1, innerCubePos -> {
                if(defaultColorDataStorageMap.containsKey(innerCubePos)){
                    List<Vec3i> vecs = groupedColorMap.computeIfAbsent(cubePos, (vec3i) -> new ArrayList<>());

                    vecs.add(innerCubePos);
                }
            });
        });

        groupedColorMap.forEach((key, value) -> {
            Vec3i filteredVec3i;

            if(value.size() == 0) return;

            if (value.size() > maxGroupingSize) {
                double biggestDistance = 0;
                Vec3i currentFurthest = null;

                for (Vec3i vec3i : value) {
                    double currentDistance = key.getSquaredDistance(vec3i);

                    if (currentDistance > biggestDistance) currentFurthest = vec3i;
                }

                filteredVec3i = currentFurthest;
            } else {
                filteredVec3i = value.get(0);
            }

            if (filteredVec3i == null) {
                LOGGER.error("[ColorDebugBuilder]: It seems that something has gone wrong in the Filter Stage of the Builder and filteredVec3i was somehow NULL, such will be skipped!");
            } else {
                filterdColorDataStorageMap.put(filteredVec3i, defaultColorDataStorageMap.get(filteredVec3i));
            }
        });
    }

    //------------------------------------------

    public void runBuilderProgram(BlockPos position, World world, int cubeLength, int maxGroupingSize){
        computeData(cubeLength, maxGroupingSize);

        this.world = world;

        clearLastWorkspace(world);

        workSpacePoint = position.add(1, 0, 1);
        workSpace = BetterBlockBox.create(new BlockPos(0,0,0), new BlockPos(257, 257, 257));

        placeColorBlocks();
        placeGroupedColors(256, false);
    }

    public void clearLastWorkspace(World world) {
        for (BlockPos pos : BlockPos.iterate(workSpace.getStartPos(), workSpace.getEndPos())) {
            BlockPos offsetPos = workSpacePoint.add(pos);

            if(!world.getBlockState(offsetPos).isAir()) world.setBlockState(offsetPos, Blocks.AIR.getDefaultState());
        }
    }

    public void outlineColorSpace(BlockPos start, BlockPos end, BlockPos offsetAmount){
        OutlineChecker checker = new OutlineChecker(0, 257);

        for(BlockPos pos : BlockPos.iterate(start, end)){
            if(!checker.isTwoNumbersInBound(pos.getX(), pos.getY(), pos.getZ())) continue;

            pos = pos.add(workSpacePoint).add(offsetAmount);

            world.setBlockState(pos, Blocks.TINTED_GLASS.getDefaultState());
        }
    }

    /*
     *  0 : 1832
     *
     *  5 : 1757
     *  7 : 1667
     *  9 : 1553
     * 11 : 1442
     * 13 : 1324
     * 15 : 1223
     * 17 : 1073
     * 19 :  932
     * 21 :  874
     * 23 :  772
     */
    public void placeColorBlocks(){
        outlineColorSpace(workSpace.getStartPos(), workSpace.getEndPos(), new BlockPos(0,0,0));

        BlockPos startingPos = workSpacePoint.add(new Vec3i(1,1,1));

        Map<Vec3i, ColorDataStorage> storageMap = !filterdColorDataStorageMap.isEmpty() ? filterdColorDataStorageMap : defaultColorDataStorageMap;

        int totalColorsPlaced = 0;

        for (Map.Entry<Vec3i, ColorDataStorage> entry : storageMap.entrySet()) {
            Block woolBlock = entry.getValue().block();

            BlockPos newPosition = startingPos.add(entry.getKey());

            world.setBlockState(newPosition, woolBlock.getDefaultState());

            totalColorsPlaced++;
        }

        LOGGER.info("Total Colors Placed: {}", totalColorsPlaced);
        LOGGER.info("Total Colors in Registry: {}", defaultColorDataStorageMap.size());
    }

    public void placeGroupedColors(int maxColorValue, boolean showMinSizedGroups){
        if(cubeLength <= 1) {
            LOGGER.error("[ColorDebugBuilder]: Cube Length within filter Colors was shorter than or equal to 1 which isn't allowed!");

            return;
        }

        Vec3i offsetValue = workSpace.getEndVec().add(-257,-257,2);

        Vec3i endingPos = new BlockPos(257, 257, 257);

        outlineColorSpace(new BlockPos(0,0,0), new BlockPos(endingPos), new BlockPos(offsetValue));

        Vec3i innerStartingPos = offsetValue.add(workSpacePoint).add(new Vec3i(1,1,1));

        for (Map.Entry<Vec3i, List<Vec3i>> entry : groupedColorMap.entrySet()) {
            List<Vec3i> colorVecs = entry.getValue();

            if(colorVecs.isEmpty() || (colorVecs.size() <= maxGroupingSize && !showMinSizedGroups)) continue;

            Vec3i cubePos = entry.getKey();

            int xEnd = Math.min(cubePos.getX() + cubeLength, maxColorValue + 1);
            int yEnd = Math.min(cubePos.getY() + cubeLength, maxColorValue + 1);
            int zEnd = Math.min(cubePos.getZ() + cubeLength, maxColorValue + 1);

            AtomicInteger blocksPlaced = new AtomicInteger(0);

            loopThruXYZ(cubePos.getX(), cubePos.getY(), cubePos.getZ(), xEnd, yEnd, zEnd, 1, innerCubePos -> {
                if(blocksPlaced.get() >= colorVecs.size()) return true;

                ColorDataStorage storage = defaultColorDataStorageMap.get(colorVecs.get(blocksPlaced.get()));

                world.setBlockState(new BlockPos(innerStartingPos.add(innerCubePos)), storage.block.getDefaultState());

                blocksPlaced.incrementAndGet();

                return false;
            });
        }

        workSpace.expandMaxPoint(endingPos.add(offsetValue));
    }

    //----------------------------------------------------------------

    protected static void loopThruXYZ(int xStart, int yStart, int zStart, int endingAmount, int incrementAmount, Consumer<Vec3i> consumer){
        loopThruXYZ(xStart, yStart, zStart, xStart + endingAmount, yStart + endingAmount, zStart + endingAmount, incrementAmount, consumer);
    }

    protected static void loopThruXYZ(int xStart, int yStart, int zStart, int xEnd, int yEnd, int zEnd, int incrementAmount, Consumer<Vec3i> consumer) {
        loopThruXYZ(xStart, yStart, zStart, xEnd, yEnd, zEnd, incrementAmount, (vec3i -> {
            consumer.accept(vec3i);

            return false;
        }));
    }

    protected static void loopThruXYZ(int xStart, int yStart, int zStart, int xEnd, int yEnd, int zEnd, int incrementAmount, Predicate<Vec3i> consumer){
        for(int x = xStart; x < xEnd; x += incrementAmount){
            for(int y = yStart; y < yEnd; y += incrementAmount){
                for(int z = zStart; z < zEnd; z += incrementAmount){
                    if(consumer.test(new Vec3i(x, y, z))) return;
                }
            }
        }
    }

    public record OutlineChecker(int min, int max) {
        private boolean isTwoNumbersInBound(int x, int y, int z) {
            return (atMaxOrMin(x) && atMaxOrMin(y))
                    || (atMaxOrMin(x) && atMaxOrMin(z))
                    || (atMaxOrMin(y) && atMaxOrMin(z));
        }

        private boolean atMaxOrMin(int number) {
            return number == min || number == max;
        }
    }

    public record ColorDataStorage(DyeColorant colorant, Color color, Block block){};
}
