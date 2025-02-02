package masterlazy.lazylogin.handler;

import masterlazy.lazylogin.LazyLogin;
import masterlazy.lazylogin.LangManager;
import masterlazy.lazylogin.PlayerLogin;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class OnChatMessage {
    public static boolean canSendMessage(ServerPlayNetworkHandler networkHandler, ChatMessageC2SPacket packet) {
        ServerPlayerEntity player = networkHandler.player;
        PlayerLogin playerLogin = LazyLogin.getPlayer(player);
        if(!playerLogin.isLoggedIn()){
            player.sendMessage(LangManager.getText("unlogged.msg"), false);
            return false;
        }
        return playerLogin.isLoggedIn();
    }
}
