package masterlazy.lazylogin;

import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerSession {
    private final ServerPlayerEntity player;
    private boolean loggedIn;
    // 玩家初始位置
    private final double x;
    private final double y;
    private final double z;
    // 上次传送的时间
    private long lastTp = 0;

    public PlayerSession(ServerPlayerEntity player, double x, double y, double z) {
        this.player = player;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public ServerPlayerEntity getPlayer() {
        return player;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
    public boolean isLoggedIn() {
        return loggedIn;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }

    public void setLastTeleport() { lastTp = System.currentTimeMillis(); }
    public long getLastTeleport () { return lastTp; }
}
