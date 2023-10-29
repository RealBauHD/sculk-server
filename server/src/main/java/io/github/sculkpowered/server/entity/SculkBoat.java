package io.github.sculkpowered.server.entity;

import org.jetbrains.annotations.NotNull;

public class SculkBoat extends AbstractEntity implements Boat {

  @Override
  public @NotNull EntityType type() {
    return EntityType.BOAT;
  }
}
