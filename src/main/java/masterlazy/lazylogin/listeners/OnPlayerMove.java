package masterlazy.lazylogin.listeners;

import masterlazy.lazylogin.LoginMod;
import masterlazy.lazylogin.PlayerLogin;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class OnPlayerMove {
    public static boolean canMove(ServerPlayNetworkHandler networkHandler) {
        ServerPlayerEntity player = networkHandler.player;
        PlayerLogin playerLogin = LoginMod.getPlayer(networkHandler.player);
        boolean isLoggedIn = playerLogin.isLoggedIn();
        if (! isLoggedIn) {
            player.teleport(player.getX(), player.getY(), player.getZ()); // teleport to sync client position
        }
        return isLoggedIn;
    }
}
