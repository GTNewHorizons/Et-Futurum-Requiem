package ganymedes01.etfuturum.client.model;

import ganymedes01.etfuturum.entities.EntityPanda;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class ModelPanda extends ModelBase {

	public final ModelRenderer head;
	public final ModelRenderer body;
	public final ModelRenderer rightHindLeg;
	public final ModelRenderer leftHindLeg;
	public final ModelRenderer rightFrontLeg;
	public final ModelRenderer leftFrontLeg;
	private float sittingAnimationProgress;
	private float onBackAnimationProgress;
	private float rollAnimationProgress;
	private boolean eating;
	private boolean unhappy;
	private boolean scared;
	private boolean sneezing;
	private int sneezeTicks;

	public ModelPanda() {
		textureWidth = 64;
		textureHeight = 64;

		head = new ModelRenderer(this, 0, 6);
		head.addBox(-6.5F, -5.0F, -4.0F, 13, 10, 9);
		head.setRotationPoint(0.0F, 11.5F, -17.0F);
		head.setTextureOffset(45, 16).addBox(-3.5F, 0.0F, -6.0F, 7, 5, 2);
		head.setTextureOffset(52, 25).addBox(-8.5F, -8.0F, -1.0F, 5, 4, 1);
		head.setTextureOffset(52, 25).addBox(3.5F, -8.0F, -1.0F, 5, 4, 1);

		body = new ModelRenderer(this, 0, 25);
		body.addBox(-9.5F, -13.0F, -6.5F, 19, 26, 13);
		body.setRotationPoint(0.0F, 10.0F, 0.0F);
		body.rotateAngleX = (float) Math.PI / 2.0F;

		rightHindLeg = createLeg(-5.5F, 15.0F, 9.0F);
		leftHindLeg = createLeg(5.5F, 15.0F, 9.0F);
		rightFrontLeg = createLeg(-5.5F, 15.0F, -9.0F);
		leftFrontLeg = createLeg(5.5F, 15.0F, -9.0F);
	}

	private ModelRenderer createLeg(float x, float y, float z) {
		ModelRenderer leg = new ModelRenderer(this, 40, 0);
		leg.addBox(-3.0F, 0.0F, -3.0F, 6, 9, 6);
		leg.setRotationPoint(x, y, z);
		return leg;
	}

	@Override
	public void setLivingAnimations(EntityLivingBase entity, float limbSwing, float limbSwingAmount,
			float partialTicks) {
		super.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTicks);

		if (entity instanceof EntityPanda) {
			EntityPanda panda = (EntityPanda) entity;
			sittingAnimationProgress = clampAnimationProgress(panda.getSittingAnimationProgress(partialTicks));
			onBackAnimationProgress = clampAnimationProgress(panda.getOnBackAnimationProgress(partialTicks));
			rollAnimationProgress = clampAnimationProgress(panda.getRollAnimationProgress(partialTicks));
			eating = panda.isEating();
			unhappy = panda.isUnhappy();
			scared = panda.isScaredByThunderstorm();
			sneezing = panda.isSneezing();
			sneezeTicks = panda.getSneezeTicks();
		} else {
			sittingAnimationProgress = 0.0F;
			onBackAnimationProgress = 0.0F;
			rollAnimationProgress = 0.0F;
			eating = false;
			unhappy = false;
			scared = false;
			sneezing = false;
			sneezeTicks = 0;
		}
	}

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch, float scale) {
		setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);

		if (isChild) {
			GL11.glPushMatrix();
			GL11.glScalef(0.5555556F, 0.5555556F, 0.5555556F);
			GL11.glTranslatef(0.0F, 23.0F * scale, 4.8F * scale);
			head.render(scale);
			GL11.glPopMatrix();

			GL11.glPushMatrix();
			GL11.glScalef(1.0F / 3.0F, 1.0F / 3.0F, 1.0F / 3.0F);
			GL11.glTranslatef(0.0F, 49.0F * scale, 0.0F);
			renderBody(scale);
			GL11.glPopMatrix();
		} else {
			head.render(scale);
			renderBody(scale);
		}
	}

	private void renderBody(float scale) {
		body.render(scale);
		rightHindLeg.render(scale);
		leftHindLeg.render(scale);
		rightFrontLeg.render(scale);
		leftFrontLeg.render(scale);
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch, float scale, Entity entity) {
		head.rotateAngleX = headPitch * (float) Math.PI / 180.0F;
		head.rotateAngleY = netHeadYaw * (float) Math.PI / 180.0F;
		head.rotateAngleZ = 0.0F;

		body.rotateAngleX = (float) Math.PI / 2.0F;
		body.rotateAngleY = 0.0F;
		body.rotateAngleZ = 0.0F;

		rightHindLeg.rotateAngleY = 0.0F;
		rightHindLeg.rotateAngleZ = 0.0F;
		leftHindLeg.rotateAngleY = 0.0F;
		leftHindLeg.rotateAngleZ = 0.0F;
		rightFrontLeg.rotateAngleY = 0.0F;
		rightFrontLeg.rotateAngleZ = 0.0F;
		leftFrontLeg.rotateAngleY = 0.0F;
		leftFrontLeg.rotateAngleZ = 0.0F;

		float stride = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		float oppositeStride = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F
				* limbSwingAmount;
		rightHindLeg.rotateAngleX = stride;
		leftHindLeg.rotateAngleX = oppositeStride;
		rightFrontLeg.rotateAngleX = oppositeStride;
		leftFrontLeg.rotateAngleX = stride;

		if (unhappy) {
			float headShake = 0.35F * MathHelper.sin(ageInTicks * 0.6F);
			float pawShake = 0.75F * MathHelper.sin(ageInTicks * 0.3F);
			head.rotateAngleY = headShake;
			head.rotateAngleZ = headShake;
			rightFrontLeg.rotateAngleX = -pawShake;
			leftFrontLeg.rotateAngleX = pawShake;
		}

		if (sneezing) {
			if (sneezeTicks < 15) {
				head.rotateAngleX = -(float) Math.PI / 4.0F * sneezeTicks / 14.0F;
			} else if (sneezeTicks < 20) {
				float recovery = (sneezeTicks - 15) / 5.0F;
				head.rotateAngleX = -(float) Math.PI / 4.0F + (float) Math.PI / 4.0F * recovery;
			}
		}

		float sitting = sittingAnimationProgress;
		if (sitting > 0.0F) {
			body.rotateAngleX = lerp(sitting, body.rotateAngleX, 1.7407963F);
			head.rotateAngleX = lerp(sitting, head.rotateAngleX, (float) Math.PI / 2.0F);

			rightFrontLeg.rotateAngleZ = -0.27079642F;
			leftFrontLeg.rotateAngleZ = 0.27079642F;
			rightHindLeg.rotateAngleZ = 0.5707964F;
			leftHindLeg.rotateAngleZ = -0.5707964F;

			if (eating) {
				float chew = MathHelper.sin(ageInTicks * 0.6F);
				head.rotateAngleX = (float) Math.PI / 2.0F + 0.2F * chew;
				rightFrontLeg.rotateAngleX = -0.4F - 0.2F * chew;
				leftFrontLeg.rotateAngleX = -0.4F - 0.2F * chew;
			}
			if (scared) {
				head.rotateAngleX = 2.1707964F;
				rightFrontLeg.rotateAngleX = -0.9F;
				leftFrontLeg.rotateAngleX = -0.9F;
			}
		}

		float onBack = onBackAnimationProgress;
		if (onBack > 0.0F) {
			float hindLegMovement = 0.6F * MathHelper.sin(ageInTicks * 0.15F);
			float frontLegMovement = 0.3F * MathHelper.sin(ageInTicks * 0.25F);
			rightHindLeg.rotateAngleX = -hindLegMovement;
			leftHindLeg.rotateAngleX = hindLegMovement;
			rightFrontLeg.rotateAngleX = frontLegMovement;
			leftFrontLeg.rotateAngleX = -frontLegMovement;
			head.rotateAngleX = lerp(onBack, head.rotateAngleX, (float) Math.PI / 2.0F);
		}

		float roll = rollAnimationProgress;
		if (roll > 0.0F) {
			float legMovement = 0.5F * MathHelper.sin(ageInTicks * 0.5F);
			rightHindLeg.rotateAngleX = -legMovement;
			leftHindLeg.rotateAngleX = legMovement;
			rightFrontLeg.rotateAngleX = legMovement;
			leftFrontLeg.rotateAngleX = -legMovement;
			head.rotateAngleX = lerp(roll, head.rotateAngleX, 2.0561945F);
		}
	}

	private static float lerp(float amount, float start, float end) {
		return start + amount * (end - start);
	}

	private static float clampAnimationProgress(float progress) {
		return MathHelper.clamp_float(progress, 0.0F, 1.0F);
	}
}
