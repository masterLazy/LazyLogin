package masterlazy.lazylogin.commands;

import masterlazy.lazylogin.LazyLogin;
import masterlazy.lazylogin.LangManager;
import masterlazy.lazylogin.RegisteredPlayersJson;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

import net.minecraft.network.packet.s2c.play.PlaySoundIdS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

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
                                                    String username = player.getEntityName();
                                                    if (! RegisteredPlayersJson.isCorrectPassword(player.getEntityName(), oldPwd)) {
                                                        ctx.getSource().sendFeedback(LangManager.getText("pwd.change.incorrectPwd"), false);
                                                    } else if (! newPwd.equals(StringArgumentType.getString(ctx, "confirmPassword"))) {
                                                        ctx.getSource().sendFeedback(LangManager.getText("pwd.change.pwdNotMatch"), false);
                                                    } else {
                                                        RegisteredPlayersJson.save(username, newPwd);
                                                        ctx.getSource().sendFeedback(LangManager.getText("pwd.change.success"), false);
                                                        LazyLogin.LOGGER.info("(lazylogin) " + username + " changed newPwd.");
                                                        player.networkHandler.sendPacket(new PlaySoundIdS2CPacket(
                                                                new Identifier("minecraft:block.note_block.pling"),
                                                                SoundCategory.MASTER, player.getPos(), 100f, 0f));
                                                    }
                                                    return 1;
                                                })))))
                .then(literal("reset")
                        .requires(source -> source.hasPermissionLevel(3)) //op only
                        .then(argument("target", StringArgumentType.word())
                                .executes(ctx -> {
                                    String target = StringArgumentType.getString(ctx, "target");
                                    if (! RegisteredPlayersJson.isPlayerRegistered(target)) {
                                        ctx.getSource().sendFeedback(LangManager.getText("pwd.reset.unregistered"), false);
                                    } else {
                                        String password = LazyLogin.generatePassword();
                                        RegisteredPlayersJson.save(target, password);
                                        String feedback = LangManager.get("pwd.reset.success").replace("%s", target) + password;
                                        ctx.getSource().sendFeedback(new LiteralText(feedback), false);
                                        LazyLogin.LOGGER.info("(lazylogin) " + target + "'s password has been reset to: " + password);
                                    }
                                    return 1;
                                })))
                .then(literal("reload")
                        .requires(source -> source.hasPermissionLevel(3)) //op only
                        .executes(ctx -> {
                            RegisteredPlayersJson.read();
                            ctx.getSource().sendFeedback(LangManager.getText("pwd.reload.success"), true);
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
                            PlayerManager playerManager = ctx.getSource().getMinecraftServer().getPlayerManager();
                            String[] opList = playerManager.getOpList().getNames();
                            String[] whiteList = playerManager.getWhitelist().getNames();
                            ArrayList<String> warnList = new ArrayList<>();
                            for (String s : opList) {
                                if (! regList.contains(s)) {
                                    warnList.add(s);
                                }
                            }
                            for (String s : whiteList) {
                                if (! regList.contains(s)) {
                                    warnList.add(s);
                                }
                            }
                            if (! warnList.isEmpty()) {
                                msg = msg.concat("\n").concat(LangManager.get("pwd.list.warn").replace("%d", String.valueOf(warnList.size())));
                                for (int i = 0; i < warnList.size(); i++) {
                                    msg = msg.concat(warnList.get(i));
                                    if (i < warnList.size() - 1) {
                                        msg = msg.concat(",");
                                    }
                                }
                            }
                            ctx.getSource().sendFeedback(new LiteralText(msg), false);
                            return 1;
                        })));

    }
}