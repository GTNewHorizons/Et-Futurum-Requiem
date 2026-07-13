package ganymedes01.etfuturum.mixins.early.swimming.client;

import com.mojang.authlib.GameProfile;
import ganymedes01.etfuturum.configuration.configs.ConfigMixins;
import ganymedes01.etfuturum.pose.IPlayerPose;
import ganymedes01.etfuturum.pose.IPoseablePlayer;
import ganymedes01.etfuturum.pose.PlayerPose;
import ganymedes01.etfuturum.swimming.IPlayerSwimming;
import ganymedes01.etfuturum.swimming.SwimmingHooks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.potion.Potion;
import net.minecraft.util.MovementInput;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer {

	@Shadow
	protected Minecraft mc;

	@Shadow
	public MovementInput movementInput;

	@Unique
	private float etfu$startingYSize;
	@Unique
	private boolean etfu$wasSwimmingPose;

	protected MixinEntityPlayerSP(World world, GameProfile profile) {
		super(world, profile);
	}

	@Inject(method = "onLivingUpdate", at = @At("TAIL"))
	private void etfu$handleModernSwimmingInput(CallbackInfo ci) {
		if (!SwimmingHooks.isEnabled()) {
			return;
		}
		this.etfu$undoLegacySneakNudgeWhileLowProfile();

		IPlayerSwimming swimming = (IPlayerSwimming) this;
		if (this.etfu$wasSwimmingPose && this.isInWater() && this.movementInput.sneak
				&& !this.capabilities.isFlying) {
			this.setSprinting(true);
		}
		if (this.isInWater() && this.movementInput.sneak && !this.capabilities.isFlying) {
			this.motionY -= 0.04D;
		}

		boolean movingForward = this.mc.gameSettings.keyBindForward.getIsKeyPressed()
				&& !this.mc.gameSettings.keyBindBack.getIsKeyPressed();
		boolean canSprint = this.getFoodStats().getFoodLevel() > 6 || this.capabilities.allowFlying;
		if (swimming.etfu$isEyeInWater() && movingForward && canSprint && !this.isUsingItem()
				&& !this.isPotionActive(Potion.blindness) && this.mc.gameSettings.keyBindSprint.getIsKeyPressed()) {
			this.setSprinting(true);
		} else if (swimming.etfu$isSwimming()
				&& (!this.movementInput.sneak && !movingForward || !this.isInWater())) {
			this.setSprinting(false);
		}
	}

	@Inject(method = "onLivingUpdate", at = @At("HEAD"))
	private void etfu$captureLegacySneakOffset(CallbackInfo ci) {
		if (!SwimmingHooks.isEnabled()) {
			return;
		}
		this.etfu$startingYSize = this.ySize;
		this.etfu$wasSwimmingPose = ((IPoseablePlayer) this).etfu$getPose() == PlayerPose.SWIMMING;
	}

	@Inject(method = "isSneaking", at = @At("HEAD"), cancellable = true)
	private void etfu$useVisualCrouchingState(CallbackInfoReturnable<Boolean> cir) {
		if (SwimmingHooks.isEnabled()) {
			IPlayerPose pose = ((IPoseablePlayer) this).etfu$getPose();
			if (ConfigMixins.enableModernSneaking || pose == PlayerPose.CROUCHING && !this.etfu$isActuallySneaking()) {
				cir.setReturnValue(pose == PlayerPose.CROUCHING);
			}
		}
	}

	public boolean etfu$isActuallySneaking() {
		return this.movementInput != null && this.movementInput.sneak;
	}

	@Unique
	private void etfu$undoLegacySneakNudgeWhileLowProfile() {
		IPlayerPose pose = ((IPoseablePlayer) this).etfu$getPose();
		if (!(pose.getEyeHeight() != PlayerPose.STANDING.getEyeHeight()) || this.movementInput == null
				|| !this.movementInput.sneak) {
			return;
		}

		float nudge = this.ySize - this.etfu$startingYSize;
		if (nudge > 1.0E-6F) {
			this.posY += nudge;
			this.ySize = this.etfu$startingYSize;
		}
	}
}
