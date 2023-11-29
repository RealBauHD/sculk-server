package io.github.sculkpowered.server.entity;

import io.github.sculkpowered.server.SculkServer;
import org.jetbrains.annotations.NotNull;

public final class SculkParrot extends AbstractTameableAnimal implements Parrot {

  public SculkParrot(final SculkServer server) {
    super(server);
  }

  @Override
  public @NotNull EntityType type() {
    return EntityType.PARROT;
  }
}
