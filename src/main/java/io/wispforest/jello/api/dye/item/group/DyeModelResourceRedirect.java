package io.wispforest.jello.api.dye.item.group;

import io.wispforest.jello.api.dye.RandomDyeColorStuff;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class DyeModelResourceRedirect implements ModelResourceProvider {
    private static final Logger LOGGER = LogManager.getLogger("Jello|ModelLoaderMixin");

    @Override
    public @Nullable UnbakedModel loadModelResource(Identifier resourceId, ModelProviderContext context) throws ModelProviderException {
        if(Objects.equals(resourceId.getNamespace(), RandomDyeColorStuff.JSON_NAMESPACE)){
            //LOGGER.info("Is this even running? " + resourceId);

            return context.loadModel(new Identifier("jello", "item/dynamic_dye"));
        }
        return null;
    }
}
