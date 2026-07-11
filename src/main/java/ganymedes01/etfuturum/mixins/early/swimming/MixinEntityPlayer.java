package ganymedes01.etfuturum.mixins.early.swimming;

import ganymedes01.etfuturum.configuration.configs.ConfigFunctions;
import ganymedes01.etfuturum.configuration.configs.ConfigMixins;
import ganymedes01.etfuturum.elytra.IElytraPlayer;
import ganymedes01.etfuturum.spectator.SpectatorMode;
import ganymedes01.etfuturum.swimming.IPlayerSwimming;
import ganymedes01.etfuturum.swimming.PlayerPose;
import ganymedes01.etfuturum.swimming.SwimmingHooks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends EntityLivingBase implements IPlayerSwimming {
	@Unique
	private static final float etfu$PLAYER_WIDTH = 0.6F;

	@Shadow
	public PlayerCapabilities capabilities;
	@Shadow
	public float cameraYaw;

	@Shadow
	public abstract boolean isPlayerSleeping();
	@Unique
	private PlayerPose etfu$pose = PlayerPose.STANDING;

	@Unique
	private boolean etfu$eyeInWater;

	@Unique
	private float etfu$swimAnimation;

	@Unique
	private float etfu$previousSwimAnimation;

	protected MixinEntityPlayer(World world) {
		super(world);
	}

	@Inject(method = "onUpdate", at = @At("TAIL"))
	private void etfu$updateModernSwimming(CallbackInfo ci) {
		if (!SwimmingHooks.isEnabled()) {
			return;
		}

		this.etfu$eyeInWater = this.isInsideOfMaterial(Material.water);
		this.etfu$updateSwimmingFlag();
		this.etfu$updatePose();
		this.etfu$updateSwimAnimation();
		if (this.etfu$isSwimming()) {
			this.cameraYaw *= 0.6F;
			this.cameraPitch = 0.0F;
		}
	}

	@Unique
	private void etfu$updateSwimmingFlag() {
		boolean swimming = this.etfu$isSwimming();
		boolean hasSwimmingInput = this.isSprinting() || swimming && this.etfu$isActuallySneaking();
		boolean canContinue = hasSwimmingInput && this.isInWater() && !this.isRiding()
				&& !this.capabilities.isFlying && !this.etfu$isFallFlying() && !SpectatorMode.isSpectator((EntityPlayer) (Object) this);
		boolean shouldSwim = canContinue && (swimming || this.etfu$eyeInWater);
		this.setFlag(ConfigFunctions.swimmingDataWatcherFlag, shouldSwim);
	}

	@Unique
	private void etfu$updatePose() {
		PlayerPose desiredPose;

		if (this.getHealth() <= 0.0F || this.isPlayerSleeping()) {
			this.etfu$applyPoseSize(PlayerPose.STANDING);
			this.etfu$pose = PlayerPose.STANDING;
			return;
		}

		if (this.etfu$isFallFlying()) {
			desiredPose = PlayerPose.FALL_FLYING;
		} else if (this.etfu$isSwimming()) {
			desiredPose = PlayerPose.SWIMMING;
		} else if (this.etfu$isActuallySneaking() && !this.capabilities.isFlying && !this.isOnLadder()) {
			desiredPose = PlayerPose.CROUCHING;
		} else {
			desiredPose = PlayerPose.STANDING;
		}
		if (!this.noClip && !this.isRiding() && this.etfu$canResize() && !this.etfu$isPoseClear(desiredPose)) {
			if (this.etfu$isPoseClear(PlayerPose.CROUCHING)) {
				desiredPose = PlayerPose.CROUCHING;
			} else if (ConfigMixins.enableCrawling && this.etfu$isPoseClear(PlayerPose.CRAWLING)) {
				desiredPose = PlayerPose.CRAWLING;
			} else if (this.etfu$pose.isLowProfile() && this.etfu$isPoseClear(this.etfu$pose)) {
				desiredPose = this.etfu$pose;
			} else {
				return;
			}
		}

		this.etfu$pose = desiredPose;
		this.etfu$applyPoseSize(desiredPose);
	}

	@Unique
	private boolean etfu$canResize() {
		final float tolerance = 0.025F;
		double boxWidth = this.boundingBox.maxX - this.boundingBox.minX;
		double boxHeight = this.boundingBox.maxY - this.boundingBox.minY;
		boolean normalWidth = Math.abs(this.width - etfu$PLAYER_WIDTH) < tolerance
				&& Math.abs(boxWidth - etfu$PLAYER_WIDTH) < tolerance;
		boolean knownHeight = Math.abs(this.height - PlayerPose.SWIMMING.height) < tolerance
				|| Math.abs(this.height - PlayerPose.CROUCHING.height) < tolerance
				|| Math.abs(this.height - PlayerPose.STANDING.height) < tolerance;
		boolean knownBoxHeight = Math.abs(boxHeight - PlayerPose.SWIMMING.height) < tolerance
				|| Math.abs(boxHeight - PlayerPose.CROUCHING.height) < tolerance
				|| Math.abs(boxHeight - PlayerPose.STANDING.height) < tolerance;
		return normalWidth && knownHeight && knownBoxHeight;
	}

	@Unique
	private void etfu$applyPoseSize(PlayerPose pose) {
		if (!this.etfu$canResize()) {
			return;
		}

		if (Math.abs(this.width - etfu$PLAYER_WIDTH) > 0.001F || Math.abs(this.height - pose.height) > 0.001F) {
			this.setSize(etfu$PLAYER_WIDTH, pose.height);
		}
	}

	@Unique
	private boolean etfu$isFallFlying() {
		return ConfigMixins.enableElytra && this instanceof IElytraPlayer
				&& ((IElytraPlayer) this).etfu$isElytraFlying();
	}

	@Unique
	private void etfu$updateSwimAnimation() {
		this.etfu$previousSwimAnimation = this.etfu$swimAnimation;
		if (this.etfu$isActuallySwimming()) {
			this.etfu$swimAnimation = Math.min(1.0F, this.etfu$swimAnimation + 0.09F);
		} else {
			this.etfu$swimAnimation = Math.max(0.0F, this.etfu$swimAnimation - 0.09F);
		}
	}

	@Inject(method = "getEyeHeight", at = @At("HEAD"), cancellable = true)
	private void etfu$getPoseEyeHeight(CallbackInfoReturnable<Float> cir) {
		if (!SwimmingHooks.isEnabled()) {
			return;
		}

		if (this.etfu$pose.usesModernEyeHeight()) {
			cir.setReturnValue(this.etfu$getLegacyEyeHeight(this.etfu$pose));
		}
	}

	@Unique
	private float etfu$getLegacyEyeHeight(PlayerPose pose) {
		/* EntityPlayerSP keeps posY at the standing camera anchor by retaining the
		 * legacy 1.62 yOffset. Convert the modern feet-relative eye height into
		 * that coordinate system. Server and remote players use feet-based
		 * positions and keep the modern value unchanged.
		 */
		if (this.worldObj.isRemote && this.yOffset > 1.0F) {
			return pose.eyeHeight - this.yOffset;
		}
		return pose.eyeHeight;
	}

	@Inject(method = "canTriggerWalking", at = @At("HEAD"), cancellable = true)
	private void etfu$suppressWalkingWhileSwimming(CallbackInfoReturnable<Boolean> cir) {
		if (this.etfu$isSwimming()) {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "moveEntityWithHeading", at = @At("HEAD"))
	private void etfu$steerSwimmingVertically(float strafe, float forward, CallbackInfo ci) {
		if (this.etfu$usesCrawlingMovement()) {
			this.setSprinting(false);
		}

		if (!this.etfu$isSwimming() || this.isRiding()) {
			return;
		}

		double lookY = this.getLookVec().yCoord;
		int x = MathHelper.floor_double(this.posX);
		int y = MathHelper.floor_double(this.boundingBox.maxY + 0.1D);
		int z = MathHelper.floor_double(this.posZ);
		Block blockAbove = this.worldObj.getBlock(x, y, z);
		if (lookY <= 0.0D || this.isJumping || blockAbove instanceof BlockLiquid || blockAbove instanceof IFluidBlock) {
			double response = lookY < -0.2D ? 0.085D : 0.06D;
			this.motionY += (lookY - this.motionY) * response;
		}
	}

	@ModifyVariable(method = "moveEntityWithHeading", at = @At("HEAD"), argsOnly = true, ordinal = 0)
	private float etfu$slowCrawlingStrafe(float strafe) {
		return this.etfu$adjustLowProfileMovement(strafe);
	}

	@ModifyVariable(method = "moveEntityWithHeading", at = @At("HEAD"), argsOnly = true, ordinal = 1)
	private float etfu$slowCrawlingForward(float forward) {
		return this.etfu$adjustLowProfileMovement(forward);
	}

	@Unique
	private float etfu$adjustLowProfileMovement(float movement) {
		if (this.etfu$pose == PlayerPose.SWIMMING && this.etfu$isActuallySneaking()) {
			return Math.abs(movement) > 1.0E-5F && Math.abs(movement) <= 0.30001F
					? movement / 0.3F : movement;
		}
		if (this.etfu$usesCrawlingMovement()) {
			boolean alreadySlowed = this.etfu$isActuallySneaking()
					&& Math.abs(movement) <= 0.30001F;
			return alreadySlowed ? movement : movement * 0.3F;
		}
		return movement;
	}

	@Unique
	private boolean etfu$usesCrawlingMovement() {
		return this.etfu$pose == PlayerPose.CRAWLING;
	}

	@Override
	public boolean isSneaking() {
		return SwimmingHooks.isEnabled() ? this.etfu$pose == PlayerPose.CROUCHING : super.isSneaking();
	}

	@Override
	public boolean etfu$isSwimming() {
		return SwimmingHooks.isEnabled() && !this.capabilities.isFlying
				&& !SpectatorMode.isSpectator((EntityPlayer) (Object) this)
				&& this.getFlag(ConfigFunctions.swimmingDataWatcherFlag);
	}

	@Override
	public boolean etfu$isActuallySwimming() {
		return this.etfu$pose == PlayerPose.SWIMMING || this.etfu$pose == PlayerPose.CRAWLING;
	}

	@Override
	public boolean etfu$isActuallySneaking() {
		return super.isSneaking();
	}

	@Override
	public boolean etfu$isEyeInWater() {
		return this.etfu$eyeInWater;
	}

	@Override
	public PlayerPose etfu$getPose() {
		return this.etfu$pose;
	}

	@Unique
	private boolean etfu$isPoseClear(PlayerPose pose) {
		float halfWidth = etfu$PLAYER_WIDTH / 2.0F;
		AxisAlignedBB poseBox = AxisAlignedBB.getBoundingBox(
				this.posX - halfWidth,
				this.boundingBox.minY,
				this.posZ - halfWidth,
				this.posX + halfWidth,
				this.boundingBox.minY + pose.height,
				this.posZ + halfWidth).contract(1.0E-7D, 1.0E-7D, 1.0E-7D);
		return this.worldObj.getCollidingBoundingBoxes((EntityPlayer) (Object) this, poseBox).isEmpty();
	}

	@Override
	public float etfu$getSwimAnimation(float partialTicks) {
		return this.etfu$previousSwimAnimation
				+ (this.etfu$swimAnimation - this.etfu$previousSwimAnimation) * partialTicks;
	}
}
