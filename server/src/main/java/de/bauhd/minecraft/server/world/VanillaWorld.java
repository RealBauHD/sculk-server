package de.bauhd.minecraft.server.world;

import de.bauhd.minecraft.server.world.chunk.ChunkGenerator;
import de.bauhd.minecraft.server.world.chunk.MinecraftChunk;
import de.bauhd.minecraft.server.world.dimension.Dimension;

public final class VanillaWorld extends MinecraftWorld {

    private final VanillaLoader loader;

    public VanillaWorld(final String name,
                        final Dimension dimension, final ChunkGenerator generator,
                        final Position spawnPosition, final VanillaLoader loader) {
        super(name, dimension, generator, spawnPosition);
        this.loader = loader;
        this.loader.setWorld(this);
    }

    @Override
    protected MinecraftChunk createChunk(int chunkX, int chunkZ) {
        var chunk = this.loader.getChunk(chunkX, chunkZ);
        if (chunk == null) {
            chunk = super.createChunk(chunkX, chunkZ);
        } else {
            this.put(chunk);
        }
        return chunk;
    }
}
