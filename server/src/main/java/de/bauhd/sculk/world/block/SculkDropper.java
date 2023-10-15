package de.bauhd.sculk.world.block;

import net.kyori.adventure.nbt.CompoundBinaryTag;
import org.jetbrains.annotations.NotNull;
import java.util.Map;

final class SculkDropper extends SculkBlockState.Entity<Dropper> implements Dropper {

  SculkDropper(BlockParent block, int id, Map<String, String> properties) {
    super(block, id, properties, 6);
  }

  public SculkDropper(BlockParent block, int id, Map<String, String> properties, int entityId, CompoundBinaryTag nbt) {
    super(block, id, properties, entityId, nbt);
  }

  @Override
  public @NotNull Dropper nbt(@NotNull CompoundBinaryTag nbt) {
     return new SculkDropper(this.block, this.id, this.properties, this.entityId, nbt);
  }
}