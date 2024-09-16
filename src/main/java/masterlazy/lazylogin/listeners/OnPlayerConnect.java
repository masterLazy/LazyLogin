package masterlazy.lazylogin.listeners;

import masterlazy.lazylogin.LoginMod;
import masterlazy.lazylogin.PlayerLogin;
import masterlazy.lazylogin.LangManager;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

public class OnPlayerConnect {
    public static void listen(ServerPlayerEntity player) {
        PlayerLogin playerLogin = LoginMod.getPlayer(player);
        playerLogin.setLoggedIn(false);
        player.setInvulnerable(true);
        player.sendMessage(LangManager.getText("connect.msg"), false);
        String title = LangManager.get("connect.title") + playerLogin.getPlayer().getEntityName();
        player.networkHandler.sendPacket(new TitleS2CPacket(TitleS2CPacket.Action.TITLE, new LiteralText(title)));
    }
}
