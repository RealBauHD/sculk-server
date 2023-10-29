package io.github.sculkpowered.server.entity;

import org.jetbrains.annotations.NotNull;

public final class SculkSquid extends AbstractAnimal implements Squid {

  @Override
  public @NotNull EntityType type() {
    return EntityType.SQUID;
  }
}
