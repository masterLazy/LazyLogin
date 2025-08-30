package masterlazy.lazylogin;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;

public class PlayerSession {
    private ServerPlayerEntity player;
    private boolean loggedIn;
    private GameMode gameMode;
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
        if (loggedIn) {
            player.changeGameMode(gameMode);
        } else {
            player.setInvulnerable(true);
            gameMode = player.interactionManager.getGameMode();
            player.changeGameMode(GameMode.SPECTATOR);
        }
        this.loggedIn = loggedIn;
    }

    public void init(ServerPlayerEntity player) {
        this.gameMode = player.interactionManager.getGameMode();
        this.player = player;
    }

    public void setLastTeleport() {
        lastTp = System.currentTimeMillis();
    }

    public long getLastTeleport() {
        return lastTp;
    }
}
