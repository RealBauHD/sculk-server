package de.bauhd.minecraft.server.world;

import de.bauhd.minecraft.server.AdvancedMinecraftServer;
import de.bauhd.minecraft.server.world.block.Block;
import de.bauhd.minecraft.server.world.chunk.MinecraftChunk;
import de.bauhd.minecraft.server.world.section.PaletteHolder;
import de.bauhd.minecraft.server.world.section.Section;
import net.kyori.adventure.nbt.BinaryTagIO;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.kyori.adventure.nbt.ListBinaryTag;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static de.bauhd.minecraft.server.world.chunk.Chunk.*;

public final class VanillaLoader {

    private static final int SECTOR_SIZE = 4096;

    private final AdvancedMinecraftServer server;
    private final Path regionPath;
    private final Map<String, RegionFile> regionCache;
    private MinecraftWorld world;

    public VanillaLoader(final AdvancedMinecraftServer server, final Path path) {
        this.server = server;
        this.regionPath = path.resolve("region");
        this.regionCache = new HashMap<>();
    }

    public MinecraftChunk getChunk(final int x, final int z) {
        final var fileName = "r." + this.toRegionCoordinate(x) +
                "." + this.toRegionCoordinate(z) + ".mca";
        if (!this.regionCache.containsKey(fileName)) {
            try {
                this.regionCache.put(fileName, new RegionFile(this.regionPath.resolve(fileName)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return this.regionCache.get(fileName).getChunk(x, z);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setWorld(final MinecraftWorld world) {
        this.world = world;
    }

    private int toRegionCoordinate(final int coordinate) {
        return (int) Math.floor((double) coordinate / 32);
    }

    private final class RegionFile {

        private final RandomAccessFile accessFile;
        private final int[] locations = new int[1024];

        public RegionFile(final Path path) throws IOException {
            this.accessFile = new RandomAccessFile(path.toFile(), "r");
            this.accessFile.seek(0);

            for (int i = 0; i < this.locations.length; i++) {
                final var location = this.accessFile.readInt();
                this.locations[i] = location;
            }
        }

        private MinecraftChunk getChunk(final int chunkX, final int chunkZ) throws IOException {
            final var offset = this.sectorOffset(this.locations[(chunkX & 31) + (chunkZ & 31) * 32]) * SECTOR_SIZE;
            var buf = ByteBuffer.allocate(5);
            this.accessFile.getChannel().read(buf, offset);
            final var length = buf.getInt(0);
            if (length < 0) return null;
            final var compressionScheme = buf.get(4);
            if (compressionScheme < 1) return null;
            buf = ByteBuffer.allocate(length);
            this.accessFile.getChannel().read(buf, offset + 5);
            buf.flip();

            final var nbt = BinaryTagIO.reader(Integer.MAX_VALUE)
                    .read(new ByteArrayInputStream(buf.array(), buf.position(), length - buf.position()),
                            switch (compressionScheme) {
                                case 1 -> BinaryTagIO.Compression.GZIP;
                                case 2 -> BinaryTagIO.Compression.ZLIB;
                                case 3 -> BinaryTagIO.Compression.NONE;
                                default ->
                                        throw new IllegalStateException("Unexpected compression scheme: " + compressionScheme);
                            });

            final var sectionList = nbt.getList("sections");
            final var sections = new Section[sectionList.size()];
            for (var i = 0; i < sectionList.size(); i++) {
                final var compound = (CompoundBinaryTag) sectionList.get(i);
                final var states = compound.getCompound("block_states");
                final var blockPalette = states.getList("palette");
                if (blockPalette.equals(ListBinaryTag.empty())) continue;
                final var section = new Section(false);

                // load blocks
                {
                    final var blocks = (PaletteHolder) section.blocks();
                    final var palette = new int[blockPalette.size()];
                    for (var k = 0; k < palette.length; k++) {
                        final var entry = blockPalette.getCompound(k);
                        final var block = entry.getString("Name");
                        palette[k] = Block.get(block).stateId();
                        // ignore properties for now
                    }
                    if (palette.length == 1) {
                        blocks.fill(palette[0]);
                    } else {
                        blocks.setIndirectPalette();
                        final var blockStates = this.uncompressedBlockStates(states);
                        for (var y = 0; y < CHUNK_SECTION_SIZE; y++) {
                            for (var z = 0; z < CHUNK_SECTION_SIZE; z++) {
                                for (var x = 0; x < CHUNK_SECTION_SIZE; x++) {
                                    final var blockIndex = y * CHUNK_SECTION_SIZE * CHUNK_SECTION_SIZE + z * CHUNK_SECTION_SIZE + x;
                                    final var paletteIndex = blockStates[blockIndex];
                                    blocks.set(x, y, z, palette[paletteIndex]);
                                }
                            }
                        }
                    }
                }

                // load biomes
                {
                    final var biomes = (PaletteHolder) section.biomes();
                    final var biomesCompound = compound.getCompound("biomes");
                    final var biomePalette = biomesCompound.getList("palette");
                    final var palette = new int[biomePalette.size()];
                    for (var k = 0; k < palette.length; k++) {
                        final var entry = biomePalette.getCompound(k);
                        palette[k] = VanillaLoader.this.server.getBiomeHandler().getBiome(entry.getString("value")).id();
                    }
                    if (palette.length == 1) {
                        biomes.fill(palette[0]);
                    } else {
                        biomes.setIndirectPalette();
                        final var biomeIndexes = this.uncompressedBiomeIndexes(biomesCompound, biomePalette.size());
                        for (var y = 0; y < CHUNK_SECTION_SIZE; y++) {
                            for (var z = 0; z < CHUNK_SIZE_Z; z++) {
                                for (var x = 0; x < CHUNK_SIZE_X; x++) {
                                    final var finalX = (chunkX * CHUNK_SIZE_X + x);
                                    final var finalZ = (chunkZ * CHUNK_SIZE_Z + z);
                                    final var finalY = (i * CHUNK_SECTION_SIZE + y);
                                    final var index = x / 4 + (z / 4) * 4 + (y / 4) * 16;
                                    biomes.set(finalX, finalY, finalZ, biomeIndexes[index]);
                                }
                            }
                        }
                    }
                }

                sections[i] = section;
            }
            return new MinecraftChunk(VanillaLoader.this.world, chunkX, chunkZ, sections);
        }

        private int[] uncompressedBlockStates(CompoundBinaryTag states) {
            final var longs = states.getLongArray("data");
            final var sizeInBits = longs.length * 64 / 4096;
            var expectedCompressedLength = 0;
            if (longs.length == 0) {
                expectedCompressedLength = -1;
            } else {
                final var intPerLong = 64 / sizeInBits;
                expectedCompressedLength = (int) Math.ceil(4096.0 / intPerLong);
            }
            if (longs.length != expectedCompressedLength) {
                if (longs.length == 0) {
                    return new int[4096];
                }
            }
            return this.uncompress(longs, sizeInBits);
        }

        private int[] uncompressedBiomeIndexes(CompoundBinaryTag biomes, final double size) {
            final var compressedBiomes = biomes.getLongArray("data");
            final var sizeInBits = (int) Math.ceil(Math.log(size) / Math.log(2));
            return this.uncompress(compressedBiomes, sizeInBits);
        }

        private int[] uncompress(final long[] longs, final int sizeInBits) {
            final var intPerLong = Math.floor(64.0 / sizeInBits);
            final var intCount = (int) Math.ceil(longs.length * intPerLong);
            final var ints = new int[intCount];
            final var intPerLongCeil = (int) Math.ceil(intPerLong);
            final var mask = (1L << sizeInBits) - 1L;
            for (int i = 0; i < intCount; i++) {
                final var longIndex = i / intPerLongCeil;
                final var subIndex = i % intPerLongCeil;
                final var value = (int) ((longs[longIndex] >> (subIndex * sizeInBits)) & mask);
                ints[i] = value;
            }
            return ints;
        }

        private long sectorOffset(final int location) {
            return location >>> 8;
        }

    }
}
