package masterlazy.lazylogin.listeners;

import masterlazy.lazylogin.LoginMod;
import masterlazy.lazylogin.PlayerLogin;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class OnGameMessage {
    public static boolean canSendMessage(ServerPlayNetworkHandler networkHandler, ChatMessageC2SPacket packet) {
        ServerPlayerEntity player = networkHandler.player;
        PlayerLogin playerLogin = LoginMod.getPlayer(player);
        String message = packet.getChatMessage();
        // TODO: config to allow more commands when you're not logged
        if (!playerLogin.isLoggedIn() && (message.startsWith("/login") || message.startsWith("/register"))) {
            return true;
        }
        return playerLogin.isLoggedIn();
    }
}
