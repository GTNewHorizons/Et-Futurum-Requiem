package ganymedes01.etfuturum.mixins.early.swimming.client;

import ganymedes01.etfuturum.pose.IPoseablePlayer;
import ganymedes01.etfuturum.pose.PlayerPose;
import ganymedes01.etfuturum.swimming.IPlayerSwimming;
import net.minecraft.client.entity.EntityClientPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityClientPlayerMP.class)
public abstract class MixinEntityClientPlayerMP {

	@Redirect(
			method = "sendMotionUpdates",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityClientPlayerMP;isSneaking()Z"))
	private boolean etfu$sendActualSneakInput(EntityClientPlayerMP player) {
		return player instanceof IPlayerSwimming
				? ((IPlayerSwimming) player).etfu$isActuallySneaking() : player.isSneaking();
	}

	@Inject(method = "onUpdate", at = @At("HEAD"))
	private void updateYOffset(CallbackInfo ci) {
		IPoseablePlayer p = (IPoseablePlayer) this;
		float targetYOffset = PlayerPose.STANDING.getEyeHeight() - p.etfu$getPose().getEyeHeight() * p.etfu$getScale();
		targetYOffset = Math.min(targetYOffset, 1.62f);
		float currentYOffset = p.etfu$getCurrentYOffset();
		if (currentYOffset == targetYOffset) {
			return;
		}
		currentYOffset += (targetYOffset - currentYOffset) * 0.5F;
		p.etfu$setCurrentYOffset(currentYOffset);
		((EntityClientPlayerMP) (Object) this).yOffset = 1.62f - currentYOffset;
	}
}
