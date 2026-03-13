package ganymedes01.etfuturum.mixins.early.spectator;

import net.minecraft.entity.Entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class MixinEntity {

    @Shadow
    public boolean noClip;

    @Inject(method = "isEntityInsideOpaqueBlock", at = @At("HEAD"), cancellable = true)
    private void ignoreBlockIfSpectator(CallbackInfoReturnable<Boolean> cir) {
        if (this.noClip) {
            cir.setReturnValue(false);
        }
    }
}
