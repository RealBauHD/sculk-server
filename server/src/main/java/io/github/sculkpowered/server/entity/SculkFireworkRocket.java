package io.github.sculkpowered.server.entity;

import io.github.sculkpowered.server.SculkServer;
import org.jetbrains.annotations.NotNull;

public final class SculkFireworkRocket extends AbstractEntity implements FireworkRocket {

  public SculkFireworkRocket(final SculkServer server) {
    super(server);
  }

  @Override
  public @NotNull EntityType type() {
    return EntityType.FIREWORK_ROCKET;
  }
}
