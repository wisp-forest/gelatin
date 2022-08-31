package io.wispforest.gelatin.dye_entries.mixins;

import io.wispforest.gelatin.dye_entries.block.ColoredBedBlock;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.BedPart;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(PointOfInterestTypes.class)
public class PointOfInterestTypesMixin {

//    @Shadow @Final @Mutable
//    private static Set<BlockState> BED_HEADS;
//
//    @Inject(method = "<clinit>", at = @At("TAIL"))
//    private static void gelatin$addCustomColoredBeds(CallbackInfo ci){
//        BED_HEADS = Registry.BLOCK.stream()
//            .filter(block -> block instanceof BedBlock)
//            .flatMap(block -> block.getStateManager().getStates().stream())
//            .filter(blockState -> blockState.get(BedBlock.PART) == BedPart.HEAD)
//            .collect(Collectors.toSet());
//    }

    @Inject(method = "getTypeForState", at = @At(value = "RETURN"), cancellable = true)
    private static void gelatin$checkForCustomBedStates(BlockState state, CallbackInfoReturnable<Optional<RegistryEntry<PointOfInterestType>>> cir){
        if(state.getBlock() instanceof ColoredBedBlock && state.get(BedBlock.PART) == BedPart.HEAD){
            cir.setReturnValue(Registry.POINT_OF_INTEREST_TYPE.getEntry(PointOfInterestTypes.HOME));
        }
    }

}
