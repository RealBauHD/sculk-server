package io.github.sculkpowered.server.entity;

import io.github.sculkpowered.server.SculkServer;
import org.jetbrains.annotations.NotNull;

public final class SculkSquid extends AbstractAnimal implements Squid {

  public SculkSquid(final SculkServer server) {
    super(server);
  }

  @Override
  public @NotNull EntityType type() {
    return EntityType.SQUID;
  }
}
