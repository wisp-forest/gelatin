package com.dragon.jello.lib.registry;

import com.dragon.jello.common.Jello;
import com.dragon.jello.mixin.ducks.DyeableEntity;
import com.dragon.jello.mixin.ducks.GrayScaleEntity;
import com.dragon.jello.mixin.ducks.RainbowEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * {@link GrayScaleRegistry} is used to register either a Mod ID for global colorization or
 * to register specific entity's for colorization.
 * <br> <br>
 * After registering your entity, the default Colorization code will be already in place
 * if your entity extends {@link LivingEntity} in any way, but If you want different effects; just implement either {@link DyeableEntity} or {@link RainbowEntity} and Override the methods to change the effect of your entity.
 */
public class GrayScaleRegistry {
    private static final Logger LOGGER = LogManager.getLogger(GrayScaleRegistry.class);

    private static final Map<EntityType<?>, Identifier> GRAYSCALABLE_ENTITIES = new HashMap<>();

    /**
     * Registers a give Entity texture and Entity Type for DyeAbility
     *
     * @param entityTexture     Texture Location for the given entity type
     * @param entityType        The entity type being enabled for colorization
     */
    public static void registerGrayScalable(Identifier entityTexture, EntityType<?> entityType){
        GRAYSCALABLE_ENTITIES.put(entityType, entityTexture);
    }

    /**
     * [Registry Check ONLY] <br>
     * Checks if a given Entity is registered for GrayScaleAbility
     *
     * @param entity    Possible Dyeable Entity
     */
    public static boolean isRegistered(Entity entity){
        if(GRAYSCALABLE_ENTITIES.containsKey(entity.getType())){
            return true;
        } else if(Objects.equals(Registry.ENTITY_TYPE.getId(entity.getType()).getNamespace(), "minecraft")) {
            return true;
        }

        return false;
    }

    /**
     *
     * @param entity
     */
    public static Identifier getTexture(Entity entity){
        return GRAYSCALABLE_ENTITIES.get(entity.getType());
    }

    public static Identifier getOrFindTexture(Entity entity, Identifier defaultIdentifier){
        if(entity instanceof GrayScaleEntity grayScaleEntity && grayScaleEntity.isGrayScaled(entity)) {
            Identifier identifierGrayScale = GRAYSCALABLE_ENTITIES.get(entity.getType());

            if(identifierGrayScale != null){
                return identifierGrayScale;
            }

            return createGrayScaleID(defaultIdentifier);

        }else{
            return defaultIdentifier;
        }
    }

    public static Identifier createGrayScaleID(Identifier defaultIdentifier){
        String[] array = defaultIdentifier.getPath().split("/");
        String[] array2 = array[array.length - 1].split("\\.");

        String path = array[0];

        for(int i = 1; i < array.length - 1; i++){
            path = path.concat("/" + array[i]);
        }

        path = path + "/" + array2[0] + "_grayscale.png";

        return new Identifier(Jello.MODID, path);
    }
}
