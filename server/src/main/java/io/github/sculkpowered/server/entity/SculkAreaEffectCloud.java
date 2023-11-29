package io.github.sculkpowered.server.entity;

import io.github.sculkpowered.server.SculkServer;
import org.jetbrains.annotations.NotNull;

public final class SculkAreaEffectCloud extends AbstractEntity implements AreaEffectCloud {

  public SculkAreaEffectCloud(final SculkServer server) {
    super(server);
  }

  @Override
  public @NotNull EntityType type() {
    return EntityType.AREA_EFFECT_CLOUD;
  }
}
