//package io.wispforest.jello.mixin.dye;
//
//import com.google.common.collect.Lists;
//import com.mojang.datafixers.util.Pair;
//import io.wispforest.jello.api.dye.DyeColorant;
//import io.wispforest.jello.api.dye.registry.DyeColorantRegistry;
//import io.wispforest.jello.api.dye.registry.DyedVariants;
//import io.wispforest.jello.misc.ducks.DyeBlockEntitiestorage;
//import io.wispforest.jello.misc.ducks.DyeBlockStorage;
//import net.minecraft.block.AbstractBannerBlock;
//import net.minecraft.block.BannerBlock;
//import net.minecraft.block.Block;
//import net.minecraft.block.BlockState;
//import net.minecraft.block.entity.*;
//import net.minecraft.item.BlockItem;
//import net.minecraft.item.ItemStack;
//import net.minecraft.nbt.NbtCompound;
//import net.minecraft.nbt.NbtList;
//import net.minecraft.text.Text;
//import net.minecraft.util.DyeColor;
//import net.minecraft.util.Identifier;
//import net.minecraft.util.Nameable;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.registry.Registry;
//import org.jetbrains.annotations.Nullable;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Overwrite;
//import org.spongepowered.asm.mixin.Shadow;
//import org.spongepowered.asm.mixin.injection.Inject;
//
//import java.util.List;
//
//@Mixin(BannerBlockEntity.class)
//public class BannerBlockEntityMixin extends BlockEntity implements DyeBlockEntitiestorage {
//
//    @Shadow private DyeColor baseColor;
//    @Shadow private @Nullable NbtList patternListNbt;
//    @Shadow private @Nullable Text customName;
//    @Shadow private @Nullable List<Pair<BannerPattern, DyeColor>> patterns;
//
//    private DyeColorant jello$baseColor;
//    @Nullable
//    private NbtList jello$patternListNbt;
//    @Nullable
//    private List<Pair<BannerPattern, DyeColorant>> jello$patterns;
//
//    public BannerBlockEntityMixin(BlockPos pos, BlockState state) {
//        super(BlockEntityType.BANNER, pos, state);
//        this.baseColor = ((AbstractBannerBlock)state.getBlock()).getColor();
//    }
//
//    @Override
//    protected void writeNbt(NbtCompound nbt) {
//        super.writeNbt(nbt);
//        if (this.jello$patternListNbt != null) {
//            nbt.put("PatternsJello", this.jello$patternListNbt);
//        }
//
//        if (this.patternListNbt != null) {
//            nbt.put("Patterns", this.patternListNbt);
//        }
//
//        if (this.customName != null) {
//            nbt.putString("CustomName", Text.Serializer.toJson(this.customName));
//        }
//    }
//
//    @Override
//    public void readNbt(NbtCompound nbt) {
//        super.readNbt(nbt);
//        if (nbt.contains("CustomName", 8)) {
//            this.customName = Text.Serializer.fromJson(nbt.getString("CustomName"));
//        }
//
//        this.patternListNbt = nbt.getList("Patterns", 10);
//        this.patterns = null;
//
//        this.jello$patternListNbt = nbt.getList("PatternsJello", 10);
//        jello$patterns = null;
//    }
//
//    private static int jello$getPatternCount(ItemStack stack) {
//        NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(stack);
//        return nbtCompound != null && nbtCompound.contains("PatternsJello") ? nbtCompound.getList("PatternsJello", 10).size() : 0;
//    }
//
//    public List<Pair<BannerPattern, DyeColorant>> jello$getPatterns() {
//        if (this.jello$patterns == null) {
//            this.jello$patterns = jello$getPatternsFromNbt(this.jello$baseColor, this.jello$patternListNbt);
//        }
//
//        return this.jello$patterns;
//    }
//
//    private static List<Pair<BannerPattern, DyeColorant>> jello$getPatternsFromNbt(DyeColorant baseColor, @Nullable NbtList patternListNbt) {
//        List<Pair<BannerPattern, DyeColorant>> list = Lists.newArrayList();
//        list.add(Pair.of(BannerPattern.BASE, baseColor));
//        if (patternListNbt != null) {
//            for(int i = 0; i < patternListNbt.size(); ++i) {
//                NbtCompound nbtCompound = patternListNbt.getCompound(i);
//                BannerPattern bannerPattern = BannerPattern.byId(nbtCompound.getString("Pattern"));
//                if (bannerPattern != null) {
//                    String colorIDasString = nbtCompound.getString("ColorJello");
//                    list.add(Pair.of(bannerPattern, DyeColorant.byId(Identifier.tryParse(colorIDasString))));
//                }
//            }
//        }
//
//        return list;
//    }
//
//    /**
//     * @author
//     */
//    @Overwrite
//    public static void loadFromItemStack(ItemStack stack) {
//        NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(stack);
//        if (nbtCompound != null && nbtCompound.contains("Patterns", 9)) {
//            NbtList nbtList = nbtCompound.getList("Patterns", 10);
//            if (!nbtList.isEmpty()) {
//                nbtList.remove(nbtList.size() - 1);
//                if (nbtList.isEmpty()) {
//                    nbtCompound.remove("Patterns");
//                }
//
//                //BlockItem.setBlockEntityNbt(stack, BlockEntityType.BANNER, nbtCompound);
//            }
//        }
//
//        if (nbtCompound != null && nbtCompound.contains("PatternsJello", 9)) {
//            NbtList nbtList = nbtCompound.getList("PatternsJello", 10);
//            if (!nbtList.isEmpty()) {
//                nbtList.remove(nbtList.size() - 1);
//                if (nbtList.isEmpty()) {
//                    nbtCompound.remove("PatternsJello");
//                }
//
//                BlockItem.setBlockEntityNbt(stack, BlockEntityType.BANNER, nbtCompound);
//            }
//        }
//    }
//
//    /**
//     * @author
//     */
//    @Overwrite
//    public ItemStack getPickStack() {
//        ItemStack itemStack;
//        if(this.baseColor != DyeColorantRegistry.Constants.NULL_VALUE_OLD){
//            itemStack = new ItemStack(BannerBlock.getForColor(this.baseColor));
//        }else{
//            Block block = DyedVariants.DYE_ITEM_VARIANTS.get(this.jello$baseColor).dyedBlocks.stream().filter(block1 -> {
//                return block1 instanceof BannerBlock;
//            }).findAny().get();
//
//            itemStack = new ItemStack(block);
//        }
//
//        NbtCompound nbtCompound = null;
//        if (this.patternListNbt != null && !this.patternListNbt.isEmpty()) {
//            nbtCompound = new NbtCompound();
//            nbtCompound.put("Patterns", this.patternListNbt.copy());
//        }
//
//        if (this.jello$patternListNbt != null && !this.jello$patternListNbt.isEmpty()) {
//            if(nbtCompound == null){ nbtCompound = new NbtCompound(); }
//            nbtCompound.put("PatternsJello", this.jello$patternListNbt.copy());
//        }
//
//        if(nbtCompound != null){
//            BlockItem.setBlockEntityNbt(itemStack, this.getType(), nbtCompound);
//        }
//
//        if (this.customName != null) {
//            itemStack.setCustomName(this.customName);
//        }
//
//        return itemStack;
//    }
//
//    public DyeColorant jello$getColorForState() {
//        return this.jello$baseColor;
//    }
//
//    @Override
//    public void setDyeColor(DyeColorant dyeColorant) {}
//
//    @Override
//    public DyeColorant getDyeColor() {
//        if (jello$baseColor == null){
//            jello$baseColor = ((DyeBlockStorage) ((BannerBlockEntity) (Object) this).getCachedState().getBlock()).getDyeColor();
//        }
//
//        return this.jello$baseColor;
//    }
//}
