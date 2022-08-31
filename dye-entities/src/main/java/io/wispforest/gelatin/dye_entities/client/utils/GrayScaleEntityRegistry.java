package io.wispforest.gelatin.dye_entities.client.utils;

import io.wispforest.gelatin.common.misc.GelatinConstants;
import io.wispforest.gelatin.dye_entities.ducks.GrayScaleEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashSet;
import java.util.Set;

/**
 * {@link GrayScaleEntityRegistry} is used to control GrayScalability of an Entity Type or Mod thru its ModId
 */
public class GrayScaleEntityRegistry {

    public static GrayScaleEntityRegistry INSTANCE = new GrayScaleEntityRegistry();

    public static final Set<String> GRAYSCALABLE_MODID_BLACKLIST = new HashSet<>();
    public static final Set<EntityType<?>> GRAYSCALABLE_ENTITIES = new HashSet<>();

    /**
     * Register an EntityType to be always GrayScaled
     */
    public static void registerGrayScalable(EntityType<?> entityType) {
        GRAYSCALABLE_ENTITIES.add(entityType);
    }

    /**
     * Set a Modid to be BlackListed from being GrayScaled
     */
    public static void setModidBlacklist(String modid) {
        GRAYSCALABLE_MODID_BLACKLIST.add(modid);
    }

    /**
     * Checks if a given Entity is registered for GrayScaleAbility
     */
    public static boolean isRegistered(Entity entity) {
        return GRAYSCALABLE_ENTITIES.contains(entity.getType());
    }

    /**
     * Checks if a given Entity is Blacklisted for GrayScaleAbility
     */
    public static boolean isBlacklisted(Entity entity) {
        return GRAYSCALABLE_MODID_BLACKLIST.contains(Registry.ENTITY_TYPE.getId(entity.getType()).getNamespace());
    }

    public Identifier getOrFindTexture(Entity entity, Identifier defaultIdentifier) {
        if (entity instanceof GrayScaleEntity grayScaleEntity && grayScaleEntity.isGrayScaled(entity)) {
            return INSTANCE.createGrayScaleID(defaultIdentifier);
        } else {
            return defaultIdentifier;
        }
    }

    public Identifier createGrayScaleID(Identifier defaultIdentifier) {
        String[] array = defaultIdentifier.getPath().split("/");
        String[] array2 = array[array.length - 1].split("\\.");

        String path = array[0];

        for (int i = 1; i < array.length - 1; i++) {
            path = path.concat("/" + array[i]);
        }

        path = path + "/" + array2[0] + "_grayscale.png";

        return GelatinConstants.id(path);
    }
}
