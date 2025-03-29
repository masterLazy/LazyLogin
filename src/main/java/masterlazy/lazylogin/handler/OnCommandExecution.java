package masterlazy.lazylogin.handler;

import masterlazy.lazylogin.LangManager;
import masterlazy.lazylogin.LazyLogin;
import masterlazy.lazylogin.PlayerSession;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class OnCommandExecution {
    public static boolean canExecuteCommand(ServerPlayNetworkHandler networkHandler, CommandExecutionC2SPacket packet) {
        ServerPlayerEntity player = networkHandler.player;
        PlayerSession playerSession = LazyLogin.getPlayer(player);
        String command = packet.command();
        // TODO: config to allow more commands when you're not logged
        if (!playerSession.isLoggedIn() && (command.startsWith("login") || command.startsWith("register"))) {
            return true;
        } else if(!playerSession.isLoggedIn()){
            player.sendMessage(LangManager.getText("unlogged.cmd"), false);
            return false;
        }
        return playerSession.isLoggedIn();
    }
}
