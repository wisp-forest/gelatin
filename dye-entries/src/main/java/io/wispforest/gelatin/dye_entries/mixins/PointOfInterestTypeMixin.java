package io.wispforest.gelatin.dye_entries.mixins;

import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.world.poi.PointOfInterestType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Mixin(PointOfInterestType.class)
public abstract class PointOfInterestTypeMixin {

    @ModifyVariable(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/Set;copyOf(Ljava/util/Collection;)Ljava/util/Set;", shift = At.Shift.BY, by = 2), argsOnly = true)
    private Set<BlockState> gelatin$changeSetToMutable(Set<BlockState> value){
        Optional<BlockState> optionalBlockState = value.stream().findFirst();

        if(optionalBlockState.isPresent() && optionalBlockState.get().getBlock() instanceof BedBlock){
            return new HashSet<>(value);
        }

        return value;
    }
}
