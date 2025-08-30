package masterlazy.lazylogin.command;

import masterlazy.lazylogin.LazyLogin;
import masterlazy.lazylogin.LangManager;
import masterlazy.lazylogin.RegisteredPlayersJson;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Objects;

public class PasswordCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("password")
                .then(literal("change")
                        .then(argument("oldPassword", StringArgumentType.word())
                                .then(argument("newPassword", StringArgumentType.word())
                                        .then(argument("confirmPassword", StringArgumentType.word())
                                                .executes(ctx -> {
                                                    String oldPwd = StringArgumentType.getString(ctx, "oldPassword");
                                                    String newPwd = StringArgumentType.getString(ctx, "newPassword");
                                                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                                                    String username = Objects.requireNonNull(player).getName().getString();
                                                    if (!RegisteredPlayersJson.isCorrectPassword(player.getName().getString(), oldPwd)) {
                                                        LazyLogin.sendFeedback(ctx, LangManager.get("pwd.change.incorrectPwd"), false);
                                                    } else if (!newPwd.equals(StringArgumentType.getString(ctx, "confirmPassword"))) {
                                                        LazyLogin.sendFeedback(ctx, LangManager.get("pwd.change.pwdNotMatch"), false);
                                                    } else {
                                                        RegisteredPlayersJson.save(username, newPwd);
                                                        LazyLogin.sendFeedback(ctx, LangManager.get("pwd.change.success"), false);
                                                        LazyLogin.LOGGER.info("[LazyLogin] " + username + " changed password.");
                                                        LazyLogin.playNotifySound(ctx);
                                                    }
                                                    return 1;
                                                })))))
                .then(literal("reset")
                        .requires(source -> source.hasPermissionLevel(3)) //op only
                        .then(argument("target", StringArgumentType.word())
                                .executes(ctx -> {
                                    String target = StringArgumentType.getString(ctx, "target");
                                    if (!RegisteredPlayersJson.isPlayerRegistered(target)) {
                                        LazyLogin.sendFeedback(ctx, LangManager.get("pwd.reset.unregistered"), false);
                                    } else {
                                        String password = LazyLogin.generatePassword();
                                        RegisteredPlayersJson.save(target, password);
                                        MutableText feedback = Text.literal(LangManager.get("pwd.reset.success").replace("%s", target) + password);
                                        feedback.setStyle(feedback.getStyle()
                                                .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, password))
                                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,LangManager.getText(("pwd.copy"))))
                                        );
                                        LazyLogin.sendFeedback(ctx, feedback, false);
                                        LazyLogin.LOGGER.info("[LazyLogin] {}'s password has been reset to: {}", target, password);
                                    }
                                    return 1;
                                })))
                .then(literal("reload")
                        .requires(source -> source.hasPermissionLevel(3)) //op only
                        .executes(ctx -> {
                            RegisteredPlayersJson.read();
                            LazyLogin.sendFeedback(ctx, LangManager.get("pwd.reload.success"), true);
                            return 1;
                        }))
                .then(literal("list")
                        .requires(source -> source.hasPermissionLevel(3)) //op only
                        .executes(ctx -> {
                            String msg = "";
                            ArrayList<String> regList = RegisteredPlayersJson.getPlayers();
                            // List all registered players
                            msg = msg.concat(LangManager.get("pwd.list.begin").replace("%d", String.valueOf(regList.size())));
                            for (int i = 0; i < regList.size(); i++) {
                                msg = msg.concat(regList.get(i));
                                if (i < regList.size() - 1) {
                                    msg = msg.concat(",");
                                }
                            }
                            // Warn players in whitelist but not registered
                            PlayerManager playerManager = ctx.getSource().getServer().getPlayerManager();
                            ArrayList<String> warnList = new ArrayList<>();
                            regList.replaceAll(String::toLowerCase);
                            for (String s : playerManager.getOpList().getNames()) {
                                if (!regList.contains(s.toLowerCase())) {
                                    warnList.add(s);
                                }
                            }
                            for (String s : playerManager.getWhitelist().getNames()) {
                                if (!regList.contains(s.toLowerCase())) {
                                    warnList.add(s);
                                }
                            }
                            if (!warnList.isEmpty()) {
                                msg = msg.concat("\n").concat(LangManager.get("pwd.list.warn").replace("%d", String.valueOf(warnList.size())));
                                for (int i = 0; i < warnList.size(); i++) {
                                    msg = msg.concat(warnList.get(i));
                                    if (i < warnList.size() - 1) {
                                        msg = msg.concat(",");
                                    }
                                }
                            }
                            LazyLogin.sendFeedback(ctx, msg, false);
                            return 1;
                        })));

    }
}