package masterlazy.lazylogin;

import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerSession {
    private ServerPlayerEntity player;
    private boolean loggedIn;
    // 上次传送的时间
    private long lastTp = 0;

    public PlayerSession(ServerPlayerEntity player) {
        init(player);
    }

    public ServerPlayerEntity getPlayer() {
        return player;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public void init(ServerPlayerEntity player) {
        this.player = player;
    }

    public void setLastTeleport() {
        lastTp = System.currentTimeMillis();
    }

    public long getLastTeleport() {
        return lastTp;
    }
}
