package de.bauhd.minecraft.server.entity.player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * Represents a profile of a player.
 */
public record GameProfile(
        @NotNull UUID uniqueId,
        @NotNull String name,
        @NotNull List<Property> properties
) {

    public record Property(
            @NotNull String key,
            @NotNull String value,
            @Nullable String signature
    ) {}
}
