package io.github.sculkpowered.server.entity;

import org.jetbrains.annotations.NotNull;

public final class SculkDrowned extends SculkZombie implements Drowned {

  @Override
  public @NotNull EntityType type() {
    return EntityType.DROWNED;
  }
}
