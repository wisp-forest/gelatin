package io.wispforest.jello.mixins.data;

import com.google.gson.JsonObject;
import io.wispforest.jello.misc.pond.CookingRecipeJsonBuilderExtension;
import io.wispforest.jello.misc.pond.CookingRecipeJsonProviderExtension;
import net.minecraft.data.server.recipe.CookingRecipeJsonBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Optional;

@Mixin(CookingRecipeJsonBuilder.class)
public abstract class CookingRecipeJsonBuilderMixin implements CookingRecipeJsonBuilderExtension {

    @Unique private Optional<Integer> resultAmount = Optional.empty();

    @Override
    public CookingRecipeJsonBuilder setResultAmount(int amount) {
        this.resultAmount = Optional.of(amount);

        return (CookingRecipeJsonBuilder) (Object) this;
    }

    @Override
    public Optional<Integer> getResultAmount() {
        return resultAmount;
    }

    //------

    @ModifyArgs(method = "offerTo", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V"))
    private void jello_setProviderResult(Args args){
        if(args.get(0) instanceof CookingRecipeJsonProviderExtension extension && resultAmount.isPresent()){
            extension.setResultAmount(resultAmount.get());
        }
    }

    @Mixin(CookingRecipeJsonBuilder.CookingRecipeJsonProvider.class)
    public static abstract class CookingRecipeJsonProviderMixin implements CookingRecipeJsonProviderExtension {

        @Unique private Optional<Integer> resultAmount = Optional.empty();

        @Override
        public CookingRecipeJsonBuilder.CookingRecipeJsonProvider setResultAmount(int amount) {
            this.resultAmount = Optional.of(amount);

            return (CookingRecipeJsonBuilder.CookingRecipeJsonProvider) (Object) this;
        }

        @Override
        public Optional<Integer> getResultAmount() {
            return resultAmount;
        }

        @Inject(method = "serialize", at = @At("TAIL"))
        private void jello_attemptSerializeOfResultAmount(JsonObject json, CallbackInfo ci){
            this.resultAmount.ifPresent(amount -> json.addProperty("resultamount", amount));
        }
    }
}
