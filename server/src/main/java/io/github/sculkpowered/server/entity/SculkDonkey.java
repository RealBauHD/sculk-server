package io.github.sculkpowered.server.entity;

import org.jetbrains.annotations.NotNull;

public final class SculkDonkey extends AbstractAnimal implements Donkey {

  @Override
  public @NotNull EntityType type() {
    return EntityType.DONKEY;
  }
}
