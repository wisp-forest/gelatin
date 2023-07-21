package io.wispforest.gelatin.dye_entries.ducks;

import net.minecraft.util.Identifier;

public interface ResourceFinderExtension {

    void putTempRedirect(Identifier key, Identifier value);

}
