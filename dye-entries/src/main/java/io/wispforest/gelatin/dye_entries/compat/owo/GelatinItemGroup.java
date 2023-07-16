package io.wispforest.gelatin.dye_entries.compat.owo;

import io.wispforest.gelatin.common.misc.GelatinConstants;
import io.wispforest.gelatin.dye_entries.misc.DyeEntriesItemGroups;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import io.wispforest.owo.itemgroup.Icon;
import io.wispforest.owo.itemgroup.OwoItemGroup;
import io.wispforest.owo.util.pond.OwoItemExtensions;
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
                    //this.addTab(Icon.of(JelloItems.DYE_BUNDLE.getDefaultStack()), "jello_tools", null, ItemGroupTab.DEFAULT_TEXTURE);

                    if(DyeColorantRegistry.DYE_COLOR.size() > 17) {
                        Pair<Item, Item> iconPair = DyeEntriesItemGroups.getIconItems();

                        group.addCustomTab(Icon.of(iconPair.getLeft()), "dyed_item_variants", (context, entries) -> {
                            getItemEntries(group, entries, true);
                        }, true);
                        group.addCustomTab(Icon.of(iconPair.getRight()), "dyed_block_variants", (context, entries) -> {
                            getItemEntries(group, entries, true);
                        }, false);
                    }
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


}