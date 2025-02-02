package masterlazy.lazylogin.commands;

import masterlazy.lazylogin.LazyLogin;
import masterlazy.lazylogin.LangManager;
import masterlazy.lazylogin.RegisteredPlayersJson;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

import net.minecraft.server.Whitelist;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Arrays;

public class WhitelistCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("whitelist")
                .then(literal("safe-add")
                        .then(argument("target", StringArgumentType.word())
                                .requires(source -> source.hasPermissionLevel(3)) //op only
                                .executes(ctx -> {
                                    String target = StringArgumentType.getString(ctx, "target");
                                    String password = LazyLogin.generatePassword();
                                    Whitelist whitelist = ctx.getSource().getServer().getPlayerManager().getWhitelist();
                                    // Register for target
                                    RegisteredPlayersJson.save(target, password);
                                    // Add target to whitelist
                                    if(Arrays.stream(whitelist.getNames()).noneMatch(s -> s.equals(target))) {
                                        ctx.getSource().getServer().getCommandManager().executeWithPrefix(
                                                ctx.getSource(),"whitelist add" + target);

                                        String feedback = LangManager.get("whitelist.safe_add.pwd").replace("%s", target) + password;
                                        LazyLogin.sendFeedback(ctx, feedback, false);
                                        LazyLogin.LOGGER.info("(lazylogin) " + target + "'s initial password is: " + password);
                                    }
                                    else {
                                        LazyLogin.sendFeedback(ctx, LangManager.get("whitelist.safe_add.failed"), false);
                                    }
                                    return 1;
                                }))));
    }


}
