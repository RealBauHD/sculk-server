package io.github.sculkpowered.server.entity;

import org.jetbrains.annotations.NotNull;

public class SculkVillager extends AbstractAnimal implements Villager {

  @Override
  public @NotNull EntityType type() {
    return EntityType.VILLAGER;
  }
}
