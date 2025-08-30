package masterlazy.lazylogin.command;

import masterlazy.lazylogin.LazyLogin;
import masterlazy.lazylogin.LangManager;
import masterlazy.lazylogin.RegisteredPlayersJson;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

import net.minecraft.server.Whitelist;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

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
                                    // Check if the player is in the whitelist
                                    if(Arrays.stream(whitelist.getNames()).noneMatch(s -> s.equals(target))) {
                                        // Register for target
                                        RegisteredPlayersJson.save(target, password);
                                        // Add target to whitelist
                                        ctx.getSource().getServer().getCommandManager().executeWithPrefix(
                                                ctx.getSource(),"whitelist add " + target);

                                        MutableText feedback = Text.literal(LangManager.get("whitelist.safe_add.pwd").replace("%s", target) + password);
                                        feedback.setStyle(feedback.getStyle()
                                                .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, password))
                                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,LangManager.getText(("pwd.copy"))))
                                        );
                                        LazyLogin.sendFeedback(ctx, feedback, false);
                                        LazyLogin.LOGGER.info("[LazyLogin] {}'s initial password is: {}", target, password);
                                    } else {
                                        LazyLogin.sendFeedback(ctx, LangManager.get("whitelist.safe_add.failed"), false);
                                    }
                                    return 1;
                                }))));
    }


}
