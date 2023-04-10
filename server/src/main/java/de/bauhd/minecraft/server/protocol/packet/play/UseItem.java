package de.bauhd.minecraft.server.protocol.packet.play;

import de.bauhd.minecraft.server.protocol.Buffer;
import de.bauhd.minecraft.server.protocol.Protocol;
import de.bauhd.minecraft.server.protocol.packet.Packet;
import de.bauhd.minecraft.server.protocol.packet.PacketHandler;

public final class UseItem implements Packet {

    private int hand;
    private int sequence;

    @Override
    public void decode(Buffer buf, Protocol.Version version) {
        this.hand = buf.readVarInt();
        this.sequence = buf.readVarInt();
    }

    @Override
    public boolean handle(PacketHandler handler) {
        return handler.handle(this);
    }

    @Override
    public String toString() {
        return "UseItem{" +
                "hand=" + this.hand +
                ", sequence=" + this.sequence +
                '}';
    }
}
