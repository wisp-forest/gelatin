package io.wispforest.gelatin.dye_entries.compat.owo;

import io.wispforest.gelatin.common.misc.GelatinConstants;
import io.wispforest.gelatin.dye_entries.misc.DyeEntriesItemGroups;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.util.pond.OwoItemExtensions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class GelatinItemGroup extends OwoItemGroup {

    public static final OwoItemGroup MAIN_ITEM_GROUP = GelatinItemGroup.create(GelatinConstants.id("gelatin_group"));

    protected GelatinItemGroup(Identifier id, Consumer<OwoItemGroup> initializer, Supplier<Icon> iconSupplier, int tabStackHeight, int buttonStackHeight, @Nullable Identifier customTexture, boolean useDynamicTitle, boolean displaySingleTab) {
        super(id, initializer, iconSupplier, tabStackHeight, buttonStackHeight, customTexture, useDynamicTitle, displaySingleTab);
    }

    public static OwoItemGroup create(Identifier id){
        return OwoItemGroup.builder(id, () -> Icon.of(Items.ORANGE_DYE.getDefaultStack()))
                .initializer(group -> {
                    group.addCustomTab(new DelayedIcon(() -> DyeEntriesItemGroups.getIconItems().getLeft().getDefaultStack()), "dyed_item_variants", (context, entries) -> {
                        getItemEntries(group, entries, true);
                    }, true);

                    group.addCustomTab(new DelayedIcon(() -> DyeEntriesItemGroups.getIconItems().getRight().getDefaultStack()), "dyed_block_variants", (context, entries) -> {
                        getItemEntries(group, entries, true);
                    }, false);
                }).build();
    }

    public static void getItemEntries(OwoItemGroup group, ItemGroup.Entries entries, boolean matchTab){
        ItemGroup.EntriesImpl sortableEntries = new ItemGroup.EntriesImpl(group, FeatureSet.empty());

        Registries.ITEM.stream()
                .filter(item -> ((OwoItemExtensions) item).owo$group() == group && (!matchTab || ((OwoItemExtensions) item).owo$tab() == group.getSelectedTabIndex()))
                .forEach(item -> ((OwoItemExtensions) item).owo$stackGenerator().accept(item, sortableEntries));

        DyeEntriesItemGroups.sortEntries(sortableEntries.parentTabStacks, group.getSelectedTabIndex());

        entries.addAll(sortableEntries.parentTabStacks, ItemGroup.StackVisibility.PARENT_TAB_ONLY);
    }

    @Environment(EnvType.CLIENT)
    public static class DelayedIcon implements Icon {
        private final Supplier<ItemStack> stackSupplier;

        private ItemStack stack = ItemStack.EMPTY;

        private DelayedIcon(Supplier<ItemStack> stackSupplier) {
            this.stackSupplier = stackSupplier;
        }

        @Override
        public void render(DrawContext context, int x, int y, int mouseX, int mouseY, float delta) {
            if(this.stack == ItemStack.EMPTY) this.stack = this.stackSupplier.get();

            context.drawItemWithoutEntity(this.stack, x, y);
        }
    }

}