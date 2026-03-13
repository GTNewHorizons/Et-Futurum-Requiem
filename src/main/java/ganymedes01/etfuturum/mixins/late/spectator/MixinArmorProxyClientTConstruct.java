package ganymedes01.etfuturum.mixins.late.spectator;

import net.minecraftforge.client.event.RenderPlayerEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ganymedes01.etfuturum.spectator.SpectatorMode;
import tconstruct.armor.ArmorProxyClient;

@Mixin(value = ArmorProxyClient.class, remap = false)
public class MixinArmorProxyClientTConstruct {

    @Inject(method = "adjustArmor", at = @At("HEAD"), cancellable = true)
    private void skipTConstructArmorExtrasForSpectator(RenderPlayerEvent.SetArmorModel event, CallbackInfo ci) {
        if (SpectatorMode.isSpectator(event.entityPlayer)) {
            ci.cancel();
        }
    }
}
