package io.wispforest.jello.client.gui.screen.debug;

import com.mojang.logging.LogUtils;
import io.wispforest.gelatin.common.util.LangUtils;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import io.blodhgarm.oup.additions.button.ButtonAddon;
import io.blodhgarm.oup.additions.surface.ExtraSurfaces;
import io.blodhgarm.oup.additions.button.VariantButtonSurface;
import io.wispforest.jello.misc.debug.ColorDataStorage;
import io.wispforest.jello.misc.debug.ColorDebugHelper;
import io.wispforest.jello.misc.DyeColorantLoader;
import io.wispforest.jello.misc.pond.owo.ButtonAddonDuck;
import io.wispforest.jello.mixins.FlowLayoutAccessor;
import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.ScrollContainer;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.util.CommandOpenedScreen;
import io.wispforest.owo.util.EventSource;
import io.wispforest.owo.util.EventStream;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ColorDebugScreen extends BaseOwoScreen<FlowLayout> implements CommandOpenedScreen {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final ColorDataStorage NONE = new ColorDataStorage(DyeColorantRegistry.NULL_VALUE_NEW, new Color(0.0f,0.0f,0.0f), Blocks.BEDROCK);

    private static final Surface basicOutline = (context, comp) -> context.drawRectOutline(comp.x(), comp.y(), comp.width(), comp.height(), Color.BLACK.argb());

    //-------

    private static final Map<Integer, ChosenColorStorage> chosenDataMap = new HashMap<>();

    private static final Map<Integer, ChosenLabelStorage> chosenLabelMap = new LinkedHashMap<>();

    //-------

    private static final Map<Identifier, DyeColorantLoader.ColorData> edited_loadedColorData = new LinkedHashMap<>();

    private static final Map<String, List<String>> edited_rawConversionData = new LinkedHashMap<>();

    //-------

    private final ColorDebugHelper helper = new ColorDebugHelper();

    private final EventStream<UpdateEvent> updateEvent = UpdateEvent.newStream();

    private final Map<ColorDataStorage, ColorWidgetCache> cachedWidgets = new LinkedHashMap<>();

    private int mode = 0;

    //-------

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, Containers::verticalFlow);
    }

    //-------

    @Override
    protected void build(FlowLayout rootComponent) {
        helper.buildColorPositionMap();

        helper.defaultColorDataStorageMap.values().forEach(value -> cachedWidgets.put(value, createCache(value)));

        Function<Integer, Predicate<String>> minimumIntegerBuilder = (minValue) -> (s) -> {
            try { return s.isEmpty() || Integer.parseInt(s) >= minValue; }
            catch (NumberFormatException ignore) { return false; }
        };

        FlowLayout mainFlowLayout = Containers.verticalFlow(Sizing.content(), Sizing.content())
                .configure((FlowLayout component) -> {
                    component.gap(3)
                            .horizontalAlignment(HorizontalAlignment.CENTER)
                            .positioning(Positioning.relative(50, 50));
                })
                .child(
                        Components.button(Text.of("X"), buttonComponent -> this.close())
                                .renderer(ButtonComponent.Renderer.flat(new Color(0.5f, 0.5f, 0.5f).argb(), new Color(0.65f, 0.65f, 0.65f).argb(), 0))
                                .sizing(Sizing.fixed(16))
                                .positioning(Positioning.relative(120, -8))
                                .zIndex(20)
                );

        Component topFlowlayout = Containers.verticalFlow(Sizing.content(), Sizing.content())
                .child(
                        Containers.verticalFlow(Sizing.content(), Sizing.content())
                                .child(
                                        Containers.horizontalFlow(Sizing.content(), Sizing.content())
                                                .child((Component) Components.button(Text.of("Full"), buttonComponent -> {
                                                    this.mode = 0;

                                                    buildFullList();
                                                }))
                                                .child((Component) Components.button(Text.of("Groupings"), buttonComponent -> {
                                                    this.mode = 1;

                                                    buildGroupingList();
                                                }))
                                                .child((Component) Components.button(Text.of("Duplicates"), buttonComponent -> {
                                                    this.mode = 2;

                                                    buildColorDuplicateList();
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
                                                                                .configure((Component component) -> {
                                                                                    TextBoxComponent textBoxComponent = (TextBoxComponent) component;

                                                                                    textBoxComponent.setTextPredicate(minimumIntegerBuilder.apply(1));
                                                                                    textBoxComponent.onChanged().subscribe(
                                                                                            new DelayedChanged(20, value -> {
                                                                                                int number = 2;

                                                                                                try {
                                                                                                    number = Math.max(Integer.parseInt(value), 2);
                                                                                                } catch (
                                                                                                        NumberFormatException ignore) {
                                                                                                }

                                                                                                helper.computeData(number, helper.maxGroupingSize);

                                                                                                chosenDataMap.clear();

                                                                                                if (this.mode == 1)
                                                                                                    this.buildGroupingList();
                                                                                            })
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
                                                                                .configure((Component component) -> {
                                                                                    TextBoxComponent textBoxComponent = (TextBoxComponent) component;

                                                                                    textBoxComponent.setTextPredicate(minimumIntegerBuilder.apply(1));
                                                                                    textBoxComponent.onChanged().subscribe(
                                                                                            new DelayedChanged(20, value -> {
                                                                                                int number = 1;

                                                                                                try { number = Integer.parseInt(value); }
                                                                                                catch (NumberFormatException ignore) {}

                                                                                                helper.computeData(helper.cubeLength, number);

                                                                                                chosenDataMap.clear();

                                                                                                if (this.mode == 1) this.buildGroupingList();
                                                                                            }).configure(delayedChanged -> this.updateEvent().subscribe(delayedChanged))
                                                                                    );
                                                                                }).verticalSizing(Sizing.fixed(16))
                                                                )
                                                                .gap(3)
                                                                .verticalAlignment(VerticalAlignment.CENTER)
                                                )
                                                .gap(3)
                                                .horizontalAlignment(HorizontalAlignment.RIGHT)
                                                .margins(Insets.of(3))
                                ).surface(Surface.DARK_PANEL)
                                .padding(Insets.of(4))
                                .margins(Insets.of(3))
                )
                .child(
                        Containers.verticalScroll(Sizing.fixed(150 + 16), Sizing.fixed(50),
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
                .surface(ExtraSurfaces.INVERSE_PANEL);


        //-------

        rootComponent.child(
                mainFlowLayout.child(
                        Containers.verticalFlow(Sizing.content(), Sizing.content())
                                .child(topFlowlayout)
                                .allowOverflow(true)
                                .surface(Surface.DARK_PANEL)
                                .horizontalAlignment(HorizontalAlignment.CENTER)
                                .verticalAlignment(VerticalAlignment.CENTER)
                                .padding(Insets.of(6))
                                .id("top_layout")
                )
                .child(
                        Containers.horizontalFlow(Sizing.content(), Sizing.content())
                                .child(Containers.verticalFlow(Sizing.content(), Sizing.content())
                                        .child(
                                                Containers.verticalScroll(Sizing.content(), Sizing.fixed(250),
                                                                Containers.verticalFlow(Sizing.content(4), Sizing.content())
                                                                        .gap(3)
                                                                        .id("list_view_layout")
                                                        )
                                                        .scrollbar(ScrollContainer.Scrollbar.vanilla())
                                                        .scrollbarThiccness(8)
                                                        .surface(ExtraSurfaces.INVERSE_PANEL)
                                                        .padding(Insets.of(6))
                                        )
                                        .allowOverflow(true)
                                        .surface(Surface.DARK_PANEL)
                                        .horizontalAlignment(HorizontalAlignment.CENTER)
                                        .verticalAlignment(VerticalAlignment.CENTER)
                                        .padding(Insets.of(6))
                                )
                                .verticalAlignment(VerticalAlignment.CENTER)
                                .id("bottom_layout")
                )
        );

        buildFullList();
    }

    public void buildFullList(){
        this.uiAdapter.rootComponent.childById(FlowLayout.class, "list_view_layout")
                .clearChildren()
                .children(
                        helper.defaultColorDataStorageMap.values().stream()
                                .map(value -> (Component) createCachesBasedLayout(cachedWidgets.get(value)))
                                .toList()
                );

        this.uiAdapter.rootComponent.childById(FlowLayout.class, "extra_info_view_layout")
                .clearChildren()
                .child(
                        Components.label(Text.of("Colors within Registry: " + helper.defaultColorDataStorageMap.size()))
                                .margins(Insets.of(2))
                );

        FlowLayout buttonLayout = this.uiAdapter.rootComponent.childById(FlowLayout.class, "bottom_buttons");

        if(buttonLayout != null) buttonLayout.parent().removeChild(buttonLayout);
    }

    public void buildGroupingList(){
        int overmaxGroupings = 0;
        int de_duplicatedAmount = 0;

        List<Component> components = new ArrayList<>();

        for (List<Vec3i> values : helper.groupedColorMap.values()) {
            if(values.isEmpty() || values.size() <= helper.maxGroupingSize) continue;

            List<Component> colorWidgets = new ArrayList<>();

            boolean containsVanilla = false;

            for (Vec3i vec3i : values) {
                ColorDataStorage value = helper.defaultColorDataStorageMap.get(vec3i);

                if(value.colorant().getId().getNamespace().equals("minecraft")){
                    containsVanilla = true;

                    break;
                }

                colorWidgets.add(cachedWidgets.get(value).colorWidget);
            }

            if(containsVanilla) continue;

            overmaxGroupings++;

            de_duplicatedAmount += (values.size() - 1);

            //------------------------------------------------

            int groupHashCode = values.hashCode();

            List<ColorDataStorage> storageList = values.stream()
                    .map(helper.defaultColorDataStorageMap::get)
                    .toList();

            ChosenColorStorage storage = chosenDataMap
                    .computeIfAbsent(groupHashCode, integer -> new ChosenColorStorage(NONE, NONE, storageList));

            FlowLayout chosenLayout = createCachesBasedLayout(createCache(storage.label()), createCache(storage.colorValue()));

            //------------------------------------------------

            components.add(
                    Containers.horizontalFlow(Sizing.content(), Sizing.content())
                            .child(
                                    Containers.horizontalFlow(Sizing.fixed(150), Sizing.content())
                                            .configure((FlowLayout component) -> {
                                                ((ButtonAddonDuck<FlowLayout>) component).setButtonAddon(layout -> new ButtonAddon<>(layout)
                                                        .onPress(button -> showGroupedSideScreen(groupHashCode))
                                                        .buttonSurface(
                                                                VariantButtonSurface.surfaceLike(Size.square(3), Size.square(48), false, true)
                                                        )
                                                );
                                            })
                                            .child(
                                                    Containers.horizontalScroll(Sizing.fill(100), Sizing.content(),
                                                            Containers.horizontalFlow(Sizing.content(), Sizing.content())
                                                                    .children(colorWidgets)
                                                                    .gap(3)
                                                    )
                                            )
                                            .gap(3)
                                            .verticalAlignment(VerticalAlignment.CENTER)
                                            .padding(Insets.of(6))
                            )
                            .child(chosenLayout.id(String.valueOf(groupHashCode)))
                            .gap(3)
            );
        }

        //----

        FlowLayout bottomLayout = this.uiAdapter.rootComponent.childById(FlowLayout.class, "bottom_layout");

        FlowLayout listViewLayout = bottomLayout.childById(FlowLayout.class, "list_view_layout")
                .clearChildren()
                .children(components);

        this.uiAdapter.rootComponent.childById(FlowLayout.class, "extra_info_view_layout")
                .clearChildren()
                .children(
                        Stream.of(
                                "Colors within Registry: " + helper.defaultColorDataStorageMap.size(),
                                "Colors after savings: " + (helper.defaultColorDataStorageMap.size() - de_duplicatedAmount),
                                "Number Of Groupings: " + overmaxGroupings,
                                "Amount Removed: " + de_duplicatedAmount
                        ).map(s -> (Component) Components.label(Text.of(s)).margins(Insets.of(2)))
                                .toList()
                );

        FlowLayout buttonLayout = this.uiAdapter.rootComponent.childById(FlowLayout.class, "bottom_buttons");

        if(buttonLayout != null) return;

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
                                boolean validValues = true;

                                edited_loadedColorData.clear();

                                edited_loadedColorData.putAll(DyeColorantLoader.loadedColorData);

                                Component invalidComponent = null;

                                for (ChosenColorStorage value : chosenDataMap.values()) {
                                    if(!value.colorValue.equals(NONE) && !value.label.equals(NONE)) continue;

                                    FlowLayout layout = component.parent().parent().childById(FlowLayout.class, "list_view_layout");//

                                    for (Component child : layout.children()) {
                                        if(!(child instanceof FlowLayout childLayout && ((FlowLayoutAccessor)childLayout).jello$getAlgorithm() == FlowLayout.Algorithm.HORIZONTAL)) continue;

                                        FlowLayout targetLayout = (FlowLayout) childLayout.children().get(1);

                                        LabelComponent labelComponent = (LabelComponent) targetLayout.children().get(2);

                                        String labelText = labelComponent.text().getString().toLowerCase().replace(" ", "_");

                                        for (ColorDataStorage storage : value.values) {
                                            if(labelText.contains(storage.colorant().getId().getPath())){
                                                invalidComponent = targetLayout;

                                                break;
                                            }
                                        }
                                    }

                                    validValues = false;

                                    break;
                                }

                                if(!validValues){
                                    if(invalidComponent != null){
                                        ScrollContainer<?> container = (ScrollContainer<?>) invalidComponent.parent().parent().parent();

                                        container.scrollTo(invalidComponent.parent());
                                    }

                                    LOGGER.error("It seems one more color values chosen are INVALID!!!!");

                                    MinecraftClient.getInstance().getToastManager().add(
                                            SystemToast.create(
                                                    MinecraftClient.getInstance(),
                                                    SystemToast.Type.PERIODIC_NOTIFICATION,
                                                    Text.of("INVALID FUCKING COLORS BITCH"),
                                                    Text.of("There are invalid chosen colors!!!")
                                            )
                                    );

                                    return;
                                }

                                edited_rawConversionData.clear();

                                chosenDataMap.forEach((integer, chosenStorage) -> {
                                    //if(chosenStorage.colorValue.equals(NONE) || chosenStorage.label.equals(NONE)) return;

                                    for (ColorDataStorage value : chosenStorage.values) {
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

    public void buildColorDuplicateList(){
        List<DyeColorantLoader.ColorData> duplicateEntries = DyeColorantLoader.ColorData.similarNames.values()
                .stream()
                .flatMap(Collection::stream)
                .filter(colorData -> colorData.getColorId().getPath().contains("2"))
                .toList();

        List<Component> components = new ArrayList<>();

        for (DyeColorantLoader.ColorData colorData : duplicateEntries) {
            Vec3i colorVec = helper.colorIDtoColorVec.get(colorData.getColorId());

            ColorDataStorage storage = helper.defaultColorDataStorageMap.get(colorVec);

            ColorWidgetCache colorCache = createCache(storage);

            int groupHashCode = colorCache.hashCode();

            ChosenLabelStorage labelStorage = chosenLabelMap.computeIfAbsent(groupHashCode, integer -> {
                return new ChosenLabelStorage(storage, "");
            });

            FlowLayout combinedLayout = Containers.horizontalFlow(Sizing.content(), Sizing.content())
                    .child(
                            createCachesBasedLayout(colorCache, colorCache)
                                    .configure((FlowLayout component) -> {
                                        ((ButtonAddonDuck<FlowLayout>) component).setButtonAddon(layout -> new ButtonAddon<>(layout)
                                                .onPress(button -> {
                                                    showDuplicateSideScreen(groupHashCode);
                                                })
                                                .buttonSurface(
                                                        VariantButtonSurface.surfaceLike(Size.square(3), Size.square(48), false, true)
                                                )
                                        );
                                    })
                                    .gap(3)
                                    .verticalAlignment(VerticalAlignment.CENTER)
                    )
                    .child(
                            createCacheBasedLayout(Components.label(Text.of(labelStorage.label)), createCache(storage))
                                    .id(String.valueOf(groupHashCode))
                    )
                    .gap(3);

            components.add(combinedLayout);
        }

        FlowLayout bottomLayout = this.uiAdapter.rootComponent.childById(FlowLayout.class, "bottom_layout");

        FlowLayout listViewLayout = bottomLayout.childById(FlowLayout.class, "list_view_layout")
                .clearChildren()
                .children(components);

        this.uiAdapter.rootComponent.childById(FlowLayout.class, "extra_info_view_layout")
                .clearChildren()
                .child(
                        Components.label(Text.of("Duplicatly Named Colors: " + duplicateEntries.size()))
                                .margins(Insets.of(2))
                );

        FlowLayout buttonLayout = this.uiAdapter.rootComponent.childById(FlowLayout.class, "bottom_buttons");

        if(buttonLayout != null) return;

        buttonLayout = Containers.horizontalFlow(Sizing.content(), Sizing.content())
                .child(
                        Components.button(Text.of("Reset All"), component -> {
                                    chosenLabelMap.forEach((integer, chosenColorStorage) -> {
                                        chosenColorStorage.label("");
                                    });

                                    buildColorDuplicateList();
                                }).horizontalSizing(Sizing.fill(15))
                                .id("reset_button")
                ).child(
                        Components.button(Text.of("Finalize Decisions"), component -> {
                                    try {
                                        boolean validValues = true;

                                        edited_loadedColorData.clear();

                                        edited_loadedColorData.putAll(DyeColorantLoader.loadedColorData);

                                        Component invalidComponent = null;

                                        for (ChosenLabelStorage value : chosenLabelMap.values()) {
                                            boolean bl = value.label.isBlank() || helper.defaultColorDataStorageMap.values().stream()
                                                    .anyMatch(storage -> {
                                                        String identifierSafeLabel = value.label().toLowerCase().replace(" ", "_");

                                                        return identifierSafeLabel.contains(storage.colorant().getId().getPath());
                                                    });

                                            if(!bl) continue;

                                            FlowLayout layout = component.parent().parent().childById(FlowLayout.class, "list_view_layout");//

                                            for (Component child : layout.children()) {
                                                if(!(child instanceof FlowLayout childLayout && ((FlowLayoutAccessor)childLayout).jello$getAlgorithm() == FlowLayout.Algorithm.HORIZONTAL)) continue;

                                                invalidComponent = childLayout.children().get(1);

                                                break;
                                            }

                                            validValues = false;

                                            break;
                                        }

                                        if(!validValues){
                                            if(invalidComponent != null){
                                                ScrollContainer<?> container = (ScrollContainer<?>) invalidComponent.parent().parent().parent();

                                                container.scrollTo(invalidComponent.parent());
                                            }

                                            LOGGER.error("It seems one more color values chosen are INVALID!!!!");

                                            MinecraftClient.getInstance().getToastManager().add(
                                                    SystemToast.create(
                                                            MinecraftClient.getInstance(),
                                                            SystemToast.Type.PERIODIC_NOTIFICATION,
                                                            Text.of("INVALID FUCKING COLORS BITCH"),
                                                            Text.of("There are invalid chosen colors!!!")
                                                    )
                                            );

                                            //return;
                                        }

                                        edited_rawConversionData.clear();

                                        chosenLabelMap.forEach((integer, chosenStorage) -> {
                                            String label = chosenStorage.label;

                                            Identifier oldID = chosenStorage.colorData.colorant().getId();

                                            DyeColorantLoader.ColorData oldColorData = DyeColorantLoader.loadedColorData.get(oldID);

                                            String newIdentifierSafeName = label.toLowerCase().replace(" ", "_");

                                            DyeColorantLoader.ColorData newColorData = new DyeColorantLoader.ColorData(oldColorData.hexValue(), label, newIdentifierSafeName);

                                            //oldID is not being used during the saving process
                                            edited_loadedColorData.put(oldID, newColorData);

                                            edited_rawConversionData.put(newIdentifierSafeName, List.of(oldColorData.getColorId().getPath()));
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

    //-------

    public void showGroupedSideScreen(int entriesHashCode){
        FlowLayout bottomLayout = this.uiAdapter.rootComponent.childById(FlowLayout.class, "bottom_layout");

        FlowLayout oldDecidingLayout = bottomLayout.childById(FlowLayout.class, "deciding_layout");

        if(oldDecidingLayout != null) bottomLayout.removeChild(oldDecidingLayout);

        FlowLayout decidingLayout = Containers.verticalFlow(Sizing.content(), Sizing.content());

        Function<ChosenColorStorage, FlowLayout> buildChosenLayout = storage -> {
            return (FlowLayout) createCachesBasedLayout(createCache(storage.label), createCache(storage.colorValue))
                    .id("chosen_elements_layout");
        };

        ChosenColorStorage storage = chosenDataMap.get(entriesHashCode);

        List<Component> labelComponents = new ArrayList<>();
        List<Component> colorComponents = new ArrayList<>();

        for (ColorDataStorage value : storage.values) {
            ColorWidgetCache cache = createCache(value);

            labelComponents.add(
                    ((ButtonAddonDuck<FlowLayout>) createCachesBasedLayout(cache, null))
                            .setButtonAddon(layout -> {
                                return new ButtonAddon<>(layout)
                                        .onPress((ButtonAddon<FlowLayout> button) -> {
                                            storage.label(value);

                                            decidingLayout.removeChild(decidingLayout.childById(FlowLayout.class, "chosen_elements_layout"));

                                            FlowLayout chosenLayout = buildChosenLayout.apply(storage);

                                            decidingLayout.child(0, chosenLayout);
                                        })
                                        .buttonSurface(VariantButtonSurface.surfaceLike(Size.square(3), Size.square(48), false, true));
                            })
            );

            colorComponents.add(
                    ((ButtonAddonDuck<FlowLayout>) createCacheBasedLayout(null, cache))
                            .setButtonAddon(layout -> {
                                return new ButtonAddon<>(layout)
                                        .onPress((ButtonAddon<FlowLayout> button) -> {
                                            storage.colorValue(value);

                                            decidingLayout.removeChild(decidingLayout.childById(FlowLayout.class, "chosen_elements_layout"));

                                            FlowLayout chosenLayout = buildChosenLayout.apply(storage);

                                            decidingLayout.child(0, chosenLayout);
                                        })
                                        .buttonSurface(VariantButtonSurface.surfaceLike(Size.square(3), Size.square(48), false, true));
                            })
            );
        }

        decidingLayout
                .child(buildChosenLayout.apply(storage))
                .child(
                        Containers.verticalScroll(
                                Sizing.content(),
                                Sizing.fixed(150),
                                Containers.horizontalFlow(Sizing.content(), Sizing.content())
                                        .child(Containers.verticalFlow(Sizing.content(), Sizing.content())
                                                .children(labelComponents)
                                                .gap(3)
                                        )
                                        .child(Containers.verticalFlow(Sizing.content(), Sizing.content())
                                                .children(colorComponents)
                                                .gap(3)
                                        )
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
                                                    createCachesBasedLayout(label, color)
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

    public void showDuplicateSideScreen(int entriesHashCode){
        FlowLayout bottomLayout = this.uiAdapter.rootComponent.childById(FlowLayout.class, "bottom_layout");

        FlowLayout oldDecidingLayout = bottomLayout.childById(FlowLayout.class, "deciding_layout");

        if(oldDecidingLayout != null) bottomLayout.removeChild(oldDecidingLayout);

        FlowLayout decidingLayout = Containers.verticalFlow(Sizing.content(), Sizing.content());

        ChosenLabelStorage storage = chosenLabelMap.get(entriesHashCode);

        LabelComponent labelComponent = Components.label(Text.of(storage.label))
                .configure(label -> {

                });

        FlowLayout colorLabelLayout = createCacheBasedLayout(labelComponent, createCache(storage.colorData));

        FlowLayout similar_named_layout = (FlowLayout) Containers.verticalFlow(Sizing.fill(100), Sizing.content())
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .verticalAlignment(VerticalAlignment.CENTER)
                .id("similarly_named_colors");

        TextBoxComponent textComponent = Components.textBox(Sizing.fixed(200), storage.label)
                .configure(c -> {
                    c.onChanged()
                            .subscribe(value -> {
                                labelComponent.text(Text.of(value));

                                similar_named_layout.clearChildren();

                                if(value.isBlank()) return;

                                String[] words = value.toLowerCase().split(" ");

                                Int2ObjectMap<List<String>> map = new Int2ObjectLinkedOpenHashMap<>();

                                helper.defaultColorDataStorageMap.values()
                                        .forEach(colorDataStorage -> {
                                            String namespace = colorDataStorage.colorant().getId().getPath();

                                            String[] colorWords = namespace.split("_");

                                            int matchesType1 = 0;

                                            for (String word : words) {
                                                for (String colorWord : colorWords) {
                                                    if(word.contains(colorWord)) matchesType1++;
                                                }
                                            }

                                            int matchesType2 = 0;

                                            if(matchesType1 == 0){
                                                for (String colorWord : colorWords) {
                                                    for (String word : words) {
                                                        if(colorWord.contains(word)) matchesType2++;
                                                    }
                                                }
                                            }

                                            if(matchesType1 == 0 && matchesType2 == 0) return;

                                            map.computeIfAbsent(matchesType1 + matchesType2, i -> new ArrayList<>())
                                                    .add(namespace);
                                        });

                                map.forEach((integer, strings) -> {
                                    strings.forEach(s -> {
                                        similar_named_layout.child(
                                                Components.label(Text.of(LangUtils.capitalizeEachWord(s.replace("_", " "))))
                                                        .margins(Insets.of(2))
                                        );
                                    });
                                });
                            });
                });

        decidingLayout
                .child(colorLabelLayout)
                .child(
                        Containers.verticalFlow(Sizing.fixed(226), Sizing.content())
                                .child((Component) textComponent)
                                .child(
                                        Containers.verticalScroll(Sizing.fill(100), Sizing.fixed(140), similar_named_layout)
                                                .surface(Surface.DARK_PANEL)
                                                .padding(Insets.of(4))
                                                .margins(Insets.of(4))
                                )
                                .horizontalAlignment(HorizontalAlignment.CENTER)
                                .surface(basicOutline)
                                .padding(Insets.of(4))
                )
                .child(
                        Containers.horizontalFlow(Sizing.content(), Sizing.content())
                                .child(
                                        Components.button(Text.of("Cancel"), component -> {
                                            bottomLayout.removeChild(bottomLayout.childById(FlowLayout.class, "deciding_layout"));

                                        }).horizontalSizing(Sizing.fixed(150))
                                )
                                .child(
                                        Components.button(Text.of("Save"), component -> {
                                            bottomLayout.removeChild(bottomLayout.childById(FlowLayout.class, "deciding_layout"));

                                            FlowLayout layout = bottomLayout.childById(FlowLayout.class, String.valueOf(entriesHashCode));

                                            FlowLayout parentLayout = (FlowLayout) layout.parent();

                                            int index = parentLayout.children().indexOf(layout);

                                            parentLayout.removeChild(layout);

                                            LabelComponent label = Components.label(Text.of(textComponent.getText()));

                                            storage.label(textComponent.getText());

                                            ColorWidgetCache color = createCache(storage.colorData);

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

    //----

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.updateEvent.sink().update(delta, mouseX, mouseY);

        super.render(context, mouseX, mouseY, delta);
    }

    //----

    protected EventSource<UpdateEvent> updateEvent(){
        return this.updateEvent.source();
    }

    public interface UpdateEvent {
        void update(float delta, int mouseX, int mouseY);

        static EventStream<UpdateEvent> newStream() {
            return new EventStream<>(subscribers -> (delta, mouseX, mouseY) -> {
                subscribers.forEach(subscriber -> subscriber.update(delta, mouseX, mouseY));
            });
        }
    }

    public static class DelayedChanged implements TextBoxComponent.OnChanged, UpdateEvent {

        private final TextBoxComponent.OnChanged onChangedEvent;
        private final int delayAmount;

        private int timer;

        private String value = "";

        public DelayedChanged(int delayAmount, TextBoxComponent.OnChanged onChangedEvent){
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

    //----

    private static ColorWidgetCache createCache(ColorDataStorage value){
        return new ColorWidgetCache(
                Components.label(Text.of(value.colorant().getFormattedName()))
                        .maxWidth(100),
                Components.block(value.block().getDefaultState())
                        .sizing(Sizing.fixed(16)),
                ((ButtonAddonDuck<FlowLayout>)Containers.verticalFlow(Sizing.fixed(19), Sizing.fixed(19)))
                        .setButtonAddon(layout -> {
                            return new ButtonAddon<>(layout)
                                    .onPress(button -> {
                                            MinecraftClient.getInstance().keyboard.setClipboard(value.color().asHexString(false).replace("#", ""));
                                    })
                                    .buttonSurface(VariantButtonSurface.surfaceLike(Size.square(3), Size.square(48), false, false));
                        })
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

    private static FlowLayout createCachesBasedLayout(@Nullable ColorWidgetCache cache){
        return createCachesBasedLayout(cache, cache);
    }

    private static FlowLayout createCachesBasedLayout(@Nullable ColorWidgetCache label, @Nullable ColorWidgetCache color){
        return createCacheBasedLayout(label != null ? label.label : null, color);
    }

    private static <C extends Component> FlowLayout createCacheBasedLayout(@Nullable C label, @Nullable ColorWidgetCache color){
        List<Component> components = new ArrayList<>();

        if(color != null){
            components.add(color.block);

            components.add(
                    Containers.horizontalFlow(Sizing.content(), Sizing.content())
                            .child(color.colorWidget)
                            .positioning(Positioning.relative(100, 0))
            );
        }

        if(label != null) components.add(label);

        return (FlowLayout) Containers.horizontalFlow(Sizing.fixed(150), Sizing.fixed(31))
                .children(components)
                .gap(3)
                .verticalAlignment(VerticalAlignment.CENTER)
                .surface(Surface.DARK_PANEL)
                .padding(Insets.of(6));
    }

    private record ColorWidgetCache(Component label, Component block, Component colorWidget){}

    private static final class ChosenColorStorage {
        private ColorDataStorage label;
        private ColorDataStorage colorValue;

        public final List<ColorDataStorage> values;

        public ChosenColorStorage(ColorDataStorage label, ColorDataStorage colorValue, List<ColorDataStorage> values) {
            this.label = label;
            this.colorValue = colorValue;

            this.values = values;
        }

        public ColorDataStorage label() {
            return label;
        }

        public ColorDataStorage colorValue() {
            return colorValue;
        }

        public ChosenColorStorage label(ColorDataStorage value) {
            this.label = value;

            return this;
        }

        public ChosenColorStorage colorValue(ColorDataStorage value) {
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

    private static final class ChosenLabelStorage {
        private final ColorDataStorage colorData;
        private String label;

        public ChosenLabelStorage(ColorDataStorage colorData, String label) {
            this.colorData = colorData;
            this.label = label;
        }

        public ColorDataStorage colorData() {
            return colorData;
        }

        public String label() {
            return label;
        }

        public ChosenLabelStorage label(String label){
            this.label = label;

            return this;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (ChosenLabelStorage) obj;
            return Objects.equals(this.colorData, that.colorData) &&
                    Objects.equals(this.label, that.label);
        }

        @Override
        public int hashCode() {
            return Objects.hash(colorData, label);
        }

        @Override
        public String toString() {
            return "ChosenLabelStorage[" +
                    "colorData=" + colorData + ", " +
                    "label=" + label + ']';
        }
    }
}
