package io.wispforest.jello.item.jellocup;

import io.wispforest.gelatin.dye_entries.item.ColoredItem;
import io.wispforest.gelatin.dye_registry.DyeColorant;
import io.wispforest.jello.item.JelloItems;
import io.wispforest.jello.misc.JelloPotions;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Rarity;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class JelloCupItem extends ColoredItem {

    /*
     * StatusEffects.SLOW_FALLING        -> WHITE_JELLO_CUP      |
     * StatusEffects.FIRE_RESISTANCE     -> ORANGE_JELLO_CUP     |
     * StatusEffects.HEALTH_BOOST        -> MAGENTA_JELLO_CUP    | * Has Modded Potion Item : Dragon Health
     * StatusEffects.JUMP_BOOST          -> LIGHT_BLUE_JELLO_CUP |
     * Custom: [Golden Apple Based]      -> YELLOW_JELLO_CUP     | * Uses Golden Apple or Enchanted Golden Apple
     * StatusEffects.LUCK                -> LIME_JELLO_CUP       |
     * StatusEffects.INSTANT_HEALTH      -> PINK_JELLO_CUP       |
     * StatusEffects.INVISIBILITY        -> GRAY_JELLO_CUP       |
     * StatusEffects.DOLPHINS_GRACE      -> LIGHT_GRAY_JELLO_CUP | * No Vanilla Potion Item : { Place within Underwater chests or as drops? }
     * StatusEffects.WATER_BREATHING     -> CYAN_JELLO_CUP       |
     * StatusEffects.NIGHT_VISION        -> PURPLE_JELLO_CUP     |
     * StatusEffects.CONDUIT_POWER       -> BLUE_JELLO_CUP       | * No Vanilla Potion Item : { Place within Underwater chests or as drops? }
     * StatusEffects.SPEED               -> BROWN_JELLO_CUP      |
     * StatusEffects.HERO_OF_THE_VILLAGE -> GREEN_JELLO_CUP      | * No Vanilla Potion Item : { Make obtainable through certain chests in the end or nether? }
     * StatusEffects.STRENGTH            -> RED_JELLO_CUP        |
     * StatusEffects.RESISTANCE          -> BLACK_JELLO_CUP      | * Used Turtle Master Potion : (Turtle Master or Enchanted Golden Apple)
     */

    private float divisionFactor = 3f;

    public JelloCupItem(DyeColorant dyeColor, Settings settings) {
        super(dyeColor, settings, value -> value > 1);

        JelloCupCreationHandler.ALL_CUP_DATA.put(this, new JelloCupCreationHandler(this));
    }

    public JelloCupItem createEffectData(Consumer<JelloCupCreationHandler> consumer){
        consumer.accept(JelloCupCreationHandler.ALL_CUP_DATA.get(this));

        return this;
    }

    public JelloCupItem setDivisionFactor(float divisionFactor){
        this.divisionFactor = divisionFactor;

        return this;
    }

    //----

    @Override
    public ItemStack getDefaultStack() {
        JelloCupCreationHandler handler = JelloCupCreationHandler.ALL_CUP_DATA.get(this);

        return handler.getAllIterations().get(0);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        PlayerEntity playerEntity = user instanceof PlayerEntity p ? p : null;

        if (playerEntity instanceof ServerPlayerEntity serverPlayerEntity) {
            Criteria.CONSUME_ITEM.trigger(serverPlayerEntity, stack);
        }

        if (!world.isClient) {
            for(StatusEffectInstance statusEffectInstance : PotionUtil.getCustomPotionEffects(stack)) {
                if (statusEffectInstance.getEffectType().isInstant()) {
                    statusEffectInstance.getEffectType().applyInstantEffect(playerEntity, playerEntity, user, statusEffectInstance.getAmplifier(), 1.0);
                } else {
                    user.addStatusEffect(new StatusEffectInstance(statusEffectInstance));
                }
            }
        }

        if (playerEntity != null) {
            playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));

            if (!playerEntity.getAbilities().creativeMode) stack.decrement(1);
        }

        return super.finishUsing(stack, world, user);
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        if(this != JelloItems.JelloCups.YELLOW_JELLO_CUP) return this.getTranslationKey();

        return "item.yellow_jello_cup" + (PotionUtil.getPotion(stack) == JelloPotions.ENCHANTED_GOLDEN_LIQUID ? ".enchanted" : "");
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        if(this != JelloItems.JelloCups.YELLOW_JELLO_CUP) return super.getRarity(stack);

        return PotionUtil.getPotion(stack) == JelloPotions.ENCHANTED_GOLDEN_LIQUID ? Rarity.EPIC : Rarity.RARE;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        ItemStack potionRemoved = stack.copy();

        if(potionRemoved.hasNbt()) potionRemoved.removeSubNbt("Potion");

        PotionUtil.buildTooltip(potionRemoved, tooltip, 1.0F);
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (!this.isIn(group)) return;

        JelloCupCreationHandler handler = JelloCupCreationHandler.ALL_CUP_DATA.get(this);

        stacks.addAll(handler.getAllIterations());
    }

    public static ItemStack reduceEffectDuration(JelloCupItem item, ItemStack potionStack) {
        Potion potion = PotionUtil.getPotion(potionStack);

        return reduceEffectDuration(item, potion);
    }

    public static ItemStack reduceEffectDuration(JelloCupItem item, Potion potion){
        List<StatusEffectInstance> instances = new ArrayList<>(potion.getEffects());

        ItemStack jelloCup = new ItemStack(item);

        if (instances.isEmpty()) return ItemStack.EMPTY;

        instances = reduceEffectDuration(instances, item.divisionFactor);

        PotionUtil.setCustomPotionEffects(jelloCup, instances);

        PotionUtil.setPotion(jelloCup, potion);

        return jelloCup;
    }

    public static List<StatusEffectInstance> reduceEffectDuration(List<StatusEffectInstance> instances, float divisionNum){
        return instances.stream()
                .map(instance -> reduceEffectDuration(instance, divisionNum))
                .toList();
    }

    public static StatusEffectInstance reduceEffectDuration(StatusEffectInstance instance, float divisionNum){
        return new StatusEffectInstance(
                instance.getEffectType(),
                Math.round(instance.getDuration() / divisionNum),
                instance.getAmplifier(),
                instance.isAmbient(),
                instance.shouldShowParticles(),
                instance.shouldShowIcon()
        );
    }

}
