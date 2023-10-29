package io.github.sculkpowered.server.entity;

import org.jetbrains.annotations.NotNull;

public final class SculkAxolotl extends AbstractAnimal implements Axolotl {

  @Override
  public @NotNull EntityType type() {
    return EntityType.AXOLOTL;
  }

  @Override
  public @NotNull Axolotl.Variant variant() {
    return this.metadata.getEnum(17, Variant.LUCY);
  }

  @Override
  public void variant(@NotNull Variant variant) {
    this.metadata.setVarInt(17, variant.ordinal());
  }

  @Override
  public boolean playingDead() {
    return this.metadata.getBoolean(18, false);
  }

  @Override
  public void playingDead(boolean playingDead) {
    this.metadata.setBoolean(18, playingDead);
  }
}
