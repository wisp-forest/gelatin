package io.wispforest.common.mixins;

import com.google.gson.JsonElement;
import io.wispforest.common.events.LootTableInjectionEvent;
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

import java.util.HashMap;
import java.util.Map;

@Mixin(LootManager.class)
public class LootManagerMixin {

    @Shadow private Map<Identifier, LootTable> tables;

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V", at = @At("TAIL"))
    private void jello$injectLootTables(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci) {
        //this.tables = CustomSheepLootTables.initSheepLootTables(this.tables);
        Map<Identifier, LootTable> currentMap = new HashMap<>(this.tables);

        LootTableInjectionEvent.ADD_LOOT_TABLES_EVENT.invoker().afterResourceLoad(new LootTableInjectionEvent.LootTableMapHelper(currentMap));

        this.tables = Map.copyOf(currentMap);
    }

}
