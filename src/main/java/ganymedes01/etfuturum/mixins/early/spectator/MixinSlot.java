package ganymedes01.etfuturum.mixins.early.spectator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import ganymedes01.etfuturum.spectator.SpectatorMode;

@Mixin(Slot.class)
public class MixinSlot {

    @Inject(method = "canTakeStack", at = @At(value = "HEAD"), cancellable = true)
    private void cancelTake(EntityPlayer p_82869_1_, CallbackInfoReturnable<Boolean> cir) {
        if (SpectatorMode.isSpectator(p_82869_1_)) {
            cir.setReturnValue(false);
        }
    }
}
