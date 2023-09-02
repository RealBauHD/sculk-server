package de.bauhd.sculk.protocol.packet.login;

import de.bauhd.sculk.protocol.Buffer;
import de.bauhd.sculk.protocol.packet.Packet;

import java.util.Arrays;

public final class EncryptionRequest implements Packet {

    private final String serverId;
    private final byte[] publicKey;
    private final byte[] verifyToken;

    public EncryptionRequest(final String serverId, final byte[] publicKey, final byte[] verifyToken) {
        this.serverId = serverId;
        this.publicKey = publicKey;
        this.verifyToken = verifyToken;
    }

    @Override
    public void encode(Buffer buf) {
        buf
                .writeString(this.serverId)
                .writeByteArray(this.publicKey)
                .writeByteArray(this.verifyToken);
    }

    @Override
    public String toString() {
        return "EncryptionRequest{" +
                "serverId='" + this.serverId + '\'' +
                ", publicKey=" + Arrays.toString(this.publicKey) +
                ", verifyToken=" + Arrays.toString(this.verifyToken) +
                '}';
    }
}
