package ganymedes01.etfuturum.mixins.early.swimming.client;

import ganymedes01.etfuturum.elytra.IElytraPlayer;
import ganymedes01.etfuturum.swimming.IPlayerSwimming;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelBiped.class)
public abstract class MixinModelBiped extends ModelBase {

	@Shadow public ModelRenderer bipedHead;
	@Shadow public ModelRenderer bipedHeadwear;
	@Shadow public ModelRenderer bipedRightArm;
	@Shadow public ModelRenderer bipedLeftArm;
	@Shadow public ModelRenderer bipedRightLeg;
	@Shadow public ModelRenderer bipedLeftLeg;

	@Unique
	private float etfu$swimAnimation;

	@Inject(method = "setRotationAngles", at = @At("TAIL"))
	private void etfu$applySwimmingAnimation(float limbSwing, float limbSwingAmount, float ageInTicks,
			float netHeadYaw, float headPitch, float scaleFactor, Entity entity, CallbackInfo ci) {
		if (this.etfu$swimAnimation <= 0.0F || !(entity instanceof IPlayerSwimming)
				|| entity instanceof IElytraPlayer && ((IElytraPlayer) entity).etfu$isElytraFlying()) {
			return;
		}

		IPlayerSwimming swimming = (IPlayerSwimming) entity;
		if (swimming.etfu$isActuallySwimming()) {
			this.bipedHead.rotateAngleX = this.etfu$rotLerp(this.etfu$swimAnimation,
					this.bipedHead.rotateAngleX, -(float) Math.PI / 4.0F);
			this.bipedHeadwear.rotateAngleX = this.bipedHead.rotateAngleX;
		}

		float cycle = limbSwing % 26.0F;
		float armBlend = this.onGround > 0.0F ? 0.0F : this.etfu$swimAnimation;
		if (cycle < 14.0F) {
			this.bipedLeftArm.rotateAngleX = this.etfu$rotLerp(armBlend, this.bipedLeftArm.rotateAngleX, 0.0F);
			this.bipedRightArm.rotateAngleX = this.etfu$lerp(armBlend, this.bipedRightArm.rotateAngleX, 0.0F);
			this.bipedLeftArm.rotateAngleY = this.etfu$rotLerp(armBlend, this.bipedLeftArm.rotateAngleY, (float) Math.PI);
			this.bipedRightArm.rotateAngleY = this.etfu$lerp(armBlend, this.bipedRightArm.rotateAngleY, (float) Math.PI);
			this.bipedLeftArm.rotateAngleZ = this.etfu$rotLerp(armBlend, this.bipedLeftArm.rotateAngleZ,
					(float) Math.PI + 1.8707964F * this.etfu$armAngle(cycle) / this.etfu$armAngle(14.0F));
			this.bipedRightArm.rotateAngleZ = this.etfu$lerp(armBlend, this.bipedRightArm.rotateAngleZ,
					(float) Math.PI - 1.8707964F * this.etfu$armAngle(cycle) / this.etfu$armAngle(14.0F));
		} else if (cycle < 22.0F) {
			float progress = (cycle - 14.0F) / 8.0F;
			this.bipedLeftArm.rotateAngleX = this.etfu$rotLerp(armBlend, this.bipedLeftArm.rotateAngleX,
					((float) Math.PI / 2.0F) * progress);
			this.bipedRightArm.rotateAngleX = this.etfu$lerp(armBlend, this.bipedRightArm.rotateAngleX,
					((float) Math.PI / 2.0F) * progress);
			this.bipedLeftArm.rotateAngleY = this.etfu$rotLerp(armBlend, this.bipedLeftArm.rotateAngleY, (float) Math.PI);
			this.bipedRightArm.rotateAngleY = this.etfu$lerp(armBlend, this.bipedRightArm.rotateAngleY, (float) Math.PI);
			this.bipedLeftArm.rotateAngleZ = this.etfu$rotLerp(armBlend, this.bipedLeftArm.rotateAngleZ,
					5.012389F - 1.8707964F * progress);
			this.bipedRightArm.rotateAngleZ = this.etfu$lerp(armBlend, this.bipedRightArm.rotateAngleZ,
					1.2707963F + 1.8707964F * progress);
		} else {
			float progress = (cycle - 22.0F) / 4.0F;
			float armX = (float) Math.PI / 2.0F * (1.0F - progress);
			this.bipedLeftArm.rotateAngleX = this.etfu$rotLerp(armBlend, this.bipedLeftArm.rotateAngleX, armX);
			this.bipedRightArm.rotateAngleX = this.etfu$lerp(armBlend, this.bipedRightArm.rotateAngleX, armX);
			this.bipedLeftArm.rotateAngleY = this.etfu$rotLerp(armBlend, this.bipedLeftArm.rotateAngleY, (float) Math.PI);
			this.bipedRightArm.rotateAngleY = this.etfu$lerp(armBlend, this.bipedRightArm.rotateAngleY, (float) Math.PI);
			this.bipedLeftArm.rotateAngleZ = this.etfu$rotLerp(armBlend, this.bipedLeftArm.rotateAngleZ, (float) Math.PI);
			this.bipedRightArm.rotateAngleZ = this.etfu$lerp(armBlend, this.bipedRightArm.rotateAngleZ, (float) Math.PI);
		}

		this.bipedLeftLeg.rotateAngleX = this.etfu$lerp(this.etfu$swimAnimation, this.bipedLeftLeg.rotateAngleX,
				0.3F * MathHelper.cos(limbSwing * 0.33333334F + (float) Math.PI));
		this.bipedRightLeg.rotateAngleX = this.etfu$lerp(this.etfu$swimAnimation, this.bipedRightLeg.rotateAngleX,
				0.3F * MathHelper.cos(limbSwing * 0.33333334F));
	}

	@Override
	public void setLivingAnimations(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks) {
		super.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTicks);
		this.etfu$swimAnimation = entity instanceof IPlayerSwimming
				? ((IPlayerSwimming) entity).etfu$getSwimAnimation(partialTicks) : 0.0F;
	}

	@Unique
	private float etfu$armAngle(float value) {
		return -65.0F * value + value * value;
	}

	@Unique
	private float etfu$lerp(float amount, float start, float end) {
		return start + amount * (end - start);
	}

	@Unique
	private float etfu$rotLerp(float amount, float start, float end) {
		float delta = (end - start) % ((float) Math.PI * 2.0F);
		if (delta < -(float) Math.PI) delta += (float) Math.PI * 2.0F;
		if (delta >= (float) Math.PI) delta -= (float) Math.PI * 2.0F;
		return start + amount * delta;
	}
}
