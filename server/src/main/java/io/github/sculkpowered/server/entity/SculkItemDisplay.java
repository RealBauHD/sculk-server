package io.github.sculkpowered.server.entity;

import io.github.sculkpowered.server.container.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class SculkItemDisplay extends AbstractDisplay implements ItemDisplay {

  @Override
  public @NotNull EntityType type() {
    return EntityType.ITEM_DISPLAY;
  }

  @Override
  public @NotNull ItemStack item() {
    return this.metadata.getItem(22, ItemStack.empty());
  }

  @Override
  public void item(@NotNull ItemStack item) {
    this.metadata.setItem(22, item);
  }

  @Override
  public @NotNull DisplayType displayType() {
    return this.metadata.getEnum(23, DisplayType.NONE);
  }

  @Override
  public void displayType(@NotNull DisplayType displayType) {
    this.metadata.setByte(23, (byte) displayType.ordinal());
  }
}
