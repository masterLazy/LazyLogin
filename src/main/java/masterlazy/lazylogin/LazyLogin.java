package masterlazy.lazylogin;

import masterlazy.lazylogin.command.*;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.event.player.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.ServerCommandSource;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.SecureRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LazyLogin implements ModInitializer {
    static PlayerManager playerManager = new PlayerManager();
    public static final Logger LOGGER = LogManager.getLogger("lazylogin");
    private static final SecureRandom random = new SecureRandom();

    private ActionResult eventCallback(PlayerEntity player) {
        if (playerManager.get((ServerPlayerEntity) player).isLoggedIn()) {
            return ActionResult.PASS;
        } else {
            ((ServerPlayerEntity)player).networkHandler.sendPacket(new TitleS2CPacket(LangManager.getText("unlogged.title")));
            return ActionResult.FAIL;
        }
    }

    @Override
    public void onInitialize() {
        RegisteredPlayersJson.read();
        LangManager.loadLang();
        // Register commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            LoginCommand.register(dispatcher);
            RegisterCommand.register(dispatcher);
            PasswordCommand.register(dispatcher);
            WhitelistCommand.register(dispatcher);
        });
        // Register listeners
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> eventCallback(player));
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> eventCallback(player));
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> eventCallback(player));
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> eventCallback(player));
        UseItemCallback.EVENT.register((player, world, hand) -> eventCallback(player));
    }

    public static PlayerSession getPlayer(ServerPlayerEntity player) {
        return playerManager.get(player);
    }
    public static PlayerSession initPlayer(ServerPlayerEntity player) {
        return playerManager.init(player);
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
        net.minecraft.server.PlayerManager playerManager = ctx.getSource().getServer().getPlayerManager();
        for (ServerPlayerEntity player : playerManager.getPlayerList()) {
            player.sendMessage(Text.of(msg), false);
        }
    }
    public static void sendGlobalMessage(net.minecraft.server.PlayerManager playerManager, String msg) {
        for (ServerPlayerEntity player : playerManager.getPlayerList()) {
            player.sendMessage(Text.of(msg), false);
        }
    }

    public static void sendFeedback(CommandContext<ServerCommandSource> ctx, String msg, boolean broadcastToOps) {
        ctx.getSource().sendFeedback(() -> Text.literal(msg),broadcastToOps);
    }

    public static void playNotifySound(CommandContext<ServerCommandSource> ctx) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        if (player != null) {
            player.networkHandler.sendPacket(new PlaySoundS2CPacket(
                    RegistryEntry.of(SoundEvents.BLOCK_NOTE_BLOCK_PLING.value()),
                    SoundCategory.MASTER,
                    player.getPos().x,
                    player.getPos().y,
                    player.getPos().z,
                    1f,0f,0));
        }
    }

}
