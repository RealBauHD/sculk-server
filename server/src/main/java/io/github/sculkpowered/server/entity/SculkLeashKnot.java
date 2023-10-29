package io.github.sculkpowered.server.entity;

import org.jetbrains.annotations.NotNull;

public final class SculkLeashKnot extends AbstractEntity implements LeashKnot {

  @Override
  public @NotNull EntityType type() {
    return EntityType.LEASH_KNOT;
  }
}
