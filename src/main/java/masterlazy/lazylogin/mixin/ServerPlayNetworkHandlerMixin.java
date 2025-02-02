package masterlazy.lazylogin.mixin;

import masterlazy.lazylogin.handler.OnChatMessage;
import masterlazy.lazylogin.handler.OnCommandExecution;
import masterlazy.lazylogin.handler.OnPlayerMove;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Inject(method = "onPlayerMove", at = @At("HEAD"), cancellable = true)
    public void onPlayerMove(PlayerMoveC2SPacket packet, CallbackInfo ci) {
        if (!OnPlayerMove.canMove((ServerPlayNetworkHandler) (Object) this)) {
            ci.cancel();
        }
    }

    @Inject(method = "onChatMessage", at = @At("HEAD"), cancellable = true)
    public void onChatMessage(ChatMessageC2SPacket packet, CallbackInfo ci) {
        if (!OnChatMessage.canSendMessage((ServerPlayNetworkHandler) (Object) this, packet)) {
            ci.cancel();
        }
    }

    @Inject(method = "onCommandExecution", at = @At("HEAD"), cancellable = true)
    public void onCommandExecution(CommandExecutionC2SPacket packet, CallbackInfo ci) {
        if (!OnCommandExecution.canExecuteCommand((ServerPlayNetworkHandler) (Object) this, packet)) {
            ci.cancel();
        }
    }
}
