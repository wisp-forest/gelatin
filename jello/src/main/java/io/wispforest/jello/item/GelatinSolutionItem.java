package io.wispforest.jello.item;

import io.wispforest.jello.misc.JelloPotions;
import net.minecraft.client.color.item.ItemColorProvider;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GelatinSolutionItem extends Item implements ItemColorProvider {

    private static final Text NONE_TEXT = Text.translatable("effect.none").formatted(Formatting.GRAY);

    public GelatinSolutionItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack getDefaultStack() {
        return PotionUtil.setPotion(super.getDefaultStack(), Potions.WATER);
    }

    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        if(tintIndex == 0) return -1;

        return PotionUtil.getColor(stack);
    }

    @Override
    public void postProcessNbt(NbtCompound nbt) {
        super.postProcessNbt(nbt);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        List<StatusEffectInstance> list2 = PotionUtil.getPotion(stack.getNbt()).getEffects();
        float durationMultiplier = 1.0F;

        if (list2.isEmpty()) {
            tooltip.add(NONE_TEXT);
        } else {
            tooltip.add(
                    Text.literal("Effect" + ( list2.size() > 1 ? "s" : "") + ": ")
                            .formatted(Formatting.GRAY)
            );

            for(StatusEffectInstance statusEffectInstance : list2) {
                StatusEffect statusEffect = statusEffectInstance.getEffectType();

                MutableText innerText = Text.translatable(statusEffectInstance.getTranslationKey());

                if (statusEffectInstance.getAmplifier() > 0) {
                    innerText = Text.translatable("potion.withAmplifier", innerText, Text.translatable("potion.potency." + statusEffectInstance.getAmplifier()));
                }

                if (statusEffectInstance.getDuration() > 20) {
                    innerText = Text.translatable("potion.withDuration", innerText, StatusEffectUtil.getDurationText(statusEffectInstance, durationMultiplier));
                }

                innerText.formatted(statusEffect.getCategory().getFormatting());

                tooltip.add(innerText);
            }
        }
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        Potion potion = PotionUtil.getPotion(stack);

        if(potion == JelloPotions.GOLDEN_LIQUID){
            return Rarity.RARE;
        } else if(potion == JelloPotions.ENCHANTED_GOLDEN_LIQUID){
            return Rarity.EPIC;
        } else {
            return super.getRarity(stack);
        }
    }
}
