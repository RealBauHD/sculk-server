package io.github.sculkpowered.server.entity;

import org.jetbrains.annotations.NotNull;

public final class SculkGiant extends AbstractMob implements Giant {

  @Override
  public @NotNull EntityType type() {
    return EntityType.GIANT;
  }
}
