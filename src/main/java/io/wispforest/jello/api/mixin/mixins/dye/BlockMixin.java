package io.wispforest.jello.api.mixin.mixins.dye;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.registry.DyeColorRegistry;
import io.wispforest.jello.api.mixin.ducks.DyeBlockStorage;
import io.wispforest.jello.main.common.data.tags.JelloTags;
import net.minecraft.block.*;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class BlockMixin extends AbstractBlock implements DyeBlockStorage {

    public BlockMixin(Settings settings) {
        super(settings);
    }

    @Shadow @Deprecated public abstract RegistryEntry.Reference<Block> getRegistryEntry();

//    @Inject(method = "<init>", at = @At(value = "TAIL"))
//    private void setMinecraftBlocks(AbstractBlock.Settings settings, CallbackInfo ci){
//        if(this instanceof Stainable stainable){
//            DyeColor dyeColor = stainable.getColor();
//
//            if(dyeColor != null) {
//                DyeColorant dyeColorant = DyeColorant.byOldDyeColor(dyeColor);
//
//                if (dyeColorant == null) {
//                    dyeColorant = DyeColorRegistry.DYE_COLOR.get(new Identifier(DyeColorRegistry.ENUM_NAMESPACE, dyeColor.getName()));
//                }
//
//                this.setDyeColor(dyeColorant);
//            }
//        }else if((Block)(Object)this instanceof DyedCarpetBlock dyedCarpetBlock){
//            DyeColor dyeColor = dyedCarpetBlock.getDyeColor();
//
//            if(dyeColor != null) {
//                DyeColorant dyeColorant = DyeColorant.byOldDyeColor(dyeColor);
//
//                if (dyeColorant == null) {
//                    dyeColorant = DyeColorRegistry.DYE_COLOR.get(new Identifier(DyeColorRegistry.ENUM_NAMESPACE, dyeColor.getName()));
//                }
//
//                this.setDyeColor(dyeColorant);
//            }
//        }else if(this.getRegistryEntry().isIn(BlockTags.WOOL)
////                || this.getRegistryEntry().isIn(JelloTags.Blocks.CONCRETE)
//                || (Block)(Object)this instanceof ConcretePowderBlock
//                || this.getRegistryEntry().isIn(BlockTags.SHULKER_BOXES)){
//
//            MapColor mapColor = this.getDefaultMapColor();
//
//            DyeColorant dyeColorant;
//
//            if(mapColor.id >= 15){
//                dyeColorant = DyeColorant.byOldIntId(mapColor.id - 14);
//            }else{
//                dyeColorant = DyeColorant.byOldIntId(0);
//            }
//
//            this.setDyeColor(dyeColorant);
//
//        }else if(this.getRegistryEntry().isIn(BlockTags.TERRACOTTA) && (Block)(Object)this != Blocks.TERRACOTTA){
//            MapColor mapColor = this.getDefaultMapColor();
//
//            DyeColorant dyeColorant;
//
//            dyeColorant = DyeColorant.byOldIntId(mapColor.id - 36);
//
//            this.setDyeColor(dyeColorant);
//        }else if((Block)(Object)this instanceof ShulkerBoxBlock shulkerBoxBlock){
//            DyeColor dyeColor = shulkerBoxBlock.getColor();
//
//            DyeColorant dyeColorant;
//            if(dyeColor != null){
//                dyeColorant = DyeColorant.byOldDyeColor(dyeColor);
//
//                this.setDyeColor(dyeColorant);
//            }
//        }
//    }

    private DyeColorant blockDyeColor = DyeColorRegistry.NULL_VALUE_NEW;

    @Override
    public void setDyeColor(DyeColorant dyeColorant) {
        this.blockDyeColor = dyeColorant;
    }

    @Override
    public DyeColorant getDyeColor() {
        return blockDyeColor;
    }

    @Override
    public boolean isBlockDyed() {
        return getDyeColor() != DyeColorRegistry.NULL_VALUE_NEW;
    }


}
