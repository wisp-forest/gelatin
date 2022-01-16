package com.dragon.jello.mixin.mixins.client;

import com.dragon.jello.Util.ColorStateManager;
import com.dragon.jello.registry.ColorizeRegistry;
import com.dragon.jello.texture.GrayResourceTexture;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.*;

@Debug(export = true)
@Mixin(TextureManager.class)
public abstract class TextureManagerMixin {
    @Unique private static final Logger CURSED_LOGGER = LogManager.getLogger("TextManager");

    @Unique private static final Set<String> MAIN_BLACK_LIST = new HashSet<>(Arrays.stream(new String[]{"banner", "shield", "signs", "projectiles", "bed", "chest", "conduit", "bell"}).toList());
    @Unique private static final Set<String> NAME_VARIANT_BLACK_LIST = new HashSet<>(Arrays.stream(new String[]{"shulker", "fishing", "experience", "enchanting", "beacon", "end", "lead", "banner", "shield"}).toList());

    //-------------------------------------------------------------------------------------------------------

    @Unique private List<String> pathComponents;

    @Shadow @Final private Map<Identifier, AbstractTexture> textures;
    @Shadow public abstract void registerTexture(Identifier id, AbstractTexture texture);

    @Inject(method = "getTexture", at = @At(value = "RETURN", shift = At.Shift.BY, by = -1), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void doubleTextureRegister(Identifier id, CallbackInfoReturnable<AbstractTexture> cir, AbstractTexture abstractTexture) {
        Identifier grayScaleId = generateGrayScaleIdentifier(id);
        AbstractTexture abstractTextureGray = (AbstractTexture) this.textures.get(grayScaleId);

        if (abstractTextureGray == null) {
            if (!defaultPlayerSkinTest(id) && isMinecraftSpecficBlacklisted(id) && ColorizeRegistry.isRegistered(id)) {
                abstractTextureGray = new GrayResourceTexture(grayScaleId, (ResourceTexture) abstractTexture);
                this.registerTexture(grayScaleId, abstractTextureGray);

//                if (!ColorStateManager.GRAY_SCALE_TEST.isEmpty() && Boolean.TRUE.equals(ColorStateManager.GRAY_SCALE_TEST.peekLast())) {
//                    cir.setReturnValue(abstractTextureGray);
//                }
            }
        }
//        else{
//            if (!ColorStateManager.GRAY_SCALE_TEST.isEmpty() && Boolean.TRUE.equals(ColorStateManager.GRAY_SCALE_TEST.peekLast())) {
//                cir.setReturnValue(abstractTextureGray);
//            }
//        }
    }

    @Unique
    private boolean isBlackListed(Identifier identifier){
        String name = this.pathComponents.get(pathComponents.size() - 1).split("\\.")[0];

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
    private boolean isMinecraftSpecficBlacklisted(Identifier identifier){
        if(identifier.getNamespace().equals("minecraft")){
            if(this.pathComponents.contains("entity")){
                //CURSED_LOGGER.info("A Minecraft entity was found:" + identifier);
                if(!isBlackListed(identifier)) {
                    //CURSED_LOGGER.info("And passed the blacklist:" + identifier);
                    return true;
                }
            }
        }

        return false;
    }

    private boolean defaultPlayerSkinTest(Identifier id){
        //TODO: Implement player rendering fixes at a later date

        this.pathComponents = Arrays.stream(id.getPath().split("/")).toList();
        String name = pathComponents.get(pathComponents.size() - 1).split("\\.")[0];

        return Objects.equals(name, "alex") || Objects.equals(name, "steve");
    }

    @Unique
    private static Identifier generateGrayScaleIdentifier(Identifier identifier){
        String[] periodSplit = identifier.getPath().split("\\.");

        return new Identifier(periodSplit[0] + "_gray.png");
    }
}
