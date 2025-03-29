package masterlazy.lazylogin.handler;

import masterlazy.lazylogin.LazyLogin;
import masterlazy.lazylogin.PlayerSession;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class OnPlayerMove {

    public static boolean canMove(ServerPlayNetworkHandler networkHandler) {
        ServerPlayerEntity player = networkHandler.player;
        PlayerSession playerSession = LazyLogin.getPlayer(player);
        // Sync player state if not logged-in, limiting the time interval
        if (!playerSession.isLoggedIn()) {
            if (System.currentTimeMillis() - playerSession.getLastTeleport() > 100) {
                ServerPlayerEntity p = playerSession.getPlayer();
                player.teleport(p.getX(),p.getY(),p.getZ(),false);
                playerSession.setLastTeleport();
            }
            return false;
        }
        return true;
    }
}
