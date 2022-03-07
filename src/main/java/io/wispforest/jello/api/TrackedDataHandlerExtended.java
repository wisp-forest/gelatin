package io.wispforest.jello.api;

import io.wispforest.owo.registration.reflect.SimpleFieldProcessingSubject;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.lang.reflect.Field;

public class TrackedDataHandlerExtended implements SimpleFieldProcessingSubject<TrackedDataHandler> {
    public static final TrackedDataHandler<Identifier> IDENTIFIER = new TrackedDataHandler<Identifier>() {
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

    @Override
    public void processField(TrackedDataHandler value, String identifier, Field field) {
        TrackedDataHandlerRegistry.register(value);
    }

    @Override
    public Class<TrackedDataHandler> getTargetFieldType() {
        return TrackedDataHandler.class;
    }
}
