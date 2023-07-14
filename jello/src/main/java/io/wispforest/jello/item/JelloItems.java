package io.wispforest.jello.item;

import io.wispforest.gelatin.common.util.ItemFunctions;
import io.wispforest.gelatin.dye_entries.item.ColoredItem;
import io.wispforest.gelatin.dye_registry.DyeColorant;
import io.wispforest.gelatin.dye_registry.DyeColorantRegistry;
import io.wispforest.jello.Jello;
import io.wispforest.jello.item.dyebundle.DyeBundleItem;
import io.wispforest.jello.item.jellocup.JelloCupCreationHandler;
import io.wispforest.jello.item.jellocup.JelloCupItem;
import io.wispforest.jello.misc.JelloPotions;
import io.wispforest.jello.misc.itemgroup.JelloItemSettings;
import io.wispforest.owo.registration.reflect.ItemRegistryContainer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class JelloItems implements ItemRegistryContainer {

    public static final Item SPONGE = new SpongeItem(new JelloItemSettings().group(ItemGroups.TOOLS).maxCount(1));
    public static final Item DYE_BUNDLE = new DyeBundleItem(new JelloItemSettings().group(ItemGroups.TOOLS).maxCount(1));

    public static final Item EMPTY_ARTIST_PALETTE = new Item(new JelloItemSettings().group(ItemGroups.INGREDIENTS).maxCount(1));
    public static final Item ARTIST_PALETTE = new ArtistPaletteItem(new JelloItemSettings().group(ItemGroups.INGREDIENTS).maxCount(1));

    public static final Item BOWL_OF_SUGAR = new Item(new JelloItemSettings().group(ItemGroups.FOOD_AND_DRINK).food(JelloFoodComponents.BOWL_OF_SUGAR)) {
        @Override
        public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
            ItemStack itemStack = super.finishUsing(stack, world, user);

            if(user instanceof PlayerEntity player && !player.getAbilities().creativeMode) {
                player.getInventory().insertStack(new ItemStack(Items.BOWL));
            }

            return itemStack;
        }
    };
    public static final Item GELATIN = new Item(new JelloItemSettings().group(ItemGroups.INGREDIENTS));

    public static final Item GELATIN_SOLUTION = new GelatinSolutionItem(new JelloItemSettings().maxCount(1));

    public static final Item CONCENTRATED_DRAGON_BREATH = new Item(new JelloItemSettings().group(ItemGroups.INGREDIENTS));

    public static class Slimeballs implements ItemRegistryContainer {
        public static final Item WHITE_SLIME_BALL = new ColoredItem(DyeColorantRegistry.WHITE, (new JelloItemSettings()).group(ItemGroups.INGREDIENTS));
        public static final Item ORANGE_SLIME_BALL = new ColoredItem(DyeColorantRegistry.ORANGE, (new JelloItemSettings()).group(ItemGroups.INGREDIENTS));
        public static final Item MAGENTA_SLIME_BALL = new ColoredItem(DyeColorantRegistry.MAGENTA, (new JelloItemSettings()).group(ItemGroups.INGREDIENTS));
        public static final Item LIGHT_BLUE_SLIME_BALL = new ColoredItem(DyeColorantRegistry.LIGHT_BLUE, (new JelloItemSettings()).group(ItemGroups.INGREDIENTS));
        public static final Item YELLOW_SLIME_BALL = new ColoredItem(DyeColorantRegistry.YELLOW, (new JelloItemSettings()).group(ItemGroups.INGREDIENTS));
        public static final Item LIME_SLIME_BALL = new ColoredItem(DyeColorantRegistry.LIME, (new JelloItemSettings()).group(ItemGroups.INGREDIENTS));
        public static final Item PINK_SLIME_BALL = new ColoredItem(DyeColorantRegistry.PINK, (new JelloItemSettings()).group(ItemGroups.INGREDIENTS));
        public static final Item GRAY_SLIME_BALL = new ColoredItem(DyeColorantRegistry.GRAY, (new JelloItemSettings()).group(ItemGroups.INGREDIENTS));
        public static final Item LIGHT_GRAY_SLIME_BALL = new ColoredItem(DyeColorantRegistry.LIGHT_GRAY, (new JelloItemSettings()).group(ItemGroups.INGREDIENTS));
        public static final Item CYAN_SLIME_BALL = new ColoredItem(DyeColorantRegistry.CYAN, (new JelloItemSettings()).group(ItemGroups.INGREDIENTS));
        public static final Item PURPLE_SLIME_BALL = new ColoredItem(DyeColorantRegistry.PURPLE, (new JelloItemSettings()).group(ItemGroups.INGREDIENTS));
        public static final Item BLUE_SLIME_BALL = new ColoredItem(DyeColorantRegistry.BLUE, (new JelloItemSettings()).group(ItemGroups.INGREDIENTS));
        public static final Item BROWN_SLIME_BALL = new ColoredItem(DyeColorantRegistry.BROWN, (new JelloItemSettings()).group(ItemGroups.INGREDIENTS));
        public static final Item GREEN_SLIME_BALL = new ColoredItem(DyeColorantRegistry.GREEN, (new JelloItemSettings()).group(ItemGroups.INGREDIENTS));
        public static final Item RED_SLIME_BALL = new ColoredItem(DyeColorantRegistry.RED, (new JelloItemSettings()).group(ItemGroups.INGREDIENTS));
        public static final Item BLACK_SLIME_BALL = new ColoredItem(DyeColorantRegistry.BLACK, (new JelloItemSettings()).group(ItemGroups.INGREDIENTS));

        public static final List<Item> SLIME_BALLS = List
                .of(WHITE_SLIME_BALL, ORANGE_SLIME_BALL, MAGENTA_SLIME_BALL, LIGHT_BLUE_SLIME_BALL,
                        YELLOW_SLIME_BALL, LIME_SLIME_BALL, PINK_SLIME_BALL, GRAY_SLIME_BALL,
                        LIGHT_GRAY_SLIME_BALL, CYAN_SLIME_BALL, PURPLE_SLIME_BALL, BLUE_SLIME_BALL,
                        BROWN_SLIME_BALL, GREEN_SLIME_BALL, RED_SLIME_BALL, BLACK_SLIME_BALL);
    }

    public static class JelloCups implements ItemRegistryContainer {

        public static final Item SUGAR_CUP = new Item(ItemFunctions.copyFrom(new JelloItemSettings().group(ItemGroups.FOOD_AND_DRINK).fireproof()).food(JelloFoodComponents.SUGAR_CUP));

        public static final Item WHITE_JELLO_CUP = createJelloCup(DyeColorantRegistry.WHITE)
                .createEffectData(handler -> {
                    handler.primaryEffects.add(StatusEffects.SLOW_FALLING);

                    getPotions(handler);
                });

        public static final Item ORANGE_JELLO_CUP = createJelloCup(DyeColorantRegistry.ORANGE)
                .createEffectData(handler -> {
                    handler.primaryEffects.add(StatusEffects.FIRE_RESISTANCE);

                    getPotions(handler);
                });

        public static final Item MAGENTA_JELLO_CUP = createJelloCup(DyeColorantRegistry.MAGENTA)
                .createEffectData(handler -> {
                    handler.primaryEffects.add(StatusEffects.HEALTH_BOOST);

                    getPotions(handler);
                });

        public static final Item LIGHT_BLUE_JELLO_CUP = createJelloCup(DyeColorantRegistry.LIGHT_BLUE)
                .createEffectData(handler -> {
                    handler.primaryEffects.add(StatusEffects.JUMP_BOOST);

                    getPotions(handler);
                });

        public static final Item YELLOW_JELLO_CUP = createJelloCup(DyeColorantRegistry.YELLOW)
                .createEffectData(handler -> {
                    handler.primaryEffects.addAll(List.of(StatusEffects.REGENERATION, StatusEffects.RESISTANCE, StatusEffects.FIRE_RESISTANCE, StatusEffects.ABSORPTION));

                    List<Potion> potions = List.of(JelloPotions.GOLDEN_LIQUID, JelloPotions.ENCHANTED_GOLDEN_LIQUID);

                    handler.validPotions.addAll(potions);

                    handler.alternativeValidIngredient.put(Items.GOLDEN_APPLE, JelloPotions.GOLDEN_LIQUID);
                    handler.alternativeValidIngredient.put(Items.ENCHANTED_GOLDEN_APPLE, JelloPotions.ENCHANTED_GOLDEN_LIQUID);
                }).setDivisionFactor(2f);

        public static final Item LIME_JELLO_CUP = createJelloCup(DyeColorantRegistry.LIME)
                .createEffectData(handler -> {
                    handler.primaryEffects.add(StatusEffects.LUCK);

                    getPotions(handler);
                });

        public static final Item PINK_JELLO_CUP = createJelloCup(DyeColorantRegistry.PINK)
                .createEffectData(handler -> {
                    handler.primaryEffects.add(StatusEffects.INSTANT_HEALTH);

                    getPotions(handler);
                });

        public static final Item GRAY_JELLO_CUP = createJelloCup(DyeColorantRegistry.GRAY)
                .createEffectData(handler -> {
                    handler.primaryEffects.add(StatusEffects.INVISIBILITY);

                    getPotions(handler);
                });

        public static final Item LIGHT_GRAY_JELLO_CUP = createJelloCup(DyeColorantRegistry.LIGHT_GRAY)
                .createEffectData(handler -> {
                    handler.primaryEffects.add(StatusEffects.DOLPHINS_GRACE);

                    getPotions(handler);
                });

        public static final Item CYAN_JELLO_CUP = createJelloCup(DyeColorantRegistry.CYAN)
                .createEffectData(handler -> {
                    handler.primaryEffects.add(StatusEffects.WATER_BREATHING);

                    getPotions(handler);
                });

        public static final Item PURPLE_JELLO_CUP = createJelloCup(DyeColorantRegistry.PURPLE)
                .createEffectData(handler -> {
                    handler.primaryEffects.add(StatusEffects.NIGHT_VISION);

                    getPotions(handler);
                });

        public static final Item BLUE_JELLO_CUP = createJelloCup(DyeColorantRegistry.BLUE)
                .createEffectData(handler -> {
                    handler.primaryEffects.add(StatusEffects.CONDUIT_POWER);

                    getPotions(handler);
                }).setDivisionFactor(2.5f);

        public static final Item BROWN_JELLO_CUP = createJelloCup(DyeColorantRegistry.BROWN)
                .createEffectData(handler -> {
                    handler.primaryEffects.add(StatusEffects.SPEED);

                    getPotions(handler);
                });

        public static final Item GREEN_JELLO_CUP = createJelloCup(DyeColorantRegistry.GREEN)
                .createEffectData(handler -> {
                    handler.primaryEffects.add(StatusEffects.HERO_OF_THE_VILLAGE);

                    getPotions(handler);
                });

        public static final Item RED_JELLO_CUP = createJelloCup(DyeColorantRegistry.RED)
                .createEffectData(handler -> {
                    handler.primaryEffects.add(StatusEffects.STRENGTH);

                    getPotions(handler);
                });

        public static final Item BLACK_JELLO_CUP = createJelloCup(DyeColorantRegistry.BLACK)
                .createEffectData(handler -> {
                    handler.primaryEffects.add(StatusEffects.RESISTANCE);
                    handler.primaryEffects.add(StatusEffects.SLOWNESS);

                    getPotions(handler);
                }).setDivisionFactor(1.5f);

        public static final List<Item> JELLO_CUPS = List
                .of(WHITE_JELLO_CUP, ORANGE_JELLO_CUP, MAGENTA_JELLO_CUP, LIGHT_BLUE_JELLO_CUP,
                        YELLOW_JELLO_CUP, LIME_JELLO_CUP, PINK_JELLO_CUP, GRAY_JELLO_CUP,
                        LIGHT_GRAY_JELLO_CUP, CYAN_JELLO_CUP, PURPLE_JELLO_CUP, BLUE_JELLO_CUP,
                        BROWN_JELLO_CUP, GREEN_JELLO_CUP, RED_JELLO_CUP, BLACK_JELLO_CUP);

        public static void getPotions(JelloCupCreationHandler handler){
            List<Potion> potions = new ArrayList<>();

            Registries.POTION.getEntrySet()
                    .forEach(entry -> {
                        Identifier id = entry.getKey().getValue();

                        List<StatusEffectInstance> effects = entry.getValue().getEffects();

                        if(effects.isEmpty() || handler.primaryEffects.size() != effects.size()) return;

                        int amountValid = 0;

                        for (StatusEffectInstance effect : effects) {
                            if((id.getNamespace().equals("minecraft") || id.getNamespace().equals(Jello.MODID)) && handler.primaryEffects.contains(effect.getEffectType())){
                                amountValid++;
                            }
                        }

                        if (amountValid != effects.size()) return;

                        potions.add(entry.getValue());
                    });

            if(potions.isEmpty()) return;

            handler.validPotions.addAll(
                    potions.stream()
                            .sorted(getPotionComparator())
                            .toList()
            );
        }


        public static Comparator<Potion> getPotionComparator(){
            return (o1, o2) -> {
                Map<Potion, AtomicInteger> map = new HashMap<>();

                map.put(o1, new AtomicInteger(0));
                map.put(o2, new AtomicInteger(0));

                List<StatusEffectInstance> o1Effects = o1.getEffects();
                List<StatusEffectInstance> o2Effects = o2.getEffects();

                for (StatusEffectInstance o1Effect : o1Effects) {
                    boolean foundEffectType = false;

                    for (StatusEffectInstance o2Effect : o2Effects) {
                        if(o1Effect.getEffectType() == o2Effect.getEffectType()){
                            foundEffectType = true;

                            float amplValue1 = o1Effect.getAmplifier();
                            float amplValue2 = o2Effect.getAmplifier();

                            float durValue1 = o1Effect.getDuration();
                            float durValue2 = o2Effect.getDuration();

                            float compareValue1 = Float.compare(amplValue1, amplValue2);

                            boolean checkDuration = false;

                            switch ((int) compareValue1) {
                                case 1 -> map.get(o1).incrementAndGet();
                                case -1 -> map.get(o2).incrementAndGet();
                                case 0 -> checkDuration = true;
                            }

                            if(checkDuration) {
                                float compareValue2 = Float.compare(durValue1, durValue2);

                                switch ((int) compareValue2) {
                                    case 1 -> map.get(o1).incrementAndGet();
                                    case -1 -> map.get(o2).incrementAndGet();
                                }
                            }
                        }
                    }

                    if(!foundEffectType){
                        map.get(o1).incrementAndGet();
                    }
                }

                return Integer.compare(map.get(o1).get(), map.get(o2).get());
            };
        }

        private static JelloCupItem createJelloCup(DyeColorant dyeColor) {
            return new JelloCupItem(
                    dyeColor,
                    ItemFunctions.copyFrom(new JelloItemSettings().group(ItemGroups.FOOD_AND_DRINK).rarity(Rarity.UNCOMMON).maxCount(16).fireproof()).food(createJelloBase().build()));
        }

        private static FoodComponent.Builder createJelloBase() {
            return new FoodComponent.Builder().alwaysEdible().snack().hunger(2).saturationModifier(0.9F).statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200), 1.0F);//.statusEffect(new StatusEffectInstance(JelloStatusEffectsRegistry.BOUNCE, 600), 1.0F);
        }
    }
}

