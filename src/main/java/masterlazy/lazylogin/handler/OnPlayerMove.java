package masterlazy.lazylogin.handler;

import masterlazy.lazylogin.LazyLogin;
import masterlazy.lazylogin.PlayerSession;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class OnPlayerMove {

    public static boolean canMove(ServerPlayNetworkHandler networkHandler) {
        ServerPlayerEntity player = networkHandler.player;
        PlayerSession playerSession = LazyLogin.getPlayer(networkHandler.player);
        boolean isLoggedIn = playerSession.isLoggedIn();
        // Teleport to the initial position if not logged-in, limiting the time interval
        if (!isLoggedIn && System.currentTimeMillis() - playerSession.getLastTeleport() > 100) {
            player.teleport(playerSession.getX(), playerSession.getY(), playerSession.getZ(), true);
            playerSession.setLastTeleport();
            return false;
        }
        return true;
    }
}
