package masterlazy.lazylogin.handler;

import masterlazy.lazylogin.LangManager;
import masterlazy.lazylogin.LazyLogin;
import masterlazy.lazylogin.PlayerLogin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;

public class OnPlayerAction {
    public static boolean canInteract(PlayerEntity _player) {
        ServerPlayerEntity player = (ServerPlayerEntity) _player;
        PlayerLogin playerLogin = LazyLogin.getPlayer(player);
        if(!playerLogin.isLoggedIn()) {
            player.networkHandler.sendPacket(new TitleS2CPacket(LangManager.getText("unlogged.title")));
        }
        return playerLogin.isLoggedIn();
    }
}
