package io.wispforest.gelatin.common.util;

import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class TrackedDataHandlerExtended {

    public static final TrackedDataHandler<Identifier> IDENTIFIER = new TrackedDataHandler<>() {
        public void write(PacketByteBuf packetByteBuf, Identifier identifier) {
            packetByteBuf.writeString(identifier.toString());
        }

        public Identifier read(PacketByteBuf packetByteBuf) {
            return Identifier.tryParse(packetByteBuf.readString());
        }

        public Identifier copy(Identifier identifier) {
            return identifier;
        }
    };
    
    public static void init(){
        TrackedDataHandlerRegistry.register(IDENTIFIER);
    }
}
