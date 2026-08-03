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
public abstract class MixinEntityPlayer extends EntityLivingBase implements IPlayerSwimming {
	@Shadow
	public PlayerCapabilities capabilities;

	@Shadow
	public float cameraYaw;

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
		if (((IPoseablePlayer) this).etfu$getPose() == PlayerPose.SWIMMING && this.etfu$isActuallySneaking()) {
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
		return getPlayerPose() == PlayerPose.CRAWLING;
	}

	@Override
	public boolean etfu$isSwimming() {
		return SwimmingHooks.isEnabled() && !this.capabilities.isFlying
				&& !SpectatorMode.isSpectator((EntityPlayer) (Object) this)
				&& this.getFlag(ConfigFunctions.swimmingDataWatcherFlag);
	}

	@Override
	public boolean etfu$isActuallySwimming() {
		return getPlayerPose() == PlayerPose.SWIMMING || getPlayerPose() == PlayerPose.CRAWLING;
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
	public float etfu$getSwimAnimation(float partialTicks) {
		return this.etfu$previousSwimAnimation
				+ (this.etfu$swimAnimation - this.etfu$previousSwimAnimation) * partialTicks;
	}

	@Unique
	private IPlayerPose getPlayerPose() {
		return ((IPoseablePlayer) this).etfu$getPose();
	}
}
