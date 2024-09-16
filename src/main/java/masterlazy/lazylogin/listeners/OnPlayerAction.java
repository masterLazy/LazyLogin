package masterlazy.lazylogin.listeners;

import masterlazy.lazylogin.LoginMod;
import masterlazy.lazylogin.PlayerLogin;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class OnPlayerAction {
    public static boolean canInteract(ServerPlayNetworkHandler networkHandler) {
        ServerPlayerEntity player = networkHandler.player;
        PlayerLogin playerLogin = LoginMod.getPlayer(player);
        return playerLogin.isLoggedIn();
    }
}
