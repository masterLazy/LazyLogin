package masterlazy.lazylogin;

import masterlazy.lazylogin.commands.*;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
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

    public static String generatePassword() {
        final String CHAR = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        final int LENGTH = 8;
        return IntStream.range(0, LENGTH)
                .mapToObj(i -> String.valueOf(CHAR.charAt(random.nextInt(CHAR.length()))))
                .collect(Collectors.joining());
    }

    // Warps of APIs that is diff between versions

    public static void sendGlobalMessage(CommandContext<ServerCommandSource> ctx, String msg) {
        PlayerManager playerManager = ctx.getSource().getServer().getPlayerManager();
        for (ServerPlayerEntity player : playerManager.getPlayerList()) {
            player.sendMessage(Text.of(msg), false);
        }
    }

    public static void sendFeedback(CommandContext<ServerCommandSource> ctx, String msg, boolean broadcastToOps) {
        ctx.getSource().sendFeedback(() -> Text.literal(msg),broadcastToOps);
    }

    public static void playNotifySound(ServerPlayerEntity player) {
        player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_PLING, SoundCategory.BLOCKS, 1f, 1f);
    }

}
