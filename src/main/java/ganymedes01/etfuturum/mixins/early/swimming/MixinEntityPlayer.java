package ganymedes01.etfuturum.mixins.early.swimming;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import ganymedes01.etfuturum.api.elytra.IElytraPlayer;
import ganymedes01.etfuturum.configuration.configs.ConfigFunctions;
import ganymedes01.etfuturum.configuration.configs.ConfigMixins;
import ganymedes01.etfuturum.pose.IPlayerPose;
import ganymedes01.etfuturum.pose.IPoseablePlayer;
import ganymedes01.etfuturum.pose.PlayerPose;
import ganymedes01.etfuturum.pose.PlayerPoseManager;
import ganymedes01.etfuturum.pose.PlayerScaleEvent;
import ganymedes01.etfuturum.spectator.SpectatorMode;
import ganymedes01.etfuturum.swimming.IPlayerSwimming;
import ganymedes01.etfuturum.swimming.SwimmingHooks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
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
public abstract class MixinEntityPlayer extends EntityLivingBase implements IPoseablePlayer, IPlayerSwimming {
	@Shadow
	public PlayerCapabilities capabilities;
	@Shadow
	public float cameraYaw;

	@Shadow
	public abstract boolean isPlayerSleeping();

	@Unique
	private IPlayerPose etfu$pose = PlayerPose.STANDING;

	@Unique
	private boolean etfu$eyeInWater;

	@Unique
	private float etfu$swimAnimation;

	@Unique
	private float etfu$previousSwimAnimation;

	@Unique
	protected float etfu$scale = 1.0f;

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
		this.etfu$updateScale();
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
	private void etfu$updateScale() {
		PlayerScaleEvent event = new PlayerScaleEvent((EntityPlayer) (Object) this, 1.0f);
		MinecraftForge.EVENT_BUS.post(event);
		etfu$scale = event.scale;
	}

	@Override
	public float etfu$getScale() {
		return etfu$scale;
	}

	@Unique
	private void etfu$updatePose() {
		IPlayerPose desiredPose = PlayerPoseManager.getPose((EntityPlayer) (Object) this);
		this.etfu$setPose(desiredPose);
		this.etfu$applyPoseSize(desiredPose);
	}

	@Unique
	private void etfu$applyPoseSize(IPlayerPose pose) {
		float width = pose.getWidth() * etfu$getScale();
		float height = pose.getHeight() * etfu$getScale();
		if (Math.abs(this.width - width) > 0.001F || Math.abs(this.height - height) > 0.001F) {
			this.setSize(width, height);
		}
	}

	@Unique
	private boolean etfu$isFallFlying() {
		return ConfigMixins.enableElytra && this instanceof IElytraPlayer && ((IElytraPlayer) this).etfu$isElytraFlying();
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

	@ModifyReturnValue(method = "getEyeHeight", at = @At("RETURN"))
	private float etfu$getPoseEyeHeight(float origin) {
		if (this.worldObj.isRemote) {
			return origin * etfu$getScale();
		}
		return this.etfu$pose.getEyeHeight() * etfu$getScale();
	}

	@ModifyReturnValue(method = "getDefaultEyeHeight", at = @At("RETURN"), remap = false)
	private float etfu$getDefaultEyeHeight(float origin) {
		if (this.worldObj.isRemote) {
			return origin * etfu$getScale();
		}
		return this.etfu$pose.getEyeHeight() * etfu$getScale();
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
		return etfu$getPose() == PlayerPose.CRAWLING;
	}

	@Override
	public boolean isSneaking() {
		return super.isSneaking() || etfu$getPose() == PlayerPose.CROUCHING;
	}

	@Unique
	float etfu$CurrentYOffset = 0f;

	@Override
	public float etfu$getCurrentYOffset() {
		return etfu$CurrentYOffset;
	}

	@Override
	public void etfu$setCurrentYOffset(float offset) {
		etfu$CurrentYOffset = offset;
	}

	@Override
	public boolean etfu$isSwimming() {
		return SwimmingHooks.isEnabled() && !this.capabilities.isFlying
				&& !SpectatorMode.isSpectator((EntityPlayer) (Object) this)
				&& this.getFlag(ConfigFunctions.swimmingDataWatcherFlag);
	}

	@Override
	public boolean etfu$isActuallySwimming() {
		return etfu$getPose() == PlayerPose.SWIMMING || etfu$getPose() == PlayerPose.CRAWLING;
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
	public IPlayerPose etfu$getPose() {
		return this.etfu$pose;
	}

	@Override
	public void etfu$setPose(IPlayerPose pose) {
		this.etfu$pose = pose;
	}

	@Override
	public float etfu$getSwimAnimation(float partialTicks) {
		return this.etfu$previousSwimAnimation
				+ (this.etfu$swimAnimation - this.etfu$previousSwimAnimation) * partialTicks;
	}
}
