package masterlazy.lazylogin.handler;

import masterlazy.lazylogin.LazyLogin;
import masterlazy.lazylogin.LangManager;
import masterlazy.lazylogin.PlayerSession;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class OnChatMessage {
    public static boolean canSendMessage(ServerPlayNetworkHandler networkHandler) {
        ServerPlayerEntity player = networkHandler.player;
        PlayerSession playerSession = LazyLogin.getPlayer(player);
        if(!playerSession.isLoggedIn()){
            player.sendMessage(LangManager.getText("unlogged.msg"), false);
            return false;
        }
        return playerSession.isLoggedIn();
    }
}
