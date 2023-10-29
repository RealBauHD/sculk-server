package io.github.sculkpowered.server.entity;

import org.jetbrains.annotations.NotNull;

public final class SculkEgg extends AbstractEntity implements Egg {

  @Override
  public @NotNull EntityType type() {
    return EntityType.EGG;
  }
}
