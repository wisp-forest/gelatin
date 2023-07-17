package io.wispforest.jello.client.gui;

import io.wispforest.jello.mixins.client.owo.NinePatchRendererAccessor;
import io.wispforest.owo.ui.core.OwoUIDrawContext;
import io.wispforest.owo.ui.core.ParentComponent;
import io.wispforest.owo.ui.core.Size;
import io.wispforest.owo.ui.core.Surface;
import io.wispforest.owo.ui.util.NinePatchTexture;
import net.minecraft.util.Identifier;

public class VariantsNinePatchRender extends NinePatchTexture implements Surface {

    public int offsetUIndex = 0;
    public int offsetVIndex = 0;

    public int variantOffsetU;
    public int variantOffsetV;

    public VariantsNinePatchRender(Identifier texture, Size patchSize, Size textureSize, boolean repeat) {
        super(texture, patchSize, textureSize, repeat);

        this.variantOffsetU = patchSize.width() * 3;
        this.variantOffsetV = patchSize.height() * 3;
    }

    public VariantsNinePatchRender setUIndex(int i){
        this.offsetUIndex = i;

        this.access().setU(variantOffsetU * offsetUIndex);

        return this;
    }

    public VariantsNinePatchRender setVIndex(int i){
        this.offsetVIndex = i;

        this.access().setV(variantOffsetV * offsetVIndex);

        return this;
    }

    @Override
    public void draw(OwoUIDrawContext context, ParentComponent component) {
        this.draw(context, component.x(), component.y(), component.width(), component.height());
    }

    public NinePatchRendererAccessor access(){
        return (NinePatchRendererAccessor) this;
    }
}
