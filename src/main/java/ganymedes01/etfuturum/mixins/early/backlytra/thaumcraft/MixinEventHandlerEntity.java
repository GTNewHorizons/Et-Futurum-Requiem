package ganymedes01.etfuturum.mixins.early.backlytra.thaumcraft;

import net.minecraft.entity.player.EntityPlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import ganymedes01.etfuturum.elytra.IElytraPlayer;
import thaumcraft.common.lib.events.EventHandlerEntity;

@Mixin(EventHandlerEntity.class)
public abstract class MixinEventHandlerEntity {

    @Redirect(
        method = "updateSpeed",
        at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/EntityPlayer;moveForward:F"))
    private float checkElytra(EntityPlayer instance) {
        if (instance instanceof IElytraPlayer && ((IElytraPlayer) instance).etfu$isElytraFlying()) {
            return 0.0F;
        }
        return instance.moveForward;
    }
}
