package ganymedes01.etfuturum.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class ModelPanda extends ModelBase {

	public final ModelRenderer head;
	public final ModelRenderer body;
	public final ModelRenderer rightHindLeg;
	public final ModelRenderer leftHindLeg;
	public final ModelRenderer rightFrontLeg;
	public final ModelRenderer leftFrontLeg;

	public ModelPanda() {
		textureWidth = 64;
		textureHeight = 64;

		head = new ModelRenderer(this, 0, 6);
		head.addBox(-6.5F, -5.0F, -4.0F, 13, 10, 9);
		head.setRotationPoint(0.0F, 11.5F, -17.0F);
		head.setTextureOffset(45, 16).addBox(-3.5F, 0.0F, -6.0F, 7, 5, 2);
		head.setTextureOffset(52, 25).addBox(-8.5F, -4.0F, -0.5F, 5, 5, 1);
		head.setTextureOffset(52, 25).addBox(3.5F, -4.0F, -0.5F, 5, 5, 1);

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
	public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch, float scale) {
		setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entity);

		if (isChild) {
			GL11.glPushMatrix();
			GL11.glScalef(0.75F, 0.75F, 0.75F);
			GL11.glTranslatef(0.0F, 8.0F * scale, 4.0F * scale);
			head.render(scale);
			GL11.glPopMatrix();

			GL11.glPushMatrix();
			GL11.glScalef(0.5F, 0.5F, 0.5F);
			GL11.glTranslatef(0.0F, 24.0F * scale, 0.0F);
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

		float stride = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
		float oppositeStride = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F
				* limbSwingAmount;
		rightHindLeg.rotateAngleX = stride;
		leftHindLeg.rotateAngleX = oppositeStride;
		rightFrontLeg.rotateAngleX = oppositeStride;
		leftFrontLeg.rotateAngleX = stride;
	}
}
