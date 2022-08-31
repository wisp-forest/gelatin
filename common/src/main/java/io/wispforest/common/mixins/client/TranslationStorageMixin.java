package io.wispforest.common.mixins.client;

import io.wispforest.common.events.TranslationInjectionEvent;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.resource.ResourceManager;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Map;

@Mixin(TranslationStorage.class)
public class TranslationStorageMixin {

    @Inject(method = "load(Lnet/minecraft/resource/Resource Manager;Ljava/util/List;)Lnet/minecraft/client/resource/language/TranslationStorage;", at = @At(value = "JUMP", opcode = Opcodes.GOTO, ordinal = 2), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void dynamicLangStuff(ResourceManager resourceManager, List<LanguageDefinition> definitions, CallbackInfoReturnable<TranslationStorage> cir, Map<String, String> map) {
        TranslationInjectionEvent.AFTER_LANGUAGE_LOAD.invoker().generateLanguageTranslations(new TranslationInjectionEvent.TranslationMapHelper(map, definitions));
    }

}