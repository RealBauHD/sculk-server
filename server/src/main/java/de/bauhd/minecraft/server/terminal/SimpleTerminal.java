package de.bauhd.minecraft.server.terminal;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.bauhd.minecraft.server.AdvancedMinecraftServer;
import de.bauhd.minecraft.server.command.CommandSender;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.io.IoBuilder;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;

public final class SimpleTerminal {

    private static final Logger LOGGER = LogManager.getLogger(SimpleTerminal.class);
    private static final String PROMPT = "> ";
    private static final CommandSender CONSOLE_COMMAND_SENDER = new TerminalCommandSender();

    private final AdvancedMinecraftServer server;
    private final LineReader lineReader;

    public SimpleTerminal(final AdvancedMinecraftServer server) {
        this.server = server;
        try {
            this.lineReader = LineReaderBuilder.builder()
                    .appName("Minecraft-Server")
                    .terminal(TerminalBuilder.builder()
                            .dumb(true)
                            .build())
                    .option(LineReader.Option.INSERT_TAB, false)
                    .option(LineReader.Option.DISABLE_EVENT_EXPANSION, true)
                    .build();
            TerminalAppender.setLineReader(this.lineReader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setupStreams() {
        System.setOut(IoBuilder.forLogger(LOGGER).setLevel(Level.INFO).buildPrintStream());
        System.setErr(IoBuilder.forLogger(LOGGER).setLevel(Level.ERROR).buildPrintStream());
    }

    public void start() {
        while (this.server.isRunning()) {
            try {
                final var line = this.lineReader.readLine(PROMPT).trim();
                if (!line.isEmpty()) {
                    try {
                        this.server.getCommandHandler().dispatcher().execute(line, CONSOLE_COMMAND_SENDER);
                    } catch (CommandSyntaxException e) {
                        LOGGER.warn(e.getMessage());
                    }
                }
            } catch (EndOfFileException ignored) {
            } catch (UserInterruptException e) {
                this.server.shutdown(true);
            }
        }
    }

}
