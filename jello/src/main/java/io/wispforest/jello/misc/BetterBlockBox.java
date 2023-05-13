package io.wispforest.jello.misc;

import io.wispforest.jello.mixins.BlockBoxAccessor;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class BetterBlockBox extends BlockBox {

    public BetterBlockBox(BlockPos pos) {
        super(pos);
    }

    public BetterBlockBox(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        super(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public static BetterBlockBox create(Vec3i first, Vec3i second) {
        return new BetterBlockBox(
                Math.min(first.getX(), second.getX()),
                Math.min(first.getY(), second.getY()),
                Math.min(first.getZ(), second.getZ()),
                Math.max(first.getX(), second.getX()),
                Math.max(first.getY(), second.getY()),
                Math.max(first.getZ(), second.getZ())
        );
    }

    public void expandMaxPoint(Vec3i vec3i){
        expandMaxPoint(vec3i.getX(), vec3i.getY(), vec3i.getZ());
    }

    public void expandMaxPoint(int x, int y, int z){
        accessor().jello$setMaxX(getMaxX() + x);
        accessor().jello$setMaxY(getMaxY() + y);
        accessor().jello$setMaxZ(getMaxZ() + z);
    }

    public Vec3i getStartVec(){
        return new Vec3i(getMinX(), getMinY(), getMinZ());
    }

    public Vec3i getEndVec(){
        return new Vec3i(getMaxX(), getMaxY(), getMaxZ());
    }

    public BlockPos getStartPos(){
        return new BlockPos(getMinX(), getMinY(), getMinZ());
    }

    public BlockPos getEndPos(){
        return new BlockPos(getMaxX(), getMaxY(), getMaxZ());
    }

    protected BlockBoxAccessor accessor(){
        return (BlockBoxAccessor) this;
    }
}
