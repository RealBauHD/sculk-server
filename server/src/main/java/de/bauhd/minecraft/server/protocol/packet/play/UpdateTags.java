package de.bauhd.minecraft.server.protocol.packet.play;

import de.bauhd.minecraft.server.protocol.Buffer;
import de.bauhd.minecraft.server.protocol.packet.Packet;

public final class UpdateTags implements Packet {

    @Override
    public void encode(Buffer buf) {
        buf.writeVarInt(0);
    }
}
