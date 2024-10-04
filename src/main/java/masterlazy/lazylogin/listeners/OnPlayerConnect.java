package masterlazy.lazylogin.listeners;

import masterlazy.lazylogin.LazyLogin;
import masterlazy.lazylogin.PlayerLogin;
import masterlazy.lazylogin.LangManager;
import masterlazy.lazylogin.RegisteredPlayersJson;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

public class OnPlayerConnect {
    public static void listen(ServerPlayerEntity player) {
        PlayerLogin playerLogin = LazyLogin.getPlayer(player);
        String username = player.getEntityName();
        playerLogin.setLoggedIn(false);
        player.setInvulnerable(true);
        player.sendMessage(LangManager.getText("connect.msg"), false);
        if (RegisteredPlayersJson.isPlayerRegistered(username)) {
            player.sendMessage(LangManager.getText("connect.oldUser"),false);
        } else {
            player.sendMessage(LangManager.getText("connect.newUser"),false);
        }
        String title = LangManager.get("connect.title").replace("%s",player.getEntityName());
        player.networkHandler.sendPacket(new TitleS2CPacket(TitleS2CPacket.Action.TITLE, new LiteralText(title)));
    }
}
