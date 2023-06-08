package de.bauhd.minecraft.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.bauhd.minecraft.server.command.MinecraftCommandHandler;
import de.bauhd.minecraft.server.container.*;
import de.bauhd.minecraft.server.dimension.MinecraftDimensionHandler;
import de.bauhd.minecraft.server.entity.Entity;
import de.bauhd.minecraft.server.entity.EntityClassToSupplierMap;
import de.bauhd.minecraft.server.entity.player.GameProfile;
import de.bauhd.minecraft.server.entity.player.MinecraftPlayer;
import de.bauhd.minecraft.server.entity.player.Player;
import de.bauhd.minecraft.server.event.MinecraftEventHandler;
import de.bauhd.minecraft.server.event.lifecycle.ServerInitializeEvent;
import de.bauhd.minecraft.server.json.GameProfileDeserializer;
import de.bauhd.minecraft.server.json.GameProfilePropertyDeserializer;
import de.bauhd.minecraft.server.plugin.MinecraftPluginHandler;
import de.bauhd.minecraft.server.protocol.Connection;
import de.bauhd.minecraft.server.protocol.netty.NettyServer;
import de.bauhd.minecraft.server.protocol.packet.Packet;
import de.bauhd.minecraft.server.protocol.packet.login.CompressionPacket;
import de.bauhd.minecraft.server.terminal.SimpleTerminal;
import de.bauhd.minecraft.server.util.BossBarListener;
import de.bauhd.minecraft.server.world.MinecraftWorld;
import de.bauhd.minecraft.server.world.VanillaLoader;
import de.bauhd.minecraft.server.world.VanillaWorld;
import de.bauhd.minecraft.server.world.World;
import de.bauhd.minecraft.server.world.biome.MinecraftBiomeHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class AdvancedMinecraftServer implements MinecraftServer {

    private static final Logger LOGGER = LogManager.getLogger(AdvancedMinecraftServer.class);

    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(GameProfile.Property.class, new GameProfilePropertyDeserializer())
            .registerTypeAdapter(GameProfile.class, new GameProfileDeserializer())
            .setPrettyPrinting()
            .create();

    private static final GsonComponentSerializer PRE_1_16_SERIALIZER = GsonComponentSerializer.colorDownsamplingGson();
    private static final GsonComponentSerializer MODERN_SERIALIZER = GsonComponentSerializer.gson();

    static {
        System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
    }

    private boolean running = true;

    private MinecraftConfiguration configuration;
    private KeyPair keyPair;

    private final Map<UUID, MinecraftPlayer> players = new ConcurrentHashMap<>();
    private final MinecraftDimensionHandler dimensionHandler;
    private final MinecraftBiomeHandler biomeHandler;
    private final MinecraftPluginHandler pluginHandler;
    private final MinecraftEventHandler eventHandler;
    private final MinecraftCommandHandler commandHandler;
    private final BossBarListener bossBarListener;
    private final NettyServer nettyServer;
    private final EntityClassToSupplierMap entities = new EntityClassToSupplierMap();

    AdvancedMinecraftServer() {
        final var startTime = System.currentTimeMillis();
        final var terminal = new SimpleTerminal(this);

        this.loadConfig();

        if (this.configuration.mode() == MinecraftConfig.Mode.ONLINE) {
            try {
                final var generator = KeyPairGenerator.getInstance("RSA");
                generator.initialize(1024);
                this.keyPair = generator.generateKeyPair();
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }

        this.dimensionHandler = new MinecraftDimensionHandler();
        this.biomeHandler = new MinecraftBiomeHandler();
        this.pluginHandler = new MinecraftPluginHandler(this);
        this.eventHandler = new MinecraftEventHandler();
        this.commandHandler = new MinecraftCommandHandler(this);
        this.bossBarListener = new BossBarListener();

        this.pluginHandler.loadPlugins();

        this.eventHandler.call(new ServerInitializeEvent()).join();

        this.nettyServer = new NettyServer(this);
        this.nettyServer.connect(this.configuration.host(), this.configuration.port());

        LOGGER.info("Done ({}s)!", new DecimalFormat("#.##")
                .format((System.currentTimeMillis() - startTime) / 1000D));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> this.shutdown(false), "Minecraft Shutdown Thread"));

        if (this.configuration.compressionThreshold() != -1) {
            Connection.COMPRESSION_PACKET = new CompressionPacket(this.configuration.compressionThreshold());
        }

        terminal.start();
    }

    public void shutdown(final boolean runtime) {
        LOGGER.info("Shutdown!");
        this.running = false;

        LogManager.shutdown(false);

        this.nettyServer.close();

        if (runtime) {
            Runtime.getRuntime().exit(0);
        }
    }

    private void loadConfig() {
        final var path = Path.of("config.json");

        try {
            if (Files.notExists(path)) {
                Files.createFile(path);
                this.configuration = new MinecraftConfiguration();
                try (final var writer = Files.newBufferedWriter(path)) {
                    writer.write(GSON.toJson(this.configuration));
                }
            } else {
                try (final var reader = Files.newBufferedReader(path)) {
                    this.configuration = GSON.fromJson(reader, MinecraftConfiguration.class);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isRunning() {
        return this.running;
    }

    @Override
    public @NotNull MinecraftDimensionHandler getDimensionHandler() {
        return this.dimensionHandler;
    }

    @Override
    public @NotNull MinecraftBiomeHandler getBiomeHandler() {
        return this.biomeHandler;
    }

    @Override
    public @NotNull MinecraftPluginHandler getPluginHandler() {
        return this.pluginHandler;
    }

    @Override
    public @NotNull MinecraftEventHandler getEventHandler() {
        return this.eventHandler;
    }

    @Override
    public @NotNull MinecraftCommandHandler getCommandHandler() {
        return this.commandHandler;
    }

    @Override
    public @NotNull Collection<Player> getAllPlayers() {
        return List.copyOf(this.players.values());
    }

    @Override
    public int getPlayerCount() {
        return this.players.size();
    }

    @Override
    public @Nullable Player getPlayer(@NotNull UUID uniqueId) {
        return this.players.get(uniqueId);
    }

    @Override
    public @NotNull World createWorld(World.@NotNull Builder builder) {
        return new MinecraftWorld(Objects.requireNonNull(builder.name(), "a world requires a name"),
                builder.dimension(), builder.generator(), builder.spawnPosition(), builder.defaultGameMode());
    }

    @Override
    public @NotNull World loadWorld(World.@NotNull Builder builder, @NotNull Path path) {
        return new VanillaWorld(Objects.requireNonNull(builder.name(), "a world requires a name"),
                builder.dimension(), builder.generator(), builder.spawnPosition(), builder.defaultGameMode(),
                new VanillaLoader(this, path));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Entity> T createEntity(@NotNull Class<T> clazz) {
        final var supplier = this.entities.get(clazz);
        if (supplier == null) {
            throw new NullPointerException("No supplier for class" + clazz + " found!");
        }
        return (T) supplier.get();
    }

    @Override
    public @NotNull Container createContainer(Container.@NotNull Type type, @NotNull Component title) {
        return switch (type) {
            case GENERIC_9x1, GENERIC_9x2, GENERIC_9x3, GENERIC_9x6, GENERIC_9x5, GENERIC_9x4, GENERIC_3x3,
                    CRAFTING, GRINDSTONE, HOPPER, LECTERN, MERCHANT, SHULKER_BOX, SMITHING, CARTOGRAPHY ->
                    new GenericContainer(type, title);
            case ANVIL -> new MineAnvilContainer(title);
            case BEACON -> new MineBeaconContainer(title);
            case BLAST_FURNACE, FURNACE, SMOKER -> new MineFurnaceContainer(title);
            case BREWING_STAND -> new MineBrewingStandContainer(title);
            case ENCHANTMENT -> new MineEnchantingTableContainer(title);
            case LOOM -> new MineLoomContainer(title);
            case STONECUTTER -> new MineStonecutterContainer(title);
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

    public MinecraftConfiguration getConfiguration() {
        return this.configuration;
    }

    public KeyPair getKeyPair() {
        return this.keyPair;
    }

    public BossBarListener getBossBarListener() {
        return this.bossBarListener;
    }

    public void sendAll(final Packet packet) {
        this.players.values().forEach(player -> player.send(packet));
    }

    public void addPlayer(final MinecraftPlayer player) {
        this.players.put(player.getUniqueId(), player);
    }

    public void removePlayer(final UUID uniqueId) {
        this.players.remove(uniqueId);
    }

    public static GsonComponentSerializer getGsonSerializer(final int version) {
        return version >= 735 ? MODERN_SERIALIZER : PRE_1_16_SERIALIZER;
    }
}
