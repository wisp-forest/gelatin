package io.wispforest.gelatin.dye_entries.client;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashSet;
import java.util.Set;

/**
 * Stores info about Valid and Invalid info for Gelatins Redirect Model System
 */
public class VariantModelRedirectStorage {

    public static final Set<Identifier> VALID_IDENTIFIERS = new HashSet<>();
    public static final Set<String> VALID_NAMESPACE = new HashSet<>();

    public static final Set<Identifier> INVALID_IDENTIFIERS = new HashSet<>();

    /**
     * Add a Modid as valid for Model Redirect System
     * @param modid
     */
    public static void addValidModID(String modid) {
        VALID_NAMESPACE.add(modid);
    }

    /**
     * Add an Identifier as valid for Model Redirect System
     * @param id
     */
    public static void addValidID(Identifier id) {
        VALID_IDENTIFIERS.add(id);
    }

    /**
     * Add an Identifier as invalid for Model Redirect System preventing such to be redirected
     * @param id
     */
    public static void invalidateID(Identifier id) {
        INVALID_IDENTIFIERS.add(id);
    }

    @ApiStatus.Internal
    public static boolean shouldRedirectModelResource(Identifier identifier) {
        if(INVALID_IDENTIFIERS.contains(identifier)) return false;

        return VALID_NAMESPACE.contains(identifier.getNamespace()) || VALID_IDENTIFIERS.contains(identifier);
    }
}
