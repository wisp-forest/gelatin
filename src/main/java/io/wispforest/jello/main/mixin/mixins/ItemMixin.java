package io.wispforest.jello.main.mixin.mixins;

import io.wispforest.jello.main.common.Jello;
import io.wispforest.jello.main.mixin.ducks.InInventoryCraftingPacket;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.ClickType;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(Item.class)
public class ItemMixin {

    private static final Logger LOGGER_INV = LogManager.getLogger("InInventoryTest");

    private static final boolean HARDCODE_DISABLE = true;

    @Inject(method = "onClicked", at = @At(value = "HEAD"), cancellable = true)
    public void testMethod(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference, CallbackInfoReturnable<Boolean> cir){
        if(FabricLoader.getInstance().isDevelopmentEnvironment() && !HARDCODE_DISABLE) {
            if (clickType == ClickType.RIGHT && !(stack.isEmpty() || otherStack.isEmpty())) {

                World world = player.getWorld();

                NbtList debugList = new NbtList();
                LOGGER_INV.info("\n");
                LOGGER_INV.info("----------------------------------------------------------------------");
                LOGGER_INV.info("[ClientSide] B:" + player.getInventory().writeNbt(debugList).toString());

                CraftingInventory craftingInventory = new CraftingInventory(new ScreenHandler(null, -1) {
                    @Override
                    public boolean canUse(PlayerEntity player) {
                        return false;
                    }
                }, 2, 1);

                int firstSlotNumber = slot.getIndex();

                craftingInventory.setStack(0, stack);
                craftingInventory.setStack(1, otherStack);

                List<CraftingRecipe> possibleRecipes = world.getRecipeManager().getAllMatches(RecipeType.CRAFTING, craftingInventory, world).stream().filter(craftingRecipe -> craftingRecipe instanceof ShapelessRecipe).toList();

                if (!possibleRecipes.isEmpty()) {


                    Jello.CHANNEL.clientHandle().send(new InInventoryCraftingPacket.CraftPacket(firstSlotNumber, otherStack, possibleRecipes.get(0).getId()));

                    cir.setReturnValue(true);
                }
            }
        }
    }
}
