package masterlazy.lazylogin.commands;

import masterlazy.lazylogin.LazyLogin;
import masterlazy.lazylogin.LangManager;
import masterlazy.lazylogin.PlayerLogin;
import masterlazy.lazylogin.RegisteredPlayersJson;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class RegisterCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("register")
                .then(argument("newPassword", StringArgumentType.word())
                        .then(argument("confirmPassword", StringArgumentType.word())
                                .executes(ctx -> {
                                    String password = StringArgumentType.getString(ctx, "newPassword");
                                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                                    String username = player.getName().getString();
                                    if (RegisteredPlayersJson.isPlayerRegistered(username)) {
                                        LazyLogin.sendFeedback(ctx, LangManager.get("reg.registered"), false);
                                    } else if (! password.equals(StringArgumentType.getString(ctx, "confirmPassword"))) {
                                        LazyLogin.sendFeedback(ctx, LangManager.get("reg.pwdNotMatch"), false);
                                    } else {
                                        RegisteredPlayersJson.save(username, password);
                                        PlayerLogin playerLogin = LazyLogin.getPlayer(player);
                                        playerLogin.setLoggedIn(true);
                                        player.setInvulnerable(false);
                                        LazyLogin.sendFeedback(ctx, LangManager.get("reg.success"), false);
                                        LazyLogin.sendGlobalMessage(ctx,  LangManager.get("login.success").replace("%s", username));
                                        LazyLogin.LOGGER.info("(lazylogin) " + username + " registered");
                                        LazyLogin.playNotifySound(ctx);
                                    }
                                    return 1;
                                }))));
    }
}
