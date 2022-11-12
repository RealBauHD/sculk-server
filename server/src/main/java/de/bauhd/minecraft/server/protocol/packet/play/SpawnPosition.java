package de.bauhd.minecraft.server.protocol.packet.play;

import de.bauhd.minecraft.server.api.world.Position;
import de.bauhd.minecraft.server.protocol.Protocol;
import de.bauhd.minecraft.server.protocol.packet.Packet;
import io.netty5.buffer.Buffer;

import static de.bauhd.minecraft.server.protocol.packet.PacketUtils.writePosition;

public final class SpawnPosition implements Packet {

    private final Position position;

    public SpawnPosition(final Position position) {
        this.position = position;
    }

    @Override
    public void encode(Buffer buf, Protocol.Version version) {
        writePosition(buf, this.position);
        buf.writeFloat(this.position.yaw());
    }
}
