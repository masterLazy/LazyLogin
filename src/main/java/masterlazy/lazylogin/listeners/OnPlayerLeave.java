package masterlazy.lazylogin.listeners;

import masterlazy.lazylogin.LazyLogin;
import masterlazy.lazylogin.PlayerLogin;
import net.minecraft.server.network.ServerPlayerEntity;

public class OnPlayerLeave {
    public static void listen(ServerPlayerEntity player) {
        PlayerLogin playerLogin = LazyLogin.getPlayer(player);
        playerLogin.setLoggedIn(false);
    }
}
