package de.bauhd.sculk.protocol.packet.play.title;

import de.bauhd.sculk.protocol.Buffer;
import de.bauhd.sculk.protocol.packet.Packet;

public final class TitleAnimationTimes implements Packet {

    private final int fadeIn;
    private final int stay;
    private final int fadeOut;

    public TitleAnimationTimes(final int fadeIn, final int stay, final int fadeOut) {
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
    }

    @Override
    public void encode(Buffer buf) {
        buf
                .writeInt(this.fadeIn)
                .writeInt(this.stay)
                .writeInt(this.fadeOut);
    }

    @Override
    public String toString() {
        return "TitleAnimationTimes{" +
                "fadeIn=" + this.fadeIn +
                ", stay=" + this.stay +
                ", fadeOut=" + this.fadeOut +
                '}';
    }
}
