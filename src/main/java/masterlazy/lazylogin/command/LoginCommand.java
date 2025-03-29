package masterlazy.lazylogin.command;

import masterlazy.lazylogin.LazyLogin;
import masterlazy.lazylogin.LangManager;
import masterlazy.lazylogin.PlayerSession;
import masterlazy.lazylogin.RegisteredPlayersJson;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class LoginCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("login")
                .then(argument("password", StringArgumentType.word())
                        .executes(ctx -> {
                            PlayerSession playerSession = LazyLogin.getPlayer(ctx.getSource().getPlayer());
                            String password = StringArgumentType.getString(ctx, "password");
                            ServerPlayerEntity player = ctx.getSource().getPlayer();
                            String username = player.getName().getString();

                            if (playerSession.isLoggedIn()) {
                                LazyLogin.sendFeedback(ctx, LangManager.get("login.logged"), false);
                            } else if (! RegisteredPlayersJson.isPlayerRegistered(username)) {
                                LazyLogin.sendFeedback(ctx, LangManager.get("login.unregistered"), false);
                            } else if (! RegisteredPlayersJson.isCorrectPassword(username, password)) {
                                LazyLogin.sendFeedback(ctx, LangManager.get("login.incorrectPwd"), false);
                            } else {
                                playerSession.setLoggedIn(true);
                                if (! player.isCreative()) {
                                    player.setInvulnerable(false);
                                }
                                LazyLogin.sendGlobalMessage(ctx, LangManager.get("login.success").replace("%s", username));
                                LazyLogin.LOGGER.info("(lazylogin) " + username + " logged in");
                                LazyLogin.playNotifySound(ctx);
                            }
                            return 1;
                        })));
    }
}
