package io.github.sculkpowered.server.protocol;

public final class Protocol {

  public static final String VERSION_NAME = "1.20.3";
  public static final int VERSION_PROTOCOL = 765;

  public enum Direction {
    SERVERBOUND {
      @Override
      public State.PacketRegistry getRegistry(State state) {
        return state.serverBound;
      }
    },
    CLIENTBOUND {
      @Override
      public State.PacketRegistry getRegistry(State state) {
        return state.clientBound;
      }
    };

    public abstract State.PacketRegistry getRegistry(final State state);
  }
}
