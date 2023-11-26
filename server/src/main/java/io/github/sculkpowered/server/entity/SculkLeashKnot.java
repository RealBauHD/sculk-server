package io.github.sculkpowered.server.entity;

import io.github.sculkpowered.server.SculkServer;
import org.jetbrains.annotations.NotNull;

public final class SculkLeashKnot extends AbstractEntity implements LeashKnot {

  public SculkLeashKnot(final SculkServer server) {
    super(server);
  }

  @Override
  public @NotNull EntityType type() {
    return EntityType.LEASH_KNOT;
  }
}
