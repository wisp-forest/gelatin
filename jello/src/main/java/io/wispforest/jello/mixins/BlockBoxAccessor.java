package io.wispforest.jello.mixins;

import net.minecraft.util.math.BlockBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockBox.class)
public interface BlockBoxAccessor {
    @Accessor("minX") void jello$setMinX(int minX);
    @Accessor("minY") void jello$setMinY(int minY);
    @Accessor("minZ") void jello$setMinZ(int minZ);
    @Accessor("maxX") void jello$setMaxX(int maxX);
    @Accessor("maxY") void jello$setMaxY(int maxY);
    @Accessor("maxZ") void jello$setMaxZ(int maxZ);
}
