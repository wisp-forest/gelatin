package io.wispforest.gelatin.common.pas;

import io.wispforest.gelatin.common.util.VersatileLogger;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class PlayerActionStateHelper {

    private static final VersatileLogger LOGGER = new VersatileLogger("PlayerActionStatesStorage");

    private static final Map<Identifier, ActionStateFactory> registeredFactories = new HashMap<>();

    public static void registerAction(Identifier syncID, ActionStateFactory factory){
        if(registeredFactories.containsKey(syncID)){
            LOGGER.failMessage("An factory with the same ID was already registered, meaning current one will be ignored! [ID: {}]", syncID);

            return;
        }

        registeredFactories.put(syncID, factory);
    }

    public static Map<Identifier, ActionStateFactory> getRegisteredFactories(){
        return Map.copyOf(registeredFactories);
    }

    public interface ActionStateFactory {
        ActionState createState();
    }
}
