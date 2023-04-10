package de.bauhd.minecraft.server.protocol.packet;

import de.bauhd.minecraft.server.protocol.Buffer;
import de.bauhd.minecraft.server.protocol.Protocol;

public interface Packet {

    default void decode(final Buffer buf, final Protocol.Version version) {}

    default void encode(final Buffer buf, final Protocol.Version version) {}

    default boolean handle(final PacketHandler handler) {
        return false;
    }

    default int minLength() {
        return 0;
    }

    default int maxLength() {
        return -1;
    }

}
