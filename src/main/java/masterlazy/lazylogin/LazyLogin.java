package masterlazy.lazylogin;

import masterlazy.lazylogin.commands.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.SecureRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LazyLogin implements ModInitializer {
    static GetPlayer getPlayer = new GetPlayer();
    public static final Logger LOGGER = LogManager.getLogger("lazylogin");
    private static final SecureRandom random = new SecureRandom();

    @Override
    public void onInitialize() {
        RegisteredPlayersJson.read();
        LangManager.loadLang();
        // Register commands
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            LoginCommand.register(dispatcher);
            RegisterCommand.register(dispatcher);
            PasswordCommand.register(dispatcher);
            WhitelistCommand.register(dispatcher);
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

    public static String generatePassword() {
        final String CHAR = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        final int LENGTH = 8;
        return IntStream.range(0, LENGTH)
                .mapToObj(i -> String.valueOf(CHAR.charAt(random.nextInt(CHAR.length()))))
                .collect(Collectors.joining());
    }
}
