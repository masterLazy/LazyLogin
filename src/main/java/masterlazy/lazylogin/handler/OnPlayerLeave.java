package masterlazy.lazylogin.handler;

import masterlazy.lazylogin.LazyLogin;
import masterlazy.lazylogin.PlayerSession;
import net.minecraft.server.network.ServerPlayerEntity;

public class OnPlayerLeave {
    public static void handle(ServerPlayerEntity player) {
        PlayerSession playerSession = LazyLogin.getPlayer(player);
        playerSession.setLoggedIn(false);
    }
}
