package io.wispforest.gelatin.common.mixins;

import com.google.gson.JsonElement;
import io.wispforest.gelatin.common.events.LootTableInjectionEvent;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.loot.LootDataType;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(LootManager.class)
public class LootManagerMixin {

    @Inject(method = "load", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static <T> void jello$attemptLootTableInjection(LootDataType<T> type, ResourceManager resourceManager, Executor executor, Map<LootDataType<?>, Map<Identifier, ?>> results, CallbackInfoReturnable<CompletableFuture<?>> cir, Map<Identifier, T> map){
        //this.tables = CustomSheepLootTables.initSheepLootTables(this.tables);

        if(type != LootDataType.LOOT_TABLES) return;

        //Map<Identifier, LootTable> currentMap = new HashMap<>(this.tables);

        LootTableInjectionEvent.ADD_LOOT_TABLES_EVENT.invoker().afterResourceLoad(new LootTableInjectionEvent.LootTableMapHelper((Map<Identifier, LootTable>) map));

        //this.tables = Map.copyOf(currentMap);
    }

}
