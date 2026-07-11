package ganymedes01.etfuturum.mixins.early.swimming.client;

import ganymedes01.etfuturum.swimming.IPlayerSwimming;
import ganymedes01.etfuturum.swimming.SwimmingHooks;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ActiveRenderInfo.class)
public abstract class MixinActiveRenderInfo {

	@Redirect(
			method = "projectViewFromEntity",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/EntityLivingBase;getEyeHeight()F"))
	private static float etfu$avoidApplyingCameraOffsetTwice(EntityLivingBase entity) {
		if (SwimmingHooks.isEnabled() && entity instanceof IPlayerSwimming && entity instanceof EntityPlayer
				&& entity.worldObj.isRemote && entity.yOffset > 1.0F) {
			/* orientCamera's model-view matrix already contains the interpolated pose offset. */
			return ((EntityPlayer) entity).getDefaultEyeHeight();
		}
		return entity.getEyeHeight();
	}
}
