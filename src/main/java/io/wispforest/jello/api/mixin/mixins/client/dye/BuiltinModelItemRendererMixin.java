package io.wispforest.jello.api.mixin.mixins.client.dye;

import io.wispforest.jello.api.dye.DyeColorant;
import io.wispforest.jello.api.dye.registry.DyeColorRegistry;
import io.wispforest.jello.api.mixin.ducks.DyeBlockStorage;
import io.wispforest.jello.api.util.MessageUtil;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(BuiltinModelItemRenderer.class)
public class BuiltinModelItemRendererMixin {

    @Unique
    private static final MessageUtil MESSANGER = new MessageUtil("Shulker Item Render");

//    @ModifyVariable(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/entity/BlockEntityRenderDispatcher;renderEntity(Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;II)Z"))
//    private BlockEntity allowForCustomColoredShulkerEntitys(BlockEntity blockEntity){//, ItemStack stack){
//        Block block = ((BlockItem)stack.getItem()).getBlock();
//
//        if(block instanceof ShulkerBoxBlock && blockEntity == null){
//            DyeColorant dyeColorant = ((DyeBlockStorage)block).getDyeColor();
//
//            MESSANGER.infoMessage("A shulker block without a entity was about to be rendered: " + dyeColorant.getId().toString());
//
//            return new ShulkerBoxBlockEntity(DyeColorRegistry.NULL_VALUE_OLD, BlockPos.ORIGIN, block.getDefaultState());
//        }
//
//        return blockEntity;
//    }

    @ModifyVariable(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/ShulkerBoxBlock;getColor(Lnet/minecraft/item/Item;)Lnet/minecraft/util/DyeColor;", shift = At.Shift.BY, by = 2))
    private DyeColor allowForCustomColoredShulkerEntitys(DyeColor dyeColor){
        if(dyeColor == DyeColorRegistry.NULL_VALUE_OLD){
            MESSANGER.infoMessage("A shulker with the Old Null DyeColor was found!");

            return null;
        }

        return dyeColor;
    }
}
