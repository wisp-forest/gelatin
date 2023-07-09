package io.wispforest.jello.item.jellocup;

import io.wispforest.jello.item.JelloItems;
import io.wispforest.owo.nbt.NbtKey;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class JelloCupCreationHandler {

    public static final Map<Item, JelloCupCreationHandler> ALL_CUP_DATA = new HashMap<>();

    public static final NbtKey<Identifier> JELLO_CUP_TYPE = new NbtKey<>("LinkedJelloCup", NbtKey.Type.IDENTIFIER);

    //----

    private final JelloCupItem linkedItem;

    public final Set<StatusEffect> primaryEffects = new LinkedHashSet<>();

    public final Map<Item, Potion> alternativeValidIngredient = new LinkedHashMap<>();

    public final Set<Potion> validPotions = new LinkedHashSet<>();

    private final List<ItemStack> allIterations = new ArrayList<>();

    //----

    public JelloCupCreationHandler(JelloCupItem item) {
        this.linkedItem = item;
    }

    @Nullable
    public static JelloCupCreationHandler getData(ItemStack stack){
        return stack.has(JELLO_CUP_TYPE)
                ? ALL_CUP_DATA.get(Registry.ITEM.get(stack.get(JELLO_CUP_TYPE)))
                : null;
    }

    public JelloCupItem getLinkedItem() {
        return this.linkedItem;
    }

    public List<ItemStack> getAllIterations(){
        if(allIterations.isEmpty()){
            for(Potion potion : validPotions) {
                if(potion == Potions.WATER) continue;

                ItemStack stack = JelloCupItem.reduceEffectDuration(getLinkedItem(), potion);

                if (stack != ItemStack.EMPTY) allIterations.add(stack);
            }
        }

        return allIterations;
    }

    //-------------------------------------------

    public boolean validSolutionCrafting(ItemStack stack) {
        var item = stack.getItem();

        if(!alternativeValidIngredient.isEmpty()) return alternativeValidIngredient.containsKey(stack.getItem());

        if (item == Items.POTION) {
            Potion potion = PotionUtil.getPotion(stack);

            return validPotions.contains(potion);
        }

        return false;
    }

    public ItemStack buildSolution(ItemStack ingredientStack) {
        ItemStack solutionStack = new ItemStack(JelloItems.GELATIN_SOLUTION, 1);

        if(ingredientStack.getItem() == Items.POTION){
            PotionUtil.setPotion(solutionStack, PotionUtil.getPotion(ingredientStack));
            PotionUtil.setCustomPotionEffects(solutionStack, PotionUtil.getCustomPotionEffects(ingredientStack));
        } else {
            Potion potion = alternativeValidIngredient.get(ingredientStack.getItem());

            PotionUtil.setPotion(solutionStack, potion);
            PotionUtil.setCustomPotionEffects(solutionStack, potion.getEffects());
        }

        solutionStack.put(JELLO_CUP_TYPE, Registry.ITEM.getId(this.getLinkedItem()));

        return solutionStack;
    }

    //-------------------------------------------

    public boolean validJelloCupCrafting(ItemStack stack) {
        return validPotions.contains(PotionUtil.getPotion(stack));
    }

    public ItemStack buildJelloCup(ItemStack ingredientStack){
        ItemStack stack = JelloCupItem.reduceEffectDuration(getLinkedItem(), ingredientStack);

        stack.setCount(3);

        return stack;
    }
}
