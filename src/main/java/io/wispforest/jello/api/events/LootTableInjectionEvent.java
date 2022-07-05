package io.wispforest.jello.api.events;

import io.wispforest.jello.Jello;
import io.wispforest.jello.api.util.VersatileLogger;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.loot.LootTable;
import net.minecraft.util.Identifier;

import java.util.Map;

public class LootTableInjectionEvent {
    public static final Event<AddLootTables> ADD_LOOT_TABLES_EVENT = EventFactory.createArrayBacked(AddLootTables.class, callbacks -> (helper) -> {
        VersatileLogger logger = new VersatileLogger("LootTableInjectionEvent", () -> Jello.DEBUG_ENV_VAR | Jello.DEBUG_ENV);

        for(AddLootTables callback : callbacks){
            callback.afterResourceLoad(helper);
        }

        logger.stopTimerPrint("loot table injection took ");
    });

    public interface AddLootTables{
        void afterResourceLoad(LootTableMapHelper helper);
    }

    public static class LootTableMapHelper {
        private final Map<Identifier, LootTable> lootTableData;

        public LootTableMapHelper(Map<Identifier, LootTable> lootTableData){
            this.lootTableData = lootTableData;
        }

        public boolean addLootTable(Identifier key, LootTable value){
            if(!lootTableData.containsKey(key)){
                lootTableData.put(key, value);

                return true;
            }

            return false;
        }
    }
}
