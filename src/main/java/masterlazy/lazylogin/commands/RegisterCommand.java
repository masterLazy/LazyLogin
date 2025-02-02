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
                                    String username = player.getEntityName();
                                    if (RegisteredPlayersJson.isPlayerRegistered(username)) {
                                        ctx.getSource().sendFeedback(LangManager.getText("reg.registered"), false);
                                    } else if (! password.equals(StringArgumentType.getString(ctx, "confirmPassword"))) {
                                        ctx.getSource().sendFeedback(LangManager.getText("reg.pwdNotMatch"), false);
                                    } else {
                                        RegisteredPlayersJson.save(username, password);
                                        PlayerLogin playerLogin = LazyLogin.getPlayer(player);
                                        playerLogin.setLoggedIn(true);
                                        player.setInvulnerable(false);
                                        ctx.getSource().sendFeedback(LangManager.getText("reg.success"), false);
                                        LazyLogin.sendGlobalMessage(ctx.getSource().getMinecraftServer(),
                                                LangManager.get("login.success").replace("%s", username));
                                        LazyLogin.LOGGER.info("(lazylogin) " + username + " registered");
                                        LazyLogin.playNotifySound(player);
                                    }
                                    return 1;
                                }))));
    }
}
