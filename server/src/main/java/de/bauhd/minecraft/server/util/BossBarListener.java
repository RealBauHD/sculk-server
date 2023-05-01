package de.bauhd.minecraft.server.util;

import de.bauhd.minecraft.server.entity.MinecraftPlayer;
import de.bauhd.minecraft.server.protocol.packet.Packet;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class BossBarListener implements BossBar.Listener {

    private final Map<BossBar, Holder> bossBars = new HashMap<>();

    @Override
    public void bossBarNameChanged(@NotNull BossBar bar, @NotNull Component oldName, @NotNull Component newName) {
        final var holder = this.bossBars.get(bar);
        if (!holder.subscribers.isEmpty()) {
            holder.sendSubscribers(de.bauhd.minecraft.server.protocol.packet.play.BossBar.update(holder.uniqueId, newName));
        }
    }

    @Override
    public void bossBarProgressChanged(@NotNull BossBar bar, float oldProgress, float newProgress) {
        final var holder = this.bossBars.get(bar);
        if (!holder.subscribers.isEmpty()) {
            holder.sendSubscribers(de.bauhd.minecraft.server.protocol.packet.play.BossBar.update(holder.uniqueId, newProgress));
        }
    }

    @Override
    public void bossBarColorChanged(@NotNull BossBar bar, BossBar.@NotNull Color oldColor, BossBar.@NotNull Color newColor) {
        final var holder = this.bossBars.get(bar);
        if (!holder.subscribers.isEmpty()) {
            holder.sendSubscribers(de.bauhd.minecraft.server.protocol.packet.play.BossBar.update(holder.uniqueId, newColor.ordinal(), bar.overlay().ordinal()));
        }
    }

    @Override
    public void bossBarOverlayChanged(@NotNull BossBar bar, BossBar.@NotNull Overlay oldOverlay, BossBar.@NotNull Overlay newOverlay) {
        final var holder = this.bossBars.get(bar);
        if (!holder.subscribers.isEmpty()) {
            holder.sendSubscribers(de.bauhd.minecraft.server.protocol.packet.play.BossBar.update(holder.uniqueId, bar.color().ordinal(), newOverlay.ordinal()));
        }
    }

    @Override
    public void bossBarFlagsChanged(@NotNull BossBar bar, @NotNull Set<BossBar.Flag> flagsAdded, @NotNull Set<BossBar.Flag> flagsRemoved) {
        final var holder = this.bossBars.get(bar);
        if (!holder.subscribers.isEmpty()) {
            holder.sendSubscribers(de.bauhd.minecraft.server.protocol.packet.play.BossBar.update(holder.uniqueId, this.flags(bar)));
        }
    }

    public void showBossBar(final MinecraftPlayer player, final BossBar bar) {
        if (!this.bossBars.containsKey(bar)) {
            this.bossBars.put(bar, new Holder());
            bar.addListener(this);
        }
        final var holder = this.bossBars.get(bar);
        holder.subscribers.add(player);
        player.send(de.bauhd.minecraft.server.protocol.packet.play.BossBar
                .add(holder.uniqueId, bar.name(), bar.progress(), bar.color().ordinal(), bar.overlay().ordinal(), this.flags(bar)));
    }

    public void hideBossBar(final MinecraftPlayer player, final BossBar bar) {
        final var holder = this.bossBars.get(bar);
        holder.subscribers.remove(player);
        player.send(de.bauhd.minecraft.server.protocol.packet.play.BossBar.remove(holder.uniqueId));
    }

    public void onDisconnect(final MinecraftPlayer player) {
        for (final var holder : this.bossBars.values()) {
            holder.subscribers.remove(player);
        }
    }

    private byte flags(final BossBar bar) {
        byte flags = 0;
        if (bar.flags().contains(BossBar.Flag.DARKEN_SCREEN)) {
            flags |= 0x1;
        }
        if (bar.flags().contains(BossBar.Flag.PLAY_BOSS_MUSIC)) {
            flags |= 0x2;
        }
        if (bar.flags().contains(BossBar.Flag.CREATE_WORLD_FOG)) {
            flags |= 0x4;
        }
        return flags;
    }

    private static final class Holder {

        private final UUID uniqueId;
        private final List<MinecraftPlayer> subscribers;

        private Holder() {
            this.uniqueId = UUID.randomUUID();
            this.subscribers = new ArrayList<>();
        }

        private void sendSubscribers(final Packet packet) {
            for (final var subscriber : this.subscribers) {
                subscriber.send(packet);
            }
        }
    }
}
