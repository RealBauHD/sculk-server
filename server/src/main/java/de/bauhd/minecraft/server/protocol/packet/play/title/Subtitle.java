package de.bauhd.minecraft.server.protocol.packet.play.title;

import de.bauhd.minecraft.server.protocol.Buffer;
import de.bauhd.minecraft.server.protocol.packet.Packet;
import net.kyori.adventure.text.Component;

public final class Subtitle implements Packet {

    private final Component text;

    public Subtitle(final Component text) {
        this.text = text;
    }

    @Override
    public void encode(Buffer buf) {
        buf.writeComponent(this.text);
    }

    @Override
    public String toString() {
        return "Subtitle{" +
                "text=" + this.text +
                '}';
    }
}
