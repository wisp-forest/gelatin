package io.wispforest.gelatin.common.events;

import io.wispforest.gelatin.common.misc.GelatinConstants;
import io.wispforest.gelatin.common.util.VersatileLogger;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.loot.LootTable;
import net.minecraft.util.Identifier;

import java.util.Map;

public class LootTableInjectionEvent {
    public static final Event<AddLootTables> ADD_LOOT_TABLES_EVENT = EventFactory.createArrayBacked(AddLootTables.class, callbacks -> (helper) -> {
        VersatileLogger logger = new VersatileLogger("LootTableInjectionEvent", () -> GelatinConstants.DEBUG_ENV_VAR | GelatinConstants.DEBUG_ENV);

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
