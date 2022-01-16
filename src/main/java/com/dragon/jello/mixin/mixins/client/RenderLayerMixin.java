package com.dragon.jello.mixin.mixins.client;

import com.dragon.jello.Util.ColorStateManager;
import com.dragon.jello.registry.ColorizeRegistry;
import com.dragon.jello.texture.GrayResourceTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

@Mixin(RenderLayer.class)
public class RenderLayerMixin {

    @Unique private static final Logger CURSED_LOGGER = LogManager.getLogger("RenderLayer");

    @Unique private static final Set<String> MAIN_BLACK_LIST = new HashSet<>(Arrays.stream(new String[]{"banner", "shield", "signs", "projectiles", "bed", "chest", "conduit", "bell"}).toList());
    @Unique private static final Set<String> NAME_VARIANT_BLACK_LIST = new HashSet<>(Arrays.stream(new String[]{"shulker", "fishing", "experience", "enchanting", "beacon", "end", "lead", "banner", "shield"}).toList());

    //-------------------------------------------------------------------------------------------------------

    @Unique private static List<String> pathComponents;

    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Util;memoize(Ljava/util/function/Function;)Ljava/util/function/Function;"))
    private static Function<Identifier, RenderLayer> wrapFactory(Function<Identifier, RenderLayer> factory) {
        Function<Identifier, RenderLayer> memoized = Util.memoize(factory);
        return (identifier) -> {
            MinecraftClient.getInstance().getTextureManager().getTexture(identifier);

            if (!defaultPlayerSkinTest(identifier) && isMinecraftSpecficBlacklisted(identifier) && ColorizeRegistry.isRegistered(identifier)) {
                AbstractTexture texture = MinecraftClient.getInstance().getTextureManager().getTexture(generateGrayScaleIdentifier(identifier));
                if (texture instanceof GrayResourceTexture) {
                    if (!ColorStateManager.GRAY_SCALE_TEST.isEmpty() && Boolean.TRUE.equals(ColorStateManager.GRAY_SCALE_TEST.peekLast())) {
                        identifier = generateGrayScaleIdentifier(identifier);
                    }
                }
            }

            return memoized.apply(identifier);
        };
    }

    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Util;memoize(Ljava/util/function/BiFunction;)Ljava/util/function/BiFunction;"))
    private static BiFunction<Identifier, Boolean, RenderLayer> wrapFactory(BiFunction<Identifier, Boolean, RenderLayer> factory) {
        BiFunction<Identifier, Boolean, RenderLayer> memoized = Util.memoize(factory);
        return (identifier, affectsOutline) -> {
            if (!defaultPlayerSkinTest(identifier) && isMinecraftSpecficBlacklisted(identifier) && ColorizeRegistry.isRegistered(identifier)) {
                AbstractTexture texture = MinecraftClient.getInstance().getTextureManager().getTexture(generateGrayScaleIdentifier(identifier));
                if(texture instanceof GrayResourceTexture){
                    if (!ColorStateManager.GRAY_SCALE_TEST.isEmpty() && Boolean.TRUE.equals(ColorStateManager.GRAY_SCALE_TEST.peekLast())) {
                        identifier = generateGrayScaleIdentifier(identifier);
                    }
                }
            }

            return memoized.apply(identifier, affectsOutline);
        };
    }

    @Unique
    private static Identifier generateGrayScaleIdentifier(Identifier identifier){
        String[] periodSplit = identifier.getPath().split("\\.");

        return new Identifier(periodSplit[0] + "_gray.png");
    }

    @Unique
    private static boolean isBlackListed(Identifier identifier){
        String name = pathComponents.get(pathComponents.size() - 1).split("\\.")[0];

        //First use generic black list
        if(MAIN_BLACK_LIST.contains(pathComponents.get(2)) && pathComponents.size() == 5 && pathComponents.get(3).matches("decor|armor")){
            //More Targeted removal
            String[] nameSplit = name.split("_");
            if(nameSplit.length >= 2 && (NAME_VARIANT_BLACK_LIST.contains(nameSplit[0]) || nameSplit[nameSplit.length-1].equals("grayscale"))){
                return true;
            }
        }

        return false;
    }

    @Unique
    private static boolean isMinecraftSpecficBlacklisted(Identifier identifier){
        if(identifier.getNamespace().equals("minecraft")){
            if(pathComponents.contains("entity")){
                CURSED_LOGGER.info("A Minecraft entity was found:" + identifier);
                if(!isBlackListed(identifier)) {
                    CURSED_LOGGER.info("And passed the blacklist:" + identifier);
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean defaultPlayerSkinTest(Identifier id){
        //TODO: Implement player rendering fixes at a later date

        pathComponents = Arrays.stream(id.getPath().split("/")).toList();
        String name = pathComponents.get(pathComponents.size() - 1).split("\\.")[0];

        return Objects.equals(name, "alex") || Objects.equals(name, "steve");
    }
}
