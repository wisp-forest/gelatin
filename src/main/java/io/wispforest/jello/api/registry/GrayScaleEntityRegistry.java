package io.wispforest.jello.api.registry;

import io.wispforest.jello.api.ducks.entity.DyeableEntity;
import io.wispforest.jello.api.ducks.entity.GrayScaleEntity;
import io.wispforest.jello.api.ducks.entity.RainbowEntity;
import io.wispforest.jello.Jello;
import io.wispforest.jello.api.util.GrayScaleHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.*;

/**
 * {@link GrayScaleEntityRegistry} is used to control GrayScalability of an Entity Type or Mod thru its ModId
 */
public class GrayScaleEntityRegistry implements GrayScaleHelper<EntityType<?>> {

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

    @Override
    public String getGrayscaleSuffix() {
        return "_grayscale.png";
    }
}
