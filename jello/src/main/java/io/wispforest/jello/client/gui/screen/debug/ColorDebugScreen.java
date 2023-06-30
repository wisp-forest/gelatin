package io.wispforest.jello.client.gui.screen.debug;

import com.mojang.logging.LogUtils;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import io.wispforest.jello.client.gui.components.button.ButtonAddon;
import io.wispforest.jello.client.gui.ExtraSurfaces;
import io.wispforest.jello.client.gui.components.button.ToggleButtonAddon;
import io.wispforest.jello.client.gui.components.button.VariantButtonSurface;
import io.wispforest.jello.misc.ColorDebugHelper;
import io.wispforest.jello.misc.DyeColorantLoader;
import io.wispforest.jello.misc.pond.owo.ButtonAddonDuck;
import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.HorizontalFlowLayout;
import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.util.CommandOpenedScreen;
import io.wispforest.owo.ui.util.Drawer;
import io.wispforest.owo.util.EventSource;
import io.wispforest.owo.util.EventStream;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class ColorDebugScreen extends BaseOwoScreen<FlowLayout> implements CommandOpenedScreen {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static final ColorDebugHelper.ColorDataStorage NONE = new ColorDebugHelper.ColorDataStorage(DyeColorantRegistry.NULL_VALUE_NEW, new Color(0.0f,0.0f,0.0f), Blocks.BEDROCK);

    public static Surface basicOutline = (matrices, component) -> {
        Drawer.drawRectOutline(matrices, component.x(), component.y(), component.width(), component.height(), Color.BLACK.argb());
    };

    public ColorDebugHelper helper = new ColorDebugHelper();

    private final EventStream<UpdateEvent> updateEvent = UpdateEvent.newStream();

    private final Map<ColorDebugHelper.ColorDataStorage, ColorWidgetCache> cachedWidgets = new LinkedHashMap<>();

    public static final Map<Integer, ChosenColorStorage> chosenDataMap = new HashMap<>();

    public static Map<Identifier, DyeColorantLoader.ColorData> edited_loadedColorData = new LinkedHashMap<>();
    public static Map<String, List<String>> edited_rawConversionData = new LinkedHashMap<>();

    private int mode = 0;

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        FlowLayout mainFlowLayout = Containers.verticalFlow(Sizing.content(), Sizing.content());

        helper.buildColorPositionMap();

        for (ColorDebugHelper.ColorDataStorage value : helper.defaultColorDataStorageMap.values()) {
            cachedWidgets.put(value, createCache(value));
        }

        Function<Integer, Predicate<String>> minimumIntegerBuilder = (minValue) -> {
            return s -> {
                try {
                    if(!s.isEmpty()) {
                        int value = Integer.parseInt(s);

                        return value >= minValue;
                    }

                    return true;
                } catch (NumberFormatException ignore) { return false; }
            };
        };

        mainFlowLayout.child(
                Components.button(Text.of("X"), buttonComponent -> this.close())
                        .renderer(ButtonComponent.Renderer.flat(new Color(0.5f, 0.5f, 0.5f).argb(),new Color(0.65f, 0.65f, 0.65f).argb(),0))
                        .sizing(Sizing.fixed(16))
                        .positioning(Positioning.relative(120, -8))
                        .zIndex(20)
        );

        int mainColorWidgetWidth = 150 + 3 + 3 + 4 + 4 + 2;

        FlowLayout topFlowlayout = (FlowLayout) Containers.verticalFlow(Sizing.content(), Sizing.content())
                .configure((FlowLayout layout) -> {
                    layout.allowOverflow(true)
                            .surface(Surface.DARK_PANEL)
                            .horizontalAlignment(HorizontalAlignment.CENTER)
                            .verticalAlignment(VerticalAlignment.CENTER)
                            .padding(Insets.of(6));
                })
                .child(
                        Containers.verticalFlow(Sizing.content(), Sizing.content())
                                .child(
                                        Containers.verticalFlow(Sizing.content(), Sizing.content())
                                                .child(
                                                        Containers.horizontalFlow(Sizing.content(), Sizing.content())
                                                                .child(Components.button(Text.of("Full"), buttonComponent -> {
                                                                    this.mode = 0;

                                                                    buildFullList();
                                                                }))
                                                                .child(Components.button(Text.of("Filtered"), buttonComponent -> {
                                                                    this.mode = 1;

                                                                    buildFilteredList();
                                                                }))
                                                                .child(Components.button(Text.of("Groupings"), buttonComponent -> {
                                                                    this.mode = 2;

                                                                    buildGroupingList();
                                                                }))
                                                                .gap(4)
                                                                .margins(Insets.of(3, 0, 3, 3))
                                                ).child(
                                                        Containers.verticalFlow(Sizing.content(), Sizing.content())
                                                                .child(
                                                                        Containers.horizontalFlow(Sizing.content(), Sizing.content())
                                                                                .child(Components.label(Text.of("Cube Side Length")))
                                                                                .child(
                                                                                        Components.textBox(Sizing.fixed(42))
                                                                                                .configure((TextBoxComponent component) -> {
                                                                                                    component.setTextPredicate(minimumIntegerBuilder.apply(1));
                                                                                                    component.onChanged().subscribe(
                                                                                                            new DelayedChanged(value -> {
                                                                                                                int number = 2;

                                                                                                                try {
                                                                                                                    number = Math.max(Integer.parseInt(value), 2);
                                                                                                                } catch (NumberFormatException ignore) {}

                                                                                                                helper.computeData(number, helper.maxGroupingSize);

                                                                                                                chosenDataMap.clear();

                                                                                                                if(this.mode == 2) this.buildGroupingList();
                                                                                                            }, 20)
                                                                                                                    .configure(delayedChanged -> this.updateEvent().subscribe(delayedChanged))
                                                                                                    );
                                                                                                }).verticalSizing(Sizing.fixed(16))
                                                                                )
                                                                                .gap(3)
                                                                                .verticalAlignment(VerticalAlignment.CENTER)
                                                                ).child(
                                                                        Containers.horizontalFlow(Sizing.content(), Sizing.content())
                                                                                .child(Components.label(Text.of("Max Grouping Size")))
                                                                                .child(
                                                                                        Components.textBox(Sizing.fixed(42))
                                                                                                .configure((TextBoxComponent component) -> {
                                                                                                    component.setTextPredicate(minimumIntegerBuilder.apply(1));
                                                                                                    component.onChanged().subscribe(
                                                                                                            new DelayedChanged(value -> {
                                                                                                                int number = 1;

                                                                                                                try {
                                                                                                                    number = Integer.parseInt(value);
                                                                                                                } catch (NumberFormatException ignore) {};

                                                                                                                helper.computeData(helper.cubeLength, number);

                                                                                                                chosenDataMap.clear();

                                                                                                                if(this.mode == 2) this.buildGroupingList();
                                                                                                            }, 20)
                                                                                                                    .configure(delayedChanged -> this.updateEvent().subscribe(delayedChanged))
                                                                                                    );
                                                                                                }).verticalSizing(Sizing.fixed(16))
                                                                                )
                                                                                .gap(3)
                                                                                .verticalAlignment(VerticalAlignment.CENTER)
                                                                )
                                                                .gap(3)
                                                                .horizontalAlignment(HorizontalAlignment.RIGHT)
                                                                .margins(Insets.of(3))
                                                )
                                                .surface(Surface.DARK_PANEL)
                                                .padding(Insets.of(4))
                                                .margins(Insets.of(3))
                                )
                                .child(
                                        Containers.verticalScroll(Sizing.fixed(mainColorWidgetWidth), Sizing.fixed(50),
                                                        Containers.verticalFlow(Sizing.content(4), Sizing.content())
                                                                .gap(3)
                                                                .id("extra_info_view_layout")
                                                )
                                                .scrollbar(ScrollContainer.Scrollbar.vanilla())
                                                .scrollbarThiccness(8)
                                                .surface(basicOutline)
                                                .padding(Insets.of(4))
                                                .margins(Insets.of(3))
                                )
                                .horizontalAlignment(HorizontalAlignment.CENTER)
                                .padding(Insets.of(3))
                                .surface(ExtraSurfaces.INVERSE_PANEL)
                )
                .id("top_layout");

        mainFlowLayout.child(topFlowlayout);

        FlowLayout bottomLayout = (FlowLayout) Containers.horizontalFlow(Sizing.content(), Sizing.content())
                .child(Containers.verticalFlow(Sizing.content(), Sizing.content())
                        .configure((FlowLayout layout) -> {
                            layout.allowOverflow(true)
                                    .surface(Surface.DARK_PANEL)
                                    .horizontalAlignment(HorizontalAlignment.CENTER)
                                    .verticalAlignment(VerticalAlignment.CENTER)
                                    .padding(Insets.of(6));
                        })
                        .child(
                                Containers.verticalScroll(
                                                Sizing.content(),
                                                Sizing.fixed(250),
                                                Containers.verticalFlow(Sizing.content(4), Sizing.content())
                                                        .gap(3)
                                                        .id("list_view_layout")
                                        )
                                        .scrollbar(ScrollContainer.Scrollbar.vanilla())
                                        .scrollbarThiccness(8)
                                        .surface(ExtraSurfaces.INVERSE_PANEL)
                                        .padding(Insets.of(6))
//                        Containers.horizontalFlow(Sizing.content(), Sizing.content())
//                                .child(
//                                        Containers.verticalScroll(
//                                                        Sizing.content(),
//                                                        Sizing.fixed(250),
//                                                        Containers.verticalFlow(Sizing.content(4), Sizing.content())
//                                                                .gap(3)
//                                                                .id("list_view_layout")
//                                                )
//                                                .scrollbar(ScrollContainer.Scrollbar.vanilla())
//                                                .scrollbarThiccness(8)
//                                                .surface(basicOutline)
//                                                .padding(Insets.of(4))
//                                                .margins(Insets.of(3))
//                                )
//                                .horizontalAlignment(HorizontalAlignment.CENTER)
//                                .padding(Insets.of(3))
//                                .surface(ExtraSurfaces.INVERSE_PANEL)
//                                .id("primary_list_container")
                        )
                )
                .verticalAlignment(VerticalAlignment.CENTER)
                .id("bottom_layout");

        mainFlowLayout
                .child(bottomLayout)
                .gap(3)
                .horizontalAlignment(HorizontalAlignment.CENTER);

        rootComponent.child(
                mainFlowLayout
                        .positioning(Positioning.relative(50,50))
        );

        buildFullList();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.updateEvent.sink().update(delta, mouseX, mouseY);

        super.render(matrices, mouseX, mouseY, delta);
    }

    protected EventSource<UpdateEvent> updateEvent(){
        return this.updateEvent.source();
    }

    public void buildFullList(){
        FlowLayout listViewLayout = this.uiAdapter.rootComponent.childById(FlowLayout.class, "list_view_layout");

        listViewLayout.clearChildren();

        for (ColorDebugHelper.ColorDataStorage value : helper.defaultColorDataStorageMap.values()) {
            ColorWidgetCache cache = cachedWidgets.get(value);

            listViewLayout.child(createCacheBasedLayout(cache, cache));
        }

        FlowLayout extraViewLayout = this.uiAdapter.rootComponent.childById(FlowLayout.class, "extra_info_view_layout");

        extraViewLayout.clearChildren();

        extraViewLayout.child(
                Components.label(Text.of("Colors within Registry: " + helper.defaultColorDataStorageMap.size()))
                        .margins(Insets.of(2))
        );

        FlowLayout buttonLayout = this.uiAdapter.rootComponent.childById(FlowLayout.class, "bottom_buttons");

        if(buttonLayout != null) buttonLayout.parent().removeChild(buttonLayout);
    }

    public void buildFilteredList(){
        FlowLayout listViewLayout = this.uiAdapter.rootComponent.childById(FlowLayout.class, "list_view_layout");
    }

    public void buildGroupingList(){
        FlowLayout bottomLayout = this.uiAdapter.rootComponent.childById(FlowLayout.class, "bottom_layout");

        FlowLayout listViewLayout = bottomLayout.childById(FlowLayout.class, "list_view_layout");

        listViewLayout.clearChildren();

        int overmaxGroupings = 0;
        int de_duplicatedAmount = 0;

        for (Map.Entry<Vec3i, List<Vec3i>> groupEntry : helper.groupedColorMap.entrySet()) {
            if(groupEntry.getValue().isEmpty() || groupEntry.getValue().size() <= helper.maxGroupingSize) continue;

            FlowLayout mainContainer = Containers.horizontalFlow(Sizing.content(), Sizing.content());

            boolean containsVanilla = false;

            for (Vec3i vec3i : groupEntry.getValue()) {
                ColorDebugHelper.ColorDataStorage value = helper.defaultColorDataStorageMap.get(vec3i);

                if(value.colorant().getId().getNamespace().equals("minecraft")){
                    containsVanilla = true;

                    break;
                }

                ColorWidgetCache cache = cachedWidgets.get(value);

                mainContainer.child(cache.colorWidget);
            }

            if(containsVanilla) continue;

            overmaxGroupings++;

            de_duplicatedAmount += (groupEntry.getValue().size() - 1);

            mainContainer.gap(3);

            //------------------------------------------------

            int groupHashCode = groupEntry.getValue().hashCode();

            ChosenColorStorage storage;

            List<ColorDebugHelper.ColorDataStorage> storageList = groupEntry.getValue().stream()
                    .map(helper.defaultColorDataStorageMap::get)
                    .toList();

            if(chosenDataMap.containsKey(groupHashCode)){
                storage = chosenDataMap.get(groupHashCode);
            } else {
                storage = new ChosenColorStorage(NONE, NONE, storageList);

                chosenDataMap.put(groupHashCode, storage);
            }

            ColorWidgetCache colorCache = createCache(storage.colorValue());
            ColorWidgetCache labelCache = createCache(storage.label());

            FlowLayout chosenLayout = createCacheBasedLayout(labelCache, colorCache);

            //------------------------------------------------

            FlowLayout combinedLayout = Containers.horizontalFlow(Sizing.content(), Sizing.content())
                    .child(
                            Containers.horizontalFlow(Sizing.fixed(150), Sizing.content())
                                    .configure((FlowLayout component) -> {
                                        ((ButtonAddonDuck<FlowLayout>) component).setButtonAddon(layout -> new ButtonAddon<>(layout)
                                                .onPress(button -> {
                                                    showSideScreen(groupHashCode);
                                                })
                                                .useCustomButtonSurface(
                                                        VariantButtonSurface.surfaceLike(Size.square(3), Size.square(48), false, true)
                                                )
                                        );
                                    })
                                    .child(
                                            Containers.horizontalScroll(Sizing.fill(100), Sizing.content(), mainContainer)
                                    )
                                    .gap(3)
                                    .verticalAlignment(VerticalAlignment.CENTER)
                                    .padding(Insets.of(6))
                    )
                    .child(
                            chosenLayout.id(String.valueOf(groupHashCode))
                    )
                    .gap(3);

            listViewLayout.child(
                    combinedLayout
            );
        }

        FlowLayout extraViewLayout = this.uiAdapter.rootComponent.childById(FlowLayout.class, "extra_info_view_layout");

        extraViewLayout.clearChildren();

        extraViewLayout.child(
                Components.label(Text.of("Colors within Registry: " + helper.defaultColorDataStorageMap.size()))
                        .margins(Insets.of(2))
        ).child(
                Components.label(Text.of("Colors after savings: " + (helper.defaultColorDataStorageMap.size() - de_duplicatedAmount)))
                        .margins(Insets.of(2))
        ).child(
                Components.label(Text.of("Number Of Groupings: " + overmaxGroupings))
                        .margins(Insets.of(2))
        ).child(
                Components.label(Text.of("Amount Removed: " + de_duplicatedAmount))
                        .margins(Insets.of(2))
        );

        FlowLayout buttonLayout = this.uiAdapter.rootComponent.childById(FlowLayout.class, "bottom_buttons");

        if(buttonLayout == null) {
            buttonLayout = Containers.horizontalFlow(Sizing.content(), Sizing.content())
                    .child(
                            Components.button(Text.of("Reset All"), component -> {
                                chosenDataMap.forEach((integer, chosenColorStorage) -> {
                                    chosenColorStorage.label(NONE);
                                    chosenColorStorage.colorValue(NONE);
                                });

                                buildGroupingList();
                            }).horizontalSizing(Sizing.fill(15))
                                    .id("reset_button")
                    ).child(
                            Components.button(Text.of("Finalize Decisions"), component -> {
                                try {
                                    AtomicBoolean validValues = new AtomicBoolean(true);

                                    edited_loadedColorData.clear();

                                    edited_loadedColorData.putAll(DyeColorantLoader.loadedColorData);

                                    Component invalidComponent = null;

                                    for (ChosenColorStorage value : chosenDataMap.values()) {
                                        if(value.colorValue.equals(NONE) || value.label.equals(NONE)){
                                            FlowLayout layout = component.parent().parent().childById(FlowLayout.class, "list_view_layout");//

                                            for (Component child : layout.children()) {
                                                if(!(child instanceof HorizontalFlowLayout childLayout)) continue;

                                                FlowLayout targetLayout = (FlowLayout) childLayout.children().get(1);

                                                LabelComponent labelComponent = (LabelComponent) targetLayout.children().get(2);

                                                String labelText = labelComponent.text().getString().toLowerCase().replace(" ", "_");

                                                for (ColorDebugHelper.ColorDataStorage storage : value.values) {
                                                    if(labelText.contains(storage.colorant().getId().getPath())){
                                                        invalidComponent = targetLayout;

                                                        break;
                                                    }
                                                }
                                            }

                                            validValues.set(false);

                                            break;
                                        }
                                    }

                                    if(!validValues.get()){
                                        if(invalidComponent != null){
                                            ScrollContainer<?> container = (ScrollContainer<?>) invalidComponent.parent().parent().parent();

                                            container.scrollTo(invalidComponent.parent());
                                        }

                                        LOGGER.error("It seems one more color values chosen are INVALID!!!!");

                                        MinecraftClient.getInstance().getToastManager().add(
                                                SystemToast.create(
                                                        MinecraftClient.getInstance(),
                                                        SystemToast.Type.CHAT_PREVIEW_WARNING,
                                                        Text.of("INVALID FUCKING COLORS BITCH"),
                                                        Text.of("There are invalid chosen colors!!!")
                                                )
                                        );

                                        return;
                                    }

                                    edited_rawConversionData.clear();

                                    edited_rawConversionData.putAll(DyeColorantLoader.rawConversionData);

                                    chosenDataMap.forEach((integer, chosenStorage) -> {
                                        //if(chosenStorage.colorValue.equals(NONE) || chosenStorage.label.equals(NONE)) return;

                                        for (ColorDebugHelper.ColorDataStorage value : chosenStorage.values) {
                                            if(value.colorant().getId().getNamespace().contains("minecraft")) return;
                                        }

                                        Identifier labelID = chosenStorage.label.colorant().getId();

                                        DyeColorantLoader.ColorData outputLabel = DyeColorantLoader.loadedColorData.get(labelID);

                                        DyeColorantLoader.ColorData outputColor = DyeColorantLoader.loadedColorData.get(chosenStorage.colorValue.colorant().getId());

                                        DyeColorantLoader.ColorData outputData = new DyeColorantLoader.ColorData(outputColor.hexValue(), outputLabel.colorName(), outputLabel.identifierSafeName());

                                        List<String> inputValues = new ArrayList<>();

                                        chosenStorage.values.forEach(storage -> {
                                            DyeColorantLoader.ColorData inputData = edited_loadedColorData.get(storage.colorant().getId());

                                            if (inputData == null) {
                                                LOGGER.error("FUCK SHIT FUCK: [{}]", storage.colorant().getId());
                                                return;
                                            }

                                            if (inputData.getColorId().equals(labelID)) return;

                                            edited_loadedColorData.remove(storage.colorant().getId());

                                            inputValues.add(inputData.getColorId().getPath());
                                        });

                                        edited_rawConversionData.put(outputData.identifierSafeName(), inputValues);
                                    });

                                    DyeColorantLoader.saveNewVersion(edited_loadedColorData, edited_rawConversionData);
                                } catch (Exception e) {
                                    LOGGER.error(e.toString());
                                    e.printStackTrace();
                                }
                            }).horizontalSizing(Sizing.fill(15))
                                    .id("add_to_conversion_button")
                    ).gap(3);

            ((FlowLayout) listViewLayout.parent().parent())
                    .child(
                            buttonLayout.margins(Insets.of(4, 0, 0, 7))
                                    .id("bottom_buttons")
                    );
        }
    }

    public void showSideScreen(int entriesHashCode){
        FlowLayout bottomLayout = this.uiAdapter.rootComponent.childById(FlowLayout.class, "bottom_layout");

        FlowLayout oldDecidingLayout = bottomLayout.childById(FlowLayout.class, "deciding_layout");

        if(oldDecidingLayout != null) bottomLayout.removeChild(oldDecidingLayout);

        FlowLayout decidingLayout = Containers.verticalFlow(Sizing.content(), Sizing.content());

        FlowLayout labelLayout = Containers.verticalFlow(Sizing.content(), Sizing.content())
                .gap(3);

        FlowLayout colorLayout = Containers.verticalFlow(Sizing.content(), Sizing.content())
                .gap(3);

        Function<ChosenColorStorage, FlowLayout> buildChosenLayout = storage -> {
            ColorWidgetCache label = createCache(storage.label);
            ColorWidgetCache color = createCache(storage.colorValue);

            return (FlowLayout) createCacheBasedLayout(label, color)
                    .id("chosen_elements_layout");
        };

        ChosenColorStorage storage = chosenDataMap.get(entriesHashCode);

        for (ColorDebugHelper.ColorDataStorage value : storage.values) {
            ColorWidgetCache cache = createCache(value);

            labelLayout.child(
                    ((ButtonAddonDuck<FlowLayout>) createCacheBasedLayout(cache, null))
                            .setButtonAddon(layout -> {
                                ToggleButtonAddon<FlowLayout> addon = new ToggleButtonAddon<>(layout);

                                addon.onPress(button -> {
                                    if(!addon.selected) storage.label(value);

                                    decidingLayout.removeChild(decidingLayout.childById(FlowLayout.class, "chosen_elements_layout"));

                                    FlowLayout chosenLayout = buildChosenLayout.apply(storage);

                                    decidingLayout.child(0, chosenLayout);
                                });

                                addon.useCustomButtonSurface(VariantButtonSurface.surfaceLike(Size.square(3), Size.square(48), false, true));

                                return addon;
                            })
            );

            colorLayout.child(
                    ((ButtonAddonDuck<FlowLayout>) createCacheBasedLayout(null, cache))
                            .setButtonAddon(layout -> {
                                ToggleButtonAddon<FlowLayout> addon = new ToggleButtonAddon<>(layout);

                                addon.onPress(button -> {
                                    if(!addon.selected) storage.colorValue(value);

                                    decidingLayout.removeChild(decidingLayout.childById(FlowLayout.class, "chosen_elements_layout"));

                                    FlowLayout chosenLayout = buildChosenLayout.apply(storage);

                                    decidingLayout.child(0, chosenLayout);
                                });

                                addon.useCustomButtonSurface(VariantButtonSurface.surfaceLike(Size.square(3), Size.square(48), false, true));

                                return addon;
                            })
            );
        }

        decidingLayout
                .child(
                        buildChosenLayout.apply(storage)
                )
                .child(
                        Containers.verticalScroll(
                                Sizing.content(),
                                Sizing.fixed(150),
                                Containers.horizontalFlow(Sizing.content(), Sizing.content())
                                        .child(labelLayout)
                                        .child(colorLayout)
                                        .gap(3)
                                        .padding(Insets.right(8))
                                )
                                .scrollbar(ScrollContainer.Scrollbar.vanilla())
                                .scrollbarThiccness(8)
                                .surface(basicOutline)
                                .padding(Insets.of(4))
                )
                .child(
                        Containers.horizontalFlow(Sizing.content(), Sizing.content())
                                .child(
                                        Components.button(Text.of("Cancel"), component -> {
                                            bottomLayout.removeChild(bottomLayout.childById(FlowLayout.class, "deciding_layout"));

//                                            FlowLayout layout = bottomLayout.childById(FlowLayout.class, String.valueOf(entriesHashCode));
//
//                                            FlowLayout parentLayout = (FlowLayout) layout.parent();
//
//                                            int index = parentLayout.children().indexOf(layout);
//
//                                            parentLayout.removeChild(layout);
//
//                                            ColorWidgetCache label = createCache(storage.label);
//                                            ColorWidgetCache color = createCache(storage.colorValue);
//
//                                            parentLayout.child(
//                                                    index,
//                                                    createCacheBasedLayout(label, color)
//                                                            .id(String.valueOf(entriesHashCode))
//                                            );

                                            storage.label(NONE);
                                            storage.colorValue(NONE);
                                        }).horizontalSizing(Sizing.fixed(150))
                                )
                                .child(
                                        Components.button(Text.of("Save"), component -> {
                                            bottomLayout.removeChild(bottomLayout.childById(FlowLayout.class, "deciding_layout"));

                                            FlowLayout layout = bottomLayout.childById(FlowLayout.class, String.valueOf(entriesHashCode));

                                            FlowLayout parentLayout = (FlowLayout) layout.parent();

                                            int index = parentLayout.children().indexOf(layout);

                                            parentLayout.removeChild(layout);

                                            ColorWidgetCache label = createCache(storage.label);
                                            ColorWidgetCache color = createCache(storage.colorValue);

                                            parentLayout.child(
                                                    index,
                                                    createCacheBasedLayout(label, color)
                                                            .id(String.valueOf(entriesHashCode))
                                            );

                                            ScrollContainer<?> container = (ScrollContainer<?>) parentLayout.parent().parent();

                                            int nextParentIndexLayout = parentLayout.parent().children().indexOf(parentLayout);// + 1;

                                            List<Component> mainFlowLayoutChildren = parentLayout.parent().children();

                                            if(nextParentIndexLayout < mainFlowLayoutChildren.size()) {
                                                container.scrollTo(mainFlowLayoutChildren.get(nextParentIndexLayout));
                                            }
                                        }).horizontalSizing(Sizing.fixed(150))
                                )
                                .gap(3)
                                .padding(Insets.of(3))
                )
                .gap(3)
                .surface(ExtraSurfaces.INVERSE_PANEL)
                .verticalAlignment(VerticalAlignment.CENTER)
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .padding(Insets.of(6));

        bottomLayout.child(
                Containers.verticalFlow(Sizing.content(), Sizing.content())
                        .child(decidingLayout)
                        .surface(Surface.DARK_PANEL)
                        .padding(Insets.of(6))
                        .margins(Insets.left(3))
                        .id("deciding_layout")
        );
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    public static class DelayedChanged implements TextBoxComponent.OnChanged, UpdateEvent {

        private final TextBoxComponent.OnChanged onChangedEvent;
        private final int delayAmount;

        private int timer;

        private String value = "";

        public DelayedChanged(TextBoxComponent.OnChanged onChangedEvent, int delayAmount){
            this.onChangedEvent = onChangedEvent;
            this.delayAmount = delayAmount;
        }

        @Override
        public void onChanged(String value) {
            this.value = value;

            this.timer = 0;
        }

        @Override
        public void update(float delta, int mouseX, int mouseY) {
            if(this.timer >= delayAmount){
                this.onChangedEvent.onChanged(value);

                this.timer = -1;
            } else if(this.timer != -1){
                this.timer++;
            }
        }

        public DelayedChanged configure(Consumer<DelayedChanged> configuration){
            configuration.accept(this);

            return this;
        }
    }

    public interface UpdateEvent {
        void update(float delta, int mouseX, int mouseY);

        static EventStream<UpdateEvent> newStream() {
            return new EventStream<>(subscribers -> (delta, mouseX, mouseY) -> {
                for(var subscriber : subscribers){
                    subscriber.update(delta, mouseX, mouseY);
                }
            });
        }
    }

    public static ColorWidgetCache createCache(ColorDebugHelper.ColorDataStorage value){
        return new ColorWidgetCache(
                Components.label(Text.of(value.colorant().getFormattedName()))
                        .maxWidth(100),
                Components.block(value.block().getDefaultState())
                        .sizing(Sizing.fixed(16)),
                Containers.verticalFlow(Sizing.fixed(19), Sizing.fixed(19))
                        .child(
                                Components.box(Sizing.fixed(13), Sizing.fixed(13))
                                        .fill(true)
                                        .color(value.color())
                        )
                        .surface(Surface.PANEL)
                        .padding(Insets.of(3))
                        .tooltip(Text.of(value.colorant().getFormattedName()))
        );
    }

    public static FlowLayout createCacheBasedLayout(@Nullable ColorWidgetCache label, @Nullable ColorWidgetCache color){
        FlowLayout layout = Containers.horizontalFlow(Sizing.fixed(150), Sizing.fixed(31));

        if(color != null){
            layout.child(color.block)
                    .child(Containers.horizontalFlow(Sizing.content(), Sizing.content())
                            .child(color.colorWidget)
                            .positioning(Positioning.relative(100, 0))
                    );
        }

        if(label != null){
            layout.child(label.label);
        }

        layout.gap(3)
                .verticalAlignment(VerticalAlignment.CENTER)
                .surface(Surface.DARK_PANEL)
                .padding(Insets.of(6));

        return layout;
    }

    public record ColorWidgetCache(Component label, Component block, Component colorWidget){}

    public static final class ChosenColorStorage {
        private ColorDebugHelper.ColorDataStorage label;
        private ColorDebugHelper.ColorDataStorage colorValue;

        public final List<ColorDebugHelper.ColorDataStorage> values;

        public ChosenColorStorage(ColorDebugHelper.ColorDataStorage label, ColorDebugHelper.ColorDataStorage colorValue, List<ColorDebugHelper.ColorDataStorage> values) {
            this.label = label;
            this.colorValue = colorValue;

            this.values = values;
        }

        public ColorDebugHelper.ColorDataStorage label() {
            return label;
        }

        public ColorDebugHelper.ColorDataStorage colorValue() {
            return colorValue;
        }

        public ChosenColorStorage label(ColorDebugHelper.ColorDataStorage value) {
            this.label = value;

            return this;
        }

        public ChosenColorStorage colorValue(ColorDebugHelper.ColorDataStorage value) {
            this.colorValue = value;

            return this;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (ChosenColorStorage) obj;
            return Objects.equals(this.label, that.label) &&
                    Objects.equals(this.colorValue, that.colorValue);
        }

        @Override
        public int hashCode() {
            return Objects.hash(label, colorValue);
        }

        @Override
        public String toString() {
            return "ChosenWidgetStorage[" +
                    "label=" + label + ", " +
                    "colorValue=" + colorValue + ']';
        }
    }
}
