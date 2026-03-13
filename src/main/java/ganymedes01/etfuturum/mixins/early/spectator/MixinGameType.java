package ganymedes01.etfuturum.mixins.early.spectator;

import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.world.WorldSettings;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ganymedes01.etfuturum.spectator.SpectatorMode;

@Mixin(WorldSettings.GameType.class)
public class MixinGameType {

    @Unique
    private boolean etfuturum$isSpectator() {
        return (Object) this == SpectatorMode.SPECTATOR_GAMETYPE;
    }

    @Inject(method = "configurePlayerCapabilities", at = @At("HEAD"), cancellable = true)
    public void configureSpecCaps(PlayerCapabilities caps, CallbackInfo ci) {
        if (etfuturum$isSpectator()) {
            ci.cancel();
            caps.allowFlying = true;
            caps.isCreativeMode = false;
            caps.disableDamage = true;
            caps.allowEdit = false;
            caps.isFlying = true;
        }
    }
}
