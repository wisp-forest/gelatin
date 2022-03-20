package io.wispforest.jello.api.dye.client;

import io.wispforest.jello.api.dye.DyeColorantJsonTest;
import io.wispforest.jello.api.util.MessageUtil;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelVariantProvider;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

public class BlockModelRedirect implements ModelVariantProvider {

    private static final MessageUtil MESSAGE_TOOL = new MessageUtil("Block Model Redirect");

    @Override
    public @Nullable UnbakedModel loadModelVariant(ModelIdentifier modelId, ModelProviderContext context) throws ModelProviderException {
        if(Objects.equals(modelId.getNamespace(), DyeColorantJsonTest.JSON_NAMESPACE)){
            String[] stringParts = modelId.getPath().split("_");

            //MESSAGE_TOOL.infoMessage(Arrays.toString(stringParts));

            if(Objects.equals(stringParts[stringParts.length - 1], "dye")){
                return context.loadModel(new Identifier("jello", "item/dynamic_dye"));
            }

            String loadFromDirectory;
            if(modelId.getVariant() == "inventory"){
                loadFromDirectory = "item";
            }
            else{
                loadFromDirectory = "block";
            }

            if(Objects.equals(stringParts[stringParts.length - 1], "terracotta")){
                return context.loadModel(new Identifier("jello",  loadFromDirectory + "/terracotta"));
            }
            else if(Objects.equals(stringParts[stringParts.length - 1], "carpet")){
                return context.loadModel(new Identifier("jello",  loadFromDirectory + "/carpet"));
            }
            else if(Objects.equals(stringParts[stringParts.length - 1], "concrete")){
                return context.loadModel(new Identifier("jello",  loadFromDirectory + "/concrete"));
            }
            else if(Objects.equals(stringParts[stringParts.length - 1], "powder")){
                return context.loadModel(new Identifier("jello",  loadFromDirectory + "/concrete_powder"));
            }
            else if(Objects.equals(stringParts[stringParts.length - 1], "wool")){
                return context.loadModel(new Identifier("jello",  loadFromDirectory + "/wool"));
            }
            else if(Objects.equals(stringParts[stringParts.length - 1], "box")){
                return context.loadModel(new Identifier(loadFromDirectory  + "/shulker_box"));
            }

            MESSAGE_TOOL.infoMessage(Arrays.toString(stringParts));
            MESSAGE_TOOL.failMessage(" {Deetz Nuts} Failed to find model for " + modelId.toString());
        }
        return null;
    }
}
