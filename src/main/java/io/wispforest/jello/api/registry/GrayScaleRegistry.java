package io.wispforest.jello.api.registry;

import io.wispforest.jello.api.ducks.entity.DyeableEntity;
import io.wispforest.jello.api.ducks.entity.GrayScaleEntity;
import io.wispforest.jello.api.ducks.entity.RainbowEntity;
import io.wispforest.jello.Jello;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * {@link GrayScaleRegistry} is used to register either a Mod ID for global colorization or
 * to register specific entity's for colorization.
 * <br> <br>
 * After registering your entity, the default Colorization code will be already in place
 * if your entity extends {@link LivingEntity} in any way, but If you want different effects; just implement either {@link DyeableEntity} or {@link RainbowEntity} and Override the methods to change the effect of your entity.
 */
public class GrayScaleRegistry {

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

    public static Identifier getOrFindTexture(Entity entity, Identifier defaultIdentifier) {
        if (entity instanceof GrayScaleEntity grayScaleEntity && grayScaleEntity.isGrayScaled(entity)) {
            return createGrayScaleID(defaultIdentifier);
        } else {
            return defaultIdentifier;
        }
    }

    public static Identifier createGrayScaleID(Identifier defaultIdentifier) {
        String[] array = defaultIdentifier.getPath().split("/");
        String[] array2 = array[array.length - 1].split("\\.");

        String path = array[0];

        for (int i = 1; i < array.length - 1; i++) {
            path = path.concat("/" + array[i]);
        }

        path = path + "/" + array2[0] + "_grayscale.png";

        return Jello.id(path);
    }
}
