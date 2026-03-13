package ganymedes01.etfuturum.client.model;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

import ganymedes01.etfuturum.client.OpenGLHelper;
import ganymedes01.etfuturum.entities.EntityArmourStand;

public class ModelArmorStand extends ModelArmorStandArmor {

    public ModelRenderer standRightSide;
    public ModelRenderer standLeftSide;
    public ModelRenderer standWaist;
    public ModelRenderer standBase;

    public ModelArmorStand() {
        this(0.0F);
    }

    public ModelArmorStand(float size) {
        super(size, 64, 64);
        bipedHead = new ModelRenderer(this, 0, 0);
        bipedHead.addBox(-1.0F, -7.0F, -1.0F, 2, 7, 2, size);
        bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
        bipedBody = new ModelRenderer(this, 0, 26);
        bipedBody.addBox(-6.0F, 0.0F, -1.5F, 12, 3, 3, size);
        bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
        bipedRightArm = new ModelRenderer(this, 24, 0);
        bipedRightArm.addBox(-2.0F, -2.0F, -1.0F, 2, 12, 2, size);
        bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
        bipedLeftArm = new ModelRenderer(this, 32, 16);
        bipedLeftArm.mirror = true;
        bipedLeftArm.addBox(0.0F, -2.0F, -1.0F, 2, 12, 2, size);
        bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
        bipedRightLeg = new ModelRenderer(this, 8, 0);
        bipedRightLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 11, 2, size);
        bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
        bipedLeftLeg = new ModelRenderer(this, 40, 16);
        bipedLeftLeg.mirror = true;
        bipedLeftLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 11, 2, size);
        bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
        standRightSide = new ModelRenderer(this, 16, 0);
        standRightSide.addBox(-3.0F, 3.0F, -1.0F, 2, 7, 2, size);
        standRightSide.setRotationPoint(0.0F, 0.0F, 0.0F);
        standRightSide.showModel = true;
        standLeftSide = new ModelRenderer(this, 48, 16);
        standLeftSide.addBox(1.0F, 3.0F, -1.0F, 2, 7, 2, size);
        standLeftSide.setRotationPoint(0.0F, 0.0F, 0.0F);
        standWaist = new ModelRenderer(this, 0, 48);
        standWaist.addBox(-4.0F, 10.0F, -1.0F, 8, 2, 2, size);
        standWaist.setRotationPoint(0.0F, 0.0F, 0.0F);
        standBase = new ModelRenderer(this, 0, 32);
        standBase.addBox(-6.0F, 11.0F, -6.0F, 12, 1, 12, size);
        standBase.setRotationPoint(0.0F, 12.0F, 0.0F);
    }

    @Override
    public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_,
        float p_78087_5_, float p_78087_6_, Entity entity) {
        super.setRotationAngles(p_78087_1_, p_78087_2_, p_78087_3_, p_78087_4_, p_78087_5_, p_78087_6_, entity);

        if (entity instanceof EntityArmourStand stand) {
            bipedLeftArm.showModel = stand.getShowArms();
            bipedRightArm.showModel = stand.getShowArms();
            standBase.showModel = !stand.hasNoBasePlate();
            bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
            bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
            standRightSide.rotateAngleX = 0.017453292F * stand.getBodyRotation()
                .getX();
            standRightSide.rotateAngleY = 0.017453292F * stand.getBodyRotation()
                .getY();
            standRightSide.rotateAngleZ = 0.017453292F * stand.getBodyRotation()
                .getZ();
            standLeftSide.rotateAngleX = 0.017453292F * stand.getBodyRotation()
                .getX();
            standLeftSide.rotateAngleY = 0.017453292F * stand.getBodyRotation()
                .getY();
            standLeftSide.rotateAngleZ = 0.017453292F * stand.getBodyRotation()
                .getZ();
            standWaist.rotateAngleX = 0.017453292F * stand.getBodyRotation()
                .getX();
            standWaist.rotateAngleY = 0.017453292F * stand.getBodyRotation()
                .getY();
            standWaist.rotateAngleZ = 0.017453292F * stand.getBodyRotation()
                .getZ();
            standBase.rotateAngleX = 0.0F;
            standBase.rotateAngleY = 0.017453292F * -entity.rotationYaw;
            standBase.rotateAngleZ = 0.0F;
        }
    }

    @Override
    public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_,
        float p_78088_6_, float p_78088_7_) {
        super.render(p_78088_1_, p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_);
        OpenGLHelper.pushMatrix();

        if (isChild) {
            float f6 = 2.0F;
            OpenGLHelper.scale(1.0F / f6, 1.0F / f6, 1.0F / f6);
            OpenGLHelper.translate(0.0F, 24.0F * p_78088_7_, 0.0F);
            standRightSide.render(p_78088_7_);
            standLeftSide.render(p_78088_7_);
            standWaist.render(p_78088_7_);
            standBase.render(p_78088_7_);
        } else {
            if (p_78088_1_.isSneaking()) OpenGLHelper.translate(0.0F, 0.2F, 0.0F);

            standRightSide.render(p_78088_7_);
            standLeftSide.render(p_78088_7_);
            standWaist.render(p_78088_7_);
            standBase.render(p_78088_7_);
        }

        OpenGLHelper.popMatrix();
    }
}
