package masterlazy.lazylogin.commands;

import masterlazy.lazylogin.LangManager;
import masterlazy.lazylogin.LoginMod;
import masterlazy.lazylogin.RegisteredPlayersJson;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

import net.minecraft.network.packet.s2c.play.PlaySoundIdS2CPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.command.argument.EntityArgumentType;

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
                                                    }
                                                    else if (! newPwd.equals(StringArgumentType.getString(ctx, "confirmPassword"))) {
                                                        ctx.getSource().sendFeedback(LangManager.getText("pwd.change.pwdNotMatch"), false);
                                                    } else {
                                                        RegisteredPlayersJson.save(username, newPwd);
                                                        ctx.getSource().sendFeedback(LangManager.getText("pwd.change.success"), false);
                                                        LoginMod.LOGGER.info("(lazylogin) " + username + " changed newPwd.");
                                                        player.networkHandler.sendPacket(new PlaySoundIdS2CPacket(
                                                                new Identifier("minecraft:block.note_block.pling"),
                                                                SoundCategory.MASTER, player.getPos(), 100f, 0f));
                                                    }
                                                    return 1;
                                                })))))
                .then(literal("reset")
                        .requires(source -> source.hasPermissionLevel(4)) //op only
                        .then(argument("username", EntityArgumentType.player())
                                .then(argument("yourPassword", StringArgumentType.word())
                                        .executes(ctx -> {
                                            String username = EntityArgumentType.getPlayer(ctx, "username").getEntityName();
                                            String opPassword = StringArgumentType.getString(ctx, "yourPassword");
                                            ServerPlayerEntity player = ctx.getSource().getPlayer();
                                            if (! RegisteredPlayersJson.isCorrectPassword(player.getEntityName(), opPassword)) {
                                                ctx.getSource().sendFeedback(LangManager.getText("pwd.reset.incorrectPwd"), false);
                                            } else if (! RegisteredPlayersJson.remove(username)) {
                                                ctx.getSource().sendFeedback(LangManager.getText("pwd.reset.unregistered"), false);
                                            } else {
                                                String feedback = LangManager.get("pwd.reset.success").replace("<playername>", username);
                                                ctx.getSource().sendFeedback(new LiteralText(feedback), true);
                                                LoginMod.LOGGER.warn("(lazylogin) Reset password of " + username + ".");
                                                player.networkHandler.sendPacket(new PlaySoundIdS2CPacket(
                                                        new Identifier("minecraft:block.note_block.pling"),
                                                        SoundCategory.MASTER, player.getPos(), 100f, 0f));
                                            }
                                            return 1;
                                        }))))
                .then(literal("reload")
                        .requires(source -> source.hasPermissionLevel(4)) //op only
                        .executes(ctx -> {
                            ServerPlayerEntity player = ctx.getSource().getPlayer();
                            RegisteredPlayersJson.read();
                            ctx.getSource().sendFeedback(LangManager.getText("pwd.reload.success"), true);
                            LoginMod.LOGGER.info("(lazylogin) Reloaded passwords.");
                            player.networkHandler.sendPacket(new PlaySoundIdS2CPacket(
                                    new Identifier("minecraft:block.note_block.pling"),
                                    SoundCategory.MASTER, player.getPos(), 100f, 0f));
                            return 1;
                        })));

    }
}