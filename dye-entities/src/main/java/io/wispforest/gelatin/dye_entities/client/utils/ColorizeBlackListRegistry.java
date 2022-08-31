package io.wispforest.gelatin.dye_entities.client.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ColorizeBlackListRegistry {
    public static final Set<String> MODID_BLACKLIST = new HashSet<>();
    public static final Map<Identifier, EntityType<?>> COLORABLE_ENTITIES_BLACKLIST = new HashMap<>();

    private static final String ALL_ENTITIES = "all_living_entities";

    public static void setModidBlacklist(String modid) {
        setEntityTypeBlacklist(new Identifier(modid, ALL_ENTITIES), null);
    }

    public static void setEntityTypeBlacklist(Identifier entityTexture, @Nullable EntityType<?> entityType) {
        if (entityType == null && entityTexture.getPath().equals(ALL_ENTITIES)) {
            MODID_BLACKLIST.add(entityTexture.getNamespace());
        } else if (entityType != null && entityTexture != null) {
            COLORABLE_ENTITIES_BLACKLIST.put(entityTexture, entityType);
        } else {
            throw new IllegalArgumentException("A Identifier was registered without a Entity Type: " + entityTexture);
        }
    }

    public static boolean isBlackListed(Entity entity) {
        return MODID_BLACKLIST.contains(Registry.ENTITY_TYPE.getId(entity.getType()).getNamespace()) || COLORABLE_ENTITIES_BLACKLIST.containsValue(entity.getType());
    }

    public static boolean isTextureBlacklisted(Identifier entityTexture) {
        return COLORABLE_ENTITIES_BLACKLIST.containsKey(entityTexture);
    }

}
