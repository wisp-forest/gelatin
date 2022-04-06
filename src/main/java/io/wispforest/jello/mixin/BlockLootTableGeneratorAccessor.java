package io.wispforest.jello.mixin;

import net.minecraft.data.server.BlockLootTableGenerator;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(BlockLootTableGenerator.class)
public interface BlockLootTableGeneratorAccessor {
    @Accessor("EXPLOSION_IMMUNE")
    static Set<Item> jello$getEXPLOSION_IMMUNE() {
        throw new UnsupportedOperationException();
    }

    @Mutable
    @Accessor("EXPLOSION_IMMUNE")
    static void jello$setEXPLOSION_IMMUNE(Set<Item> EXPLOSION_IMMUNE) {
        throw new UnsupportedOperationException();
    }
}
