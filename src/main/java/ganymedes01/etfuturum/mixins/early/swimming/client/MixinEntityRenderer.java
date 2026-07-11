package ganymedes01.etfuturum.mixins.early.swimming.client;

import ganymedes01.etfuturum.swimming.IPlayerSwimming;
import ganymedes01.etfuturum.swimming.PlayerPose;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {

	@Shadow
	private Minecraft mc;

	@Unique
	private float etfu$cameraOffset;

	@Unique
	private float etfu$previousCameraOffset;

	@Unique
	private float etfu$partialTicks;

	@Inject(method = "orientCamera", at = @At("HEAD"))
	private void etfu$capturePartialTicks(float partialTicks, CallbackInfo ci) {
		this.etfu$partialTicks = partialTicks;
	}

	@ModifyVariable(
			method = "orientCamera",
			at = @At(value = "FIELD", target = "Lnet/minecraft/entity/EntityLivingBase;prevPosX:D", ordinal = 0),
			ordinal = 1)
	private float etfu$applyPoseCameraOffset(float original) {
		return this.etfu$previousCameraOffset
				+ (this.etfu$cameraOffset - this.etfu$previousCameraOffset) * this.etfu$partialTicks;
	}

	@Inject(method = "updateRenderer", at = @At("TAIL"))
	private void etfu$updatePoseCameraOffset(CallbackInfo ci) {
		this.etfu$previousCameraOffset = this.etfu$cameraOffset;
		float target = 0.0F;
		Entity viewEntity = this.mc.renderViewEntity;
		if (viewEntity instanceof IPlayerSwimming) {
			PlayerPose pose = ((IPlayerSwimming) viewEntity).etfu$getPose();
			if (pose.usesModernEyeHeight()) {
				target = pose.getCameraOffset();
			}
		}
		this.etfu$cameraOffset += (target - this.etfu$cameraOffset) * 0.5F;
	}
}
