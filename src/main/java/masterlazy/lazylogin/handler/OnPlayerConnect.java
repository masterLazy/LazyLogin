package masterlazy.lazylogin.handler;

import masterlazy.lazylogin.LazyLogin;
import masterlazy.lazylogin.PlayerSession;
import masterlazy.lazylogin.LangManager;
import masterlazy.lazylogin.RegisteredPlayersJson;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Objects;

public class OnPlayerConnect {
    public static void handle(ServerPlayerEntity player) {
        PlayerSession playerSession = LazyLogin.initPlayer(player);
        String username = player.getName().getString();
        if(player.getIp().equals("127.0.0.1")) {
            playerSession.setLoggedIn(true);
            LazyLogin.sendGlobalMessage(Objects.requireNonNull(player.getServer()).getPlayerManager(),
                    String.format(LangManager.get("login.local"), username));
            LazyLogin.LOGGER.info("[LazyLogin] Skipping login of local user {}", username);
        } else {
            playerSession.setLoggedIn(false);
        }
        player.sendMessage(LangManager.getText("connect.msg"), false);
        if (RegisteredPlayersJson.isPlayerRegistered(username)) {
            player.sendMessage(LangManager.getText("connect.oldUser"),false);
        } else {
            player.sendMessage(LangManager.getText("connect.newUser"),false);
        }
        String title = String.format(LangManager.get("connect.title"),player.getName().getString());
        player.networkHandler.sendPacket(new TitleS2CPacket(Text.of(title)));
    }
}
