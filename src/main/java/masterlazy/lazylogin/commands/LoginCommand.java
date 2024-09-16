package masterlazy.lazylogin.commands;

import masterlazy.lazylogin.LangManager;
import masterlazy.lazylogin.LoginMod;
import masterlazy.lazylogin.PlayerLogin;
import masterlazy.lazylogin.RegisteredPlayersJson;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;
import net.minecraft.network.packet.s2c.play.PlaySoundIdS2CPacket;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;

public class LoginCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("login")
                .then(argument("password", StringArgumentType.word())
                    .executes(ctx -> {
                        PlayerLogin playerLogin = LoginMod.getPlayer(ctx.getSource().getPlayer());
                        String password = StringArgumentType.getString(ctx, "password");
                        String username = ctx.getSource().getPlayer().getEntityName();
                        ServerPlayerEntity player = ctx.getSource().getPlayer();

                        if (playerLogin.isLoggedIn()) {
                            ctx.getSource().sendFeedback(LangManager.getText("login.logged"), false);
                        }
                        else if (!RegisteredPlayersJson.isPlayerRegistered(username)) {
                            ctx.getSource().sendFeedback(LangManager.getText("login.unregistered"), false);
                        } else if (RegisteredPlayersJson.isCorrectPassword(username, password)) {
                            playerLogin.setLoggedIn(true);
                            ctx.getSource().sendFeedback(LangManager.getText("login.success"), false);
                            LoginMod.LOGGER.info("(lazylogin) "+player.getEntityName()+" logged in.");
                            if (!player.isCreative()) {
                                player.setInvulnerable(false);
                            }
                            player.networkHandler.sendPacket(new PlaySoundIdS2CPacket(new Identifier("minecraft:block.note_block.pling"), SoundCategory.MASTER, player.getPos(), 100f, 0f));
                        } else {
                            ctx.getSource().sendFeedback(LangManager.getText("login.failed"), false);
                        }
                        return 1;
        })));
    }
}
