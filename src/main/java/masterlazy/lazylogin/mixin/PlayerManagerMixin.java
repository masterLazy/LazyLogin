package masterlazy.lazylogin.mixin;

import masterlazy.lazylogin.handler.OnPlayerConnect;
import masterlazy.lazylogin.handler.OnPlayerLeave;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(method = "onPlayerConnect", at = @At("TAIL"))
    public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci) {
        OnPlayerConnect.handle(player);
    }

    @Inject(method = "remove", at = @At("TAIL"))
    public void remove(ServerPlayerEntity player, CallbackInfo ci) {
        OnPlayerLeave.handle(player);
    }
}
