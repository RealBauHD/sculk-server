package io.github.sculkpowered.server.container;

import io.github.sculkpowered.server.entity.player.SculkPlayer;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public final class SculkFurnaceContainer extends SculkContainer implements FurnaceContainer {

  private final Type type;
  private int fuelLeft;
  private int maxFuelBurnTime;
  private int progressArrow;
  private int maximumProgress;

  public SculkFurnaceContainer(final Type type, final Component title) {
    super(title);
    this.type = type;
  }

  @Override
  public void fuelLeft(int fuelLeft) {
    this.sendProperty(0, fuelLeft);
    this.fuelLeft = fuelLeft;
  }

  @Override
  public int fuelLeft() {
    return this.fuelLeft;
  }

  @Override
  public void maxFuelBurnTime(int maxFuelBurnTime) {
    this.sendProperty(1, maxFuelBurnTime);
    this.maxFuelBurnTime = maxFuelBurnTime;
  }

  @Override
  public int maxFuelBurnTime() {
    return this.maxFuelBurnTime;
  }

  @Override
  public void progressArrow(int progressArrow) {
    this.sendProperty(2, progressArrow);
    this.progressArrow = progressArrow;
  }

  @Override
  public int progressArrow() {
    return this.progressArrow;
  }

  @Override
  public void maximumProgress(int maximumProgress) {
    this.sendProperty(3, maximumProgress);
    this.maximumProgress = maximumProgress;
  }

  @Override
  public int maximumProgress() {
    return this.maximumProgress;
  }

  @Override
  public @NotNull Type type() {
    return this.type;
  }

  @Override
  public void sendProperties(SculkPlayer player) {
    player.send(this.property(0, this.fuelLeft));
    player.send(this.property(1, this.maxFuelBurnTime));
    player.send(this.property(2, this.progressArrow));
    player.send(this.property(3, this.maximumProgress));
  }
}
