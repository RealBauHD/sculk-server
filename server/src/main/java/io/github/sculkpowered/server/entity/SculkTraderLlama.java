package io.github.sculkpowered.server.entity;

import io.github.sculkpowered.server.SculkServer;
import org.jetbrains.annotations.NotNull;

public final class SculkTraderLlama extends SculkLlama implements TraderLlama {

  public SculkTraderLlama(final SculkServer server) {
    super(server);
  }

  @Override
  public @NotNull EntityType type() {
    return EntityType.TRADER_LLAMA;
  }
}
