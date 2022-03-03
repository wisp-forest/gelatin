package com.dragon.jello.main.common.items;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;

public class JelloFoodComponents {
    public static final FoodComponent SUGAR_CUP = new FoodComponent.Builder().alwaysEdible().snack().hunger(1).saturationModifier(0F).build();

    public static final FoodComponent WHITE_JELLO_CUP = createJelloBase().statusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 200 * 2), 1.0F).build();
    public static final FoodComponent ORANGE_JELLO_CUP = createJelloBase().statusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 200 * 2), 1.0F).build();
    public static final FoodComponent MAGENTA_JELLO_CUP = createJelloBase().statusEffect(new StatusEffectInstance(StatusEffects.HEALTH_BOOST, 200 * 3), 1.0F).build();
    public static final FoodComponent LIGHT_BLUE_JELLO_CUP = createJelloBase().statusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, 200 * 2), 1.0F).build();
    public static final FoodComponent YELLOW_JELLO_CUP = createJelloBase().statusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 200 * 2), 1.0F).build();
    public static final FoodComponent LIME_JELLO_CUP = createJelloBase().statusEffect(new StatusEffectInstance(StatusEffects.LUCK, (int)(200 * 1.5)), 1.0F).build();
    public static final FoodComponent PINK_JELLO_CUP = createJelloBase().statusEffect(new StatusEffectInstance(StatusEffects.INSTANT_HEALTH, 200), 1.0F).build();
    public static final FoodComponent GRAY_JELLO_CUP = createJelloBase().statusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, (int)(200 * 2.5)), 1.0F).build();
    public static final FoodComponent LIGHT_GRAY_JELLO_CUP = createJelloBase().statusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 200 * 2), 1.0F).build();
    public static final FoodComponent CYAN_JELLO_CUP = createJelloBase().statusEffect(new StatusEffectInstance(StatusEffects.WATER_BREATHING, (int)(200 * 2.5)), 1.0F).build();
    public static final FoodComponent PURPLE_JELLO_CUP = createJelloBase().statusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 200 * 4), 1.0F).build();
    public static final FoodComponent BLUE_JELLO_CUP = createJelloBase().statusEffect(new StatusEffectInstance(StatusEffects.DOLPHINS_GRACE, (int)(200 * 2.5)), 1.0F).build();
    public static final FoodComponent BROWN_JELLO_CUP = createJelloBase().statusEffect(new StatusEffectInstance(StatusEffects.SPEED, (int)(200 * 2.5)), 1.0F).build();
    public static final FoodComponent GREEN_JELLO_CUP = createJelloBase().statusEffect(new StatusEffectInstance(StatusEffects.HERO_OF_THE_VILLAGE, 200), 1.0F).build();
    public static final FoodComponent RED_JELLO_CUP = createJelloBase().statusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, (int)(200 * 1.5)), 1.0F).build();
    public static final FoodComponent BLACK_JELLO_CUP = createJelloBase().statusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, (int)(200 * 2.5)), 1.0F).build();


    private static FoodComponent.Builder createJelloBase(){
        return new FoodComponent.Builder().alwaysEdible().snack().hunger(2).saturationModifier(0.9F).statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200), 1.0F);//.statusEffect(new StatusEffectInstance(JelloStatusEffectsRegistry.BOUNCE, 600), 1.0F);
    }
}
