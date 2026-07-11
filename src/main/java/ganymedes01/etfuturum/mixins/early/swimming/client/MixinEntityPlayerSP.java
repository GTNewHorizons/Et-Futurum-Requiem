package ganymedes01.etfuturum.mixins.early.swimming.client;

import com.mojang.authlib.GameProfile;
import ganymedes01.etfuturum.swimming.IPlayerSwimming;
import ganymedes01.etfuturum.swimming.PlayerPose;
import ganymedes01.etfuturum.swimming.SwimmingHooks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.potion.Potion;
import net.minecraft.util.MovementInput;
import net.minecraft.util.Vec3;
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
		this.etfu$wasSwimmingPose = ((IPlayerSwimming) this).etfu$getPose() == PlayerPose.SWIMMING;
	}

	@Inject(method = "isSneaking", at = @At("HEAD"), cancellable = true)
	private void etfu$useVisualCrouchingState(CallbackInfoReturnable<Boolean> cir) {
		if (SwimmingHooks.isEnabled()) {
			cir.setReturnValue(((IPlayerSwimming) this).etfu$getPose() == PlayerPose.CROUCHING);
		}
	}

	public boolean etfu$isActuallySneaking() {
		return this.movementInput != null && this.movementInput.sneak;
	}

	@Override
	public Vec3 getPosition(float partialTicks) {
		if (SwimmingHooks.isEnabled()) {
			PlayerPose pose = ((IPlayerSwimming) this).etfu$getPose();
			if (pose.usesModernEyeHeight()) {
				double x = this.prevPosX + (this.posX - this.prevPosX) * partialTicks;
				double y = this.prevPosY + (this.posY - this.prevPosY) * partialTicks - pose.getCameraOffset();
				double z = this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks;
				return Vec3.createVectorHelper(x, y, z);
			}
		}
		return super.getPosition(partialTicks);
	}

	@Unique
	private void etfu$undoLegacySneakNudgeWhileLowProfile() {
		PlayerPose pose = ((IPlayerSwimming) this).etfu$getPose();
		if (!pose.usesModernEyeHeight() || this.movementInput == null
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
