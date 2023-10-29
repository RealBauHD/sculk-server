package io.github.sculkpowered.server.entity;

import org.jetbrains.annotations.NotNull;

public final class SculkSkeletonHorse extends AbstractAnimal implements SkeletonHorse {

  @Override
  public @NotNull EntityType type() {
    return EntityType.SKELETON_HORSE;
  }
}
