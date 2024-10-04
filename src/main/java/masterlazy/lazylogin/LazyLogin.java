package masterlazy.lazylogin;

import masterlazy.lazylogin.commands.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LazyLogin implements ModInitializer {
    static GetPlayer getPlayer = new GetPlayer();
    public static final Logger LOGGER = LogManager.getLogger("lazylogin");

    @Override
    public void onInitialize() {
        RegisteredPlayersJson.read();
        LangManager.loadLang();
        // Register commands
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            LoginCommand.register(dispatcher);
            RegisterCommand.register(dispatcher);
            PasswordCommand.register(dispatcher);
        });
    }

    public static PlayerLogin getPlayer(ServerPlayerEntity player) {
        return getPlayer.get(player);
    }

    public static void sendGlobalMessage(MinecraftServer server, String msg) {
        PlayerManager playerManager = server.getPlayerManager();
        LiteralText literalText = new LiteralText(msg);
        for (ServerPlayerEntity player : playerManager.getPlayerList()) {
            player.sendMessage(literalText, false);
        }
    }
}
