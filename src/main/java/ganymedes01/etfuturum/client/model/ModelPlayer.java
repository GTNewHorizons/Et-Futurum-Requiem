package ganymedes01.etfuturum.client.model;

import ganymedes01.etfuturum.client.skins.DelegatedModelBiped;
import ganymedes01.etfuturum.client.skins.PlayerModelManager;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

import java.util.HashSet;
import java.util.Set;

public class ModelPlayer extends ModelBiped {

	/*
	 * This class, with permission, is provided by SkinPort, thanks to LainMI
	 * https://www.curseforge.com/minecraft/mc-mods/skinport
	 * Permission: https://www.curseforge.com/minecraft/mc-mods/skinport?comment=142
	 */

	public ModelRenderer bipedLeftArmwear;
	public ModelRenderer bipedRightArmwear;
	public ModelRenderer bipedLeftLegwear;
	public ModelRenderer bipedRightLegwear;
	public ModelRenderer bipedBodyWear;
	public boolean smallArms;

	private Set<DelegatedModelBiped> delegates = new HashSet<>();

	public ModelPlayer(float z, boolean smallArms) {
		super(z, 0.0F, 64, 64);

		this.smallArms = smallArms;

		bipedCloak = new ModelRenderer(this, 0, 0);
		bipedCloak.setTextureSize(64, 32);
		bipedCloak.addBox(-5.0F, 0.0F, -1.0F, 10, 16, 1, z);

		if (smallArms) {
			bipedLeftArm = new ModelRenderer(this, 32, 48);
			bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, z);
			bipedLeftArm.setRotationPoint(5.0F, 2.5F, 0.0F);

			bipedRightArm = new ModelRenderer(this, 40, 16);
			bipedRightArm.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, z);
			bipedRightArm.setRotationPoint(-5.0F, 2.5F, 0.0F);

			bipedLeftArmwear = new ModelRenderer(this, 48, 48);
			bipedLeftArmwear.addBox(-1.0F, -2.0F, -2.0F, 3, 12, 4, z + 0.25F);
			bipedLeftArm.addChild(bipedLeftArmwear);

			bipedRightArmwear = new ModelRenderer(this, 40, 32);
			bipedRightArmwear.addBox(-2.0F, -2.0F, -2.0F, 3, 12, 4, z + 0.25F);
			bipedRightArm.addChild(bipedRightArmwear);
		} else {
			bipedLeftArm = new ModelRenderer(this, 32, 48);
			bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, z);
			bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);

			bipedLeftArmwear = new ModelRenderer(this, 48, 48);
			bipedLeftArmwear.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, z + 0.25F);
			bipedLeftArm.addChild(bipedLeftArmwear);

			bipedRightArmwear = new ModelRenderer(this, 40, 32);
			bipedRightArmwear.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, z + 0.25F);
			bipedRightArm.addChild(bipedRightArmwear);
		}

		bipedLeftLeg = new ModelRenderer(this, 16, 48);
		bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, z);
		bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);

		bipedLeftLegwear = new ModelRenderer(this, 0, 48);
		bipedLeftLegwear.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, z + 0.25F);
		bipedLeftLeg.addChild(bipedLeftLegwear);

		bipedRightLegwear = new ModelRenderer(this, 0, 32);
		bipedRightLegwear.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, z + 0.25F);
		bipedRightLeg.addChild(bipedRightLegwear);

		bipedBodyWear = new ModelRenderer(this, 16, 32);
		bipedBodyWear.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, z + 0.25F);
		bipedBody.addChild(bipedBodyWear);
	}

	public void regenerateDelegates() {
		this.delegates = PlayerModelManager.constructDelegatesFor(this);
	}

	public int getDelegatesCount() {
		return delegates.size();
	}

	@Override
	public void render(Entity p_render_1_, float p_render_2_, float p_render_3_, float p_render_4_, float p_render_5_, float p_render_6_, float p_render_7_) {
		this.setRotationAngles(p_render_2_, p_render_3_, p_render_4_, p_render_5_, p_render_6_, p_render_7_, p_render_1_);
		this.delegates.forEach(delegate -> delegate.preRender(p_render_1_, p_render_2_, p_render_3_, p_render_4_, p_render_5_, p_render_6_, p_render_7_));

		// code stolen from original ModelBiped so that delegation can occur AFTER setRotationAngles but before body rendering
		// alternative implementation is to add preRender delegate after setRotationAngles, but in the event that it's used elsewhere for some reason
		// this approach is more "compatible"
		if (this.isChild) {
			float f6 = 2.0F;
			GL11.glPushMatrix();
			GL11.glScalef(1.5F / f6, 1.5F / f6, 1.5F / f6);
			GL11.glTranslatef(0.0F, 16.0F * p_render_7_, 0.0F);
			this.bipedHead.render(p_render_7_);
			GL11.glPopMatrix();
			GL11.glPushMatrix();
			GL11.glScalef(1.0F / f6, 1.0F / f6, 1.0F / f6);
			GL11.glTranslatef(0.0F, 24.0F * p_render_7_, 0.0F);
			this.bipedBody.render(p_render_7_);
			this.bipedRightArm.render(p_render_7_);
			this.bipedLeftArm.render(p_render_7_);
			this.bipedRightLeg.render(p_render_7_);
			this.bipedLeftLeg.render(p_render_7_);
			this.bipedHeadwear.render(p_render_7_);
			GL11.glPopMatrix();
		} else {
			this.bipedHead.render(p_render_7_);
			this.bipedBody.render(p_render_7_);
			this.bipedRightArm.render(p_render_7_);
			this.bipedLeftArm.render(p_render_7_);
			this.bipedRightLeg.render(p_render_7_);
			this.bipedLeftLeg.render(p_render_7_);
			this.bipedHeadwear.render(p_render_7_);
		}
		// end stolen code
		this.delegates.forEach(delegate -> delegate.postRender(p_render_1_, p_render_2_, p_render_3_, p_render_4_, p_render_5_, p_render_6_, p_render_7_));
	}

	@Override
	public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_) {
		this.delegates.forEach(delegate -> delegate.preSetRotationAngles(p_78087_1_, p_78087_2_, p_78087_3_, p_78087_4_, p_78087_5_, p_78087_6_, p_78087_7_));
		super.setRotationAngles(p_78087_1_, p_78087_2_, p_78087_3_, p_78087_4_, p_78087_5_, p_78087_6_, p_78087_7_);
		this.delegates.forEach(delegate -> delegate.postSetRotationAngles(p_78087_1_, p_78087_2_, p_78087_3_, p_78087_4_, p_78087_5_, p_78087_6_, p_78087_7_));
	}

	@Override
	public void renderEars(float p_78110_1_) {
		this.delegates.forEach(delegate -> delegate.preRenderEars(p_78110_1_));
		super.renderEars(p_78110_1_);
		this.delegates.forEach(delegate -> delegate.postRenderEars(p_78110_1_));
	}

	@Override
	public void renderCloak(float p_78111_1_) {
		this.delegates.forEach(delegate -> delegate.preRenderCloak(p_78111_1_));
		super.renderCloak(p_78111_1_);
		this.delegates.forEach(delegate -> delegate.postRenderCloak(p_78111_1_));
	}
}