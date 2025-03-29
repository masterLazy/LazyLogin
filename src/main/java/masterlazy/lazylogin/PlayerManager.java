package masterlazy.lazylogin;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.UUID;

public class PlayerManager extends HashMap<UUID, PlayerSession> {
    public PlayerSession get(ServerPlayerEntity player){
        UUID uuid = player.getUuid();
        if (containsKey(uuid)) {
            return super.get(uuid);
        }
        PlayerSession newPlayer = new PlayerSession(player, player.getX(), player.getY(), player.getZ());
        put(uuid, newPlayer);
        return newPlayer;
    }
}
