package ganymedes01.etfuturum.mixins.early.closedrops;

import net.minecraft.entity.player.EntityPlayerMP;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ganymedes01.etfuturum.core.handlers.ServerEventHandler;

@Mixin(EntityPlayerMP.class)
public class MixinEntityPlayerMP {

    @Inject(method = "closeContainer", at = @At("HEAD"))
    private void detectClosingContainer(CallbackInfo ci) {
        ServerEventHandler.playersClosedContainers.add((EntityPlayerMP) (Object) this);
    }
}
