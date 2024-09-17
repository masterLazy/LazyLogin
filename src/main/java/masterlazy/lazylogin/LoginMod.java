package masterlazy.lazylogin;

import masterlazy.lazylogin.commands.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.network.ServerPlayerEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoginMod implements ModInitializer {
    static GetPlayer getPlayer = new GetPlayer();
    public static final Logger LOGGER = LogManager.getLogger("lazylogin");

    @Override
    public void onInitialize() {
        RegisteredPlayersJson.read();
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            LoginCommand.register(dispatcher);
            RegisterCommand.register(dispatcher);
            PasswordCommand.register(dispatcher);
        });
        LangManager.loadLang();
    }

    public static PlayerLogin getPlayer(ServerPlayerEntity player) {
        return getPlayer.get(player);
    }
}
