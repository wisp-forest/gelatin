package io.wispforest.jello.api.registry;

import io.wispforest.jello.api.mixin.ducks.DyeableEntity;
import io.wispforest.jello.api.mixin.ducks.RainbowEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link ColorizeRegistry} is used to register either a Mod ID for global colorization or
 * to register specific entity's for colorization.
 * <br> <br>
 * After registering your entity, the default Colorization code will be already in place
 * if your entity extends {@link LivingEntity} in any way, but If you want different effects; just implement either {@link DyeableEntity} or {@link RainbowEntity} and Override the methods to change the effect of your entity.
 *
 */
public class ColorizeRegistry {
    private static final Logger LOGGER = LogManager.getLogger(ColorizeRegistry.class);

    private static final ArrayList<String> MODID_WHITELIST = new ArrayList<>();
    private static final Map<Identifier, EntityType<?>> COLORABLE_ENTITIES = new HashMap<>();

    private static final String ALL_ENTITIES = "all_living_entities";

    /**
     * Registers a give Mod ID to enable global DyeAbility for a specified namespace
     *
     * @param modid     String represention of your Mod ID
     */
    public static void registerColorable(String modid){
        registerColorable(new Identifier(modid,ALL_ENTITIES), null);
    }

    /**
     * Registers a give Entity texture and Entity Type for DyeAbility
     *
     * @param entityTexture     Texture Location for the given entity type
     * @param entityType        The entity type being enabled for colorization
     */
    public static void registerColorable(Identifier entityTexture, EntityType<?> entityType){
        if(entityType == null && entityTexture.getPath().equals(ALL_ENTITIES)){
            MODID_WHITELIST.add(entityTexture.getNamespace());
        }
        else if(entityType != null && entityTexture != null){
            COLORABLE_ENTITIES.put(entityTexture, entityType);
        }
        else{
            throw new IllegalArgumentException("A Identifier was registered without a Entity Type: " + entityTexture);
        }
    }

    /**
     * [Registry Check ONLY] <br>
     * Checks if a given Texture Identifier is registered within
     * Entity Map for DyeAbility or within the Mod ID White List
     * @param entityTexture     Entity Texture Identifier
     */
    @ApiStatus.Internal
    public static boolean isRegistered(Identifier entityTexture){
        if(MODID_WHITELIST.contains(entityTexture.getNamespace())){
            return true;
        }
        else{
            return isTextureRegisterd(entityTexture);
        }
    }

    /**
     * [Registry Check ONLY] <br>
     * Checks if a given Entity is registered for DyeAbility
     *
     * @param entity    Possible Dyeable Entity
     */
    @ApiStatus.Internal
    public static boolean isRegistered(Entity entity){
        if(COLORABLE_ENTITIES.containsValue(entity.getType())){
            return true;
        }else{
            return MODID_WHITELIST.contains(Registry.ENTITY_TYPE.getId(entity.getType()).getNamespace());
        }
    }

    /**
     * [Registry Check ONLY] <br>
     * Checks if a given Texture Identifier is registered within Entity Map for DyeAbility
     *
     * @param entityTexture      Entity Texture Identifier
     */
    @ApiStatus.Internal
    public static boolean isTextureRegisterd(Identifier entityTexture){
        return COLORABLE_ENTITIES.containsKey(entityTexture);
    }

    /**
     * [Registry Check ONLY] <br>
     * Gets the entity type mapped to the Identifier given if it exists
     *
     * @param entityTexture      Entity Texture Identifier
     */
    @ApiStatus.Internal
    public static EntityType<?> getRegisteredEntityType(Identifier entityTexture){
        if(isTextureRegisterd(entityTexture)){
            return COLORABLE_ENTITIES.get(entityTexture);
        }
        else{
            return null;
        }
    }

    static{
        registerColorable("minecraft");
    }

}
