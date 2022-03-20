package io.wispforest.jello.api.dye.client;

import io.wispforest.jello.api.util.MessageUtil;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DyeModelResourceRedirect implements ModelResourceProvider {
    private static final MessageUtil MESSAGE_TOOL = new MessageUtil("Json Model Redirect");

    private static final List<String> BLOCK_TYPE = List.of(
            "carpet",
            "wool",
//            "_bed",
            "terracotta",
            "concrete",
//            "_candle",
            "concrete_powder",
            "candle_cake",
            "shulker_box",
            "stained_glass",
            "slime_block",
            "slime_slab",
            "stained_glass_pane"
    );

    @Override
    public @Nullable UnbakedModel loadModelResource(Identifier resourceId, ModelProviderContext context) throws ModelProviderException {

//        if(Objects.equals(resourceId.getNamespace(), RandomDyeColorStuff.JSON_NAMESPACE)){
//            String[] pathParts = resourceId.getPath().split("/");
//
//            String[] stringParts = pathParts[1].split("_");
//
//            MESSAGE_TOOL.infoMessage(Arrays.toString(pathParts) + " / " + Arrays.toString(stringParts));
//
//            if(Objects.equals(pathParts[0], "item")) {
//
//                if(Objects.equals(stringParts[stringParts.length - 1], "dye")){
//                    return context.loadModel(new Identifier("jello", "item/dynamic_dye"));
//                }
//            }
//            else if(Objects.equals(pathParts[0], "block")){
//                if(Objects.equals(stringParts[stringParts.length - 1], "terracotta")){
//                    return context.loadModel(new Identifier("jello", "block/terracotta"));
//                }
//                else if(Objects.equals(stringParts[stringParts.length - 1], "carpet")){
//                    return context.loadModel(new Identifier("jello", "block/carpet"));
//                }
//                else if(Objects.equals(stringParts[stringParts.length - 1], "concrete")){
//                    return context.loadModel(new Identifier("jello", "block/concrete"));
//                }
//                else if(Objects.equals(stringParts[stringParts.length - 1], "powder")){
//                    return context.loadModel(new Identifier("jello", "block/concrete_powder"));
//                }
//                else if(Objects.equals(stringParts[stringParts.length - 1], "wool")){
//                    return context.loadModel(new Identifier("jello", "block/wool"));
//                }
//                else if(Objects.equals(stringParts[stringParts.length - 1], "box")){
//                    return context.loadModel(new Identifier("block/shulker_box"));
//                }
//
//                //LOGGER.info("[Block]: Is this even running? " + resourceId);
////                return context.loadModel(new Identifier("block/cube_all"));
//            }
//
//            MESSAGE_TOOL.infoMessage(Arrays.toString(pathParts) + " / " + Arrays.toString(stringParts));
//            MESSAGE_TOOL.failMessage(" {Deetz Nuts} Failed to find model for " + resourceId.toString());
//        }

        return null;
    }
}
