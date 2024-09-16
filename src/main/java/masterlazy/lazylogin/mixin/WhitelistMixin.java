package masterlazy.lazylogin.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.Whitelist;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Whitelist.class)
public abstract class WhitelistMixin {
    /**
     * @author masterLazy
     * @reason Let whitelist check the username instead of uuid
     */
    @Overwrite
    public String toString(GameProfile gameProfile) {
        return gameProfile.getName();
    }
}