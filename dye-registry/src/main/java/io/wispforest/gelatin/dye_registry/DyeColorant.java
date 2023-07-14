package io.wispforest.gelatin.dye_registry;

import io.wispforest.gelatin.common.util.ColorUtil;
import net.fabricmc.loader.impl.util.StringUtil;
import net.minecraft.block.MapColor;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A replacement for Minecrafts {@link DyeColor} Enum with mostly the exact same functions (Planned).
 */
public class DyeColorant {

    private final RegistryEntry.Reference<DyeColorant> registryEntry = DyeColorantRegistry.DYE_COLOR.createEntry(this);

    private final MapColor mapColor;
    private final float[] colorComponents;

    private final int baseColor;
    private final int fireworkColor;
    private final int signColor;

    public DyeColorant(MapColor mapColor, int baseColor, int fireworkColor, int signColor) {
        this.colorComponents = ColorUtil.getColorComponents(baseColor);
        this.mapColor = mapColor;
        this.baseColor = baseColor;
        this.fireworkColor = fireworkColor;
        this.signColor = signColor;
    }

    //----------------------------------------------------------------

    /**
     * @return the {@link Identifier} of the {@link DyeColorant} from the {@link DyeColorantRegistry#DYE_COLOR} Registry
     */
    public Identifier getId() {
        return DyeColorantRegistry.DYE_COLOR.getId(this);
    }

    /**
     * @return the path or name of the {@link DyeColorant} from the {@link DyeColorantRegistry#DYE_COLOR} Registry
     */
    public String getName() {
        return getId().getPath();
    }

    /**
     * @return A Crude formatted version of the {@link DyeColorant} name
     */
    public String getFormattedName(){
        return Arrays.stream(getName().split("_"))
                .map(StringUtil::capitalize)
                .collect(Collectors.joining(" "));
    }

    //----------------------------------------------------------------

    /**
     * @return the Integer color value given to the DyeColor
     */
    public int getBaseColor() {
        return this.baseColor;
    }

    /**
     * @return the red, blue and green components of this dye color.
     */
    public float[] getColorComponents() {
        return this.colorComponents;
    }

    public MapColor getMapColor() {
        return this.mapColor;
    }

    //TODO: Implement Custom Firework Colors
    public int getFireworkColor() {
        return this.fireworkColor;
    }

    //TODO: Implement Custom Sign Colors
    public int getSignColor() {
        return this.signColor;
    }

    //----------------------------------------------------------------

    public RegistryEntry.Reference<DyeColorant> getRegistryEntry() {
        return this.registryEntry;
    }

    /**
     * Checks if the {@link DyeColorant} is within the given tag.
     *
     * @param tag Key of the tag being checked
     * @return True or False depending on if it is within the given tag
     */
    public boolean isIn(TagKey<DyeColorant> tag) {
        return getRegistryEntry().isIn(tag);
    }

    /**
     * Gets a {@link DyeColorant} if the given {@link Identifier} has been registered.
     *
     * @param id The registered {@link Identifier} for a {@link DyeColorant}
     * @return {@link DyeColorant}
     */
    public static DyeColorant byId(Identifier id) {
        return DyeColorantRegistry.DYE_COLOR.get(id);
    }

    //----------------------------------------------------------------

    public String toString() {
        return this.getName();
    }

    @ApiStatus.Internal
    public static DyeColorant byName(String name, DyeColorant defaultColor) {
        for (DyeColorant dyeColor : DyeColorantRegistry.DYE_COLOR.stream().toList()) {
            if (dyeColor.getName().equals(name)) {
                return dyeColor;
            }
        }

        return defaultColor;
    }

    @ApiStatus.Internal
    public static DyeColorant byOldIntId(int id) {
        DyeColorant dyeColor = DyeColorantRegistry.Constants.VANILLA_DYES.get(id);

        return dyeColor != null ? dyeColor : DyeColorantRegistry.NULL_VALUE_NEW;
    }

    @ApiStatus.Internal
    public static DyeColorant byFireworkColor(int color) {
        for (DyeColorant dyeColor : DyeColorantRegistry.DYE_COLOR.stream().toList()) {
            if (dyeColor.getFireworkColor() == color) {
                return dyeColor;
            }
        }

        return null;
    }

    @ApiStatus.Internal
    public static DyeColorant byOldDyeColor(DyeColor dyeColor) {
        return byOldIntId(dyeColor.getId());
    }

    public boolean nullColorCheck(){
        return this == DyeColorantRegistry.NULL_VALUE_NEW;
    }

    //----------------------------------------------------------------

    public static int blendDyeColors(DyeColorant... colors) {
        int[] is = new int[3];
        int i = 0;
        int j = 0;

        for (DyeColorant dyeColorant : colors) {
            float[] fs = dyeColorant.getColorComponents();
            int l = (int) (fs[0] * 255.0F);
            int m = (int) (fs[1] * 255.0F);
            int n = (int) (fs[2] * 255.0F);
            i += Math.max(l, Math.max(m, n));
            is[0] += l;
            is[1] += m;
            is[2] += n;
            ++j;
        }

        int k = is[0] / j;
        int o = is[1] / j;
        int p = is[2] / j;
        float h = (float) i / (float) j;
        float q = (float) Math.max(k, Math.max(o, p));
        k = (int) ((float) k * h / q);
        o = (int) ((float) o * h / q);
        p = (int) ((float) p * h / q);
        int var26 = (k << 8) + o;
        var26 = (var26 << 8) + p;

        return var26;
    }

    public static ItemStack blendItemColorAndDyeColor(ItemStack stack, List<DyeColorant> colors) {
        ItemStack itemStack = ItemStack.EMPTY;
        int[] is = new int[3];
        int i = 0;
        int j = 0;
        DyeableItem dyeableItem = null;
        Item item = stack.getItem();
        if (item instanceof DyeableItem) {
            dyeableItem = (DyeableItem) item;
            itemStack = stack.copy();
            itemStack.setCount(1);
            if (dyeableItem.hasColor(stack)) {
                int k = dyeableItem.getColor(itemStack);
                float f = (float) (k >> 16 & 0xFF) / 255.0F;
                float g = (float) (k >> 8 & 0xFF) / 255.0F;
                float h = (float) (k & 0xFF) / 255.0F;
                i = (int) ((float) i + Math.max(f, Math.max(g, h)) * 255.0F);
                is[0] = (int) ((float) is[0] + f * 255.0F);
                is[1] = (int) ((float) is[1] + g * 255.0F);
                is[2] = (int) ((float) is[2] + h * 255.0F);
                ++j;
            }

            for (DyeColorant dyeColorant : colors) {
                float[] fs = dyeColorant.getColorComponents();
                int l = (int) (fs[0] * 255.0F);
                int m = (int) (fs[1] * 255.0F);
                int n = (int) (fs[2] * 255.0F);
                i += Math.max(l, Math.max(m, n));
                is[0] += l;
                is[1] += m;
                is[2] += n;
                ++j;
            }
        }

        if (dyeableItem == null) {
            return ItemStack.EMPTY;
        } else {
            int k = is[0] / j;
            int o = is[1] / j;
            int p = is[2] / j;
            float h = (float) i / (float) j;
            float q = (float) Math.max(k, Math.max(o, p));
            k = (int) ((float) k * h / q);
            o = (int) ((float) o * h / q);
            p = (int) ((float) p * h / q);
            int var26 = (k << 8) + o;
            var26 = (var26 << 8) + p;
            dyeableItem.setColor(itemStack, var26);
            return itemStack;
        }
    }
}
