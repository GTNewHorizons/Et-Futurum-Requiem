package ganymedes01.etfuturum.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

import ganymedes01.etfuturum.client.OpenGLHelper;
import ganymedes01.etfuturum.entities.EntityBee;

public class ModelBee extends ModelBase {

    private final ModelRenderer body;
    private final ModelRenderer torso;
    private final ModelRenderer rightWing;
    private final ModelRenderer leftWing;
    private final ModelRenderer frontLegs;
    private final ModelRenderer middleLegs;
    private final ModelRenderer backLegs;
    private final ModelRenderer stinger;
    private final ModelRenderer leftAntenna;
    private final ModelRenderer rightAntenna;
    private float bodyPitch;

    public ModelBee() {
        // super(false, 24.0F, 0.0F);
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.body = new ModelRenderer(this);
        this.body.setRotationPoint(0.0F, 19.0F, 0.0F);
        this.torso = new ModelRenderer(this, 0, 0);
        this.torso.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.body.addChild(this.torso);
        this.torso.addBox(-3.5F, -4.0F, -5.0F, 7, 7, 10, 0.0F);
        this.stinger = new ModelRenderer(this, 26, 7);
        this.stinger.addBox(0.0F, -1.0F, 5.0F, 0, 1, 2, 0.0F);
        this.torso.addChild(this.stinger);
        this.leftAntenna = new ModelRenderer(this, 2, 0);
        this.leftAntenna.setRotationPoint(0.0F, -2.0F, -5.0F);
        this.leftAntenna.addBox(1.5F, -2.0F, -3.0F, 1, 2, 3, 0.0F);
        this.rightAntenna = new ModelRenderer(this, 2, 3);
        this.rightAntenna.setRotationPoint(0.0F, -2.0F, -5.0F);
        this.rightAntenna.addBox(-2.5F, -2.0F, -3.0F, 1, 2, 3, 0.0F);
        this.torso.addChild(this.leftAntenna);
        this.torso.addChild(this.rightAntenna);
        this.rightWing = new ModelRenderer(this, 0, 18);
        this.rightWing.setRotationPoint(-1.5F, -4.0F, -3.0F);
        this.rightWing.rotateAngleX = 0.0F;
        this.rightWing.rotateAngleY = -0.2618F;
        this.rightWing.rotateAngleZ = 0.0F;
        this.body.addChild(this.rightWing);
        this.rightWing.addBox(-9.0F, 0.0F, 0.0F, 9, 0, 6, 0.001F);
        this.leftWing = new ModelRenderer(this, 0, 18);
        this.leftWing.setRotationPoint(1.5F, -4.0F, -3.0F);
        this.leftWing.rotateAngleX = 0.0F;
        this.leftWing.rotateAngleY = 0.2618F;
        this.leftWing.rotateAngleZ = 0.0F;
        this.leftWing.mirror = true;
        this.body.addChild(this.leftWing);
        this.leftWing.addBox(0.0F, 0.0F, 0.0F, 9, 0, 6, 0.001F);
        this.frontLegs = new ModelRenderer(this, 26, 1);
        this.frontLegs.setRotationPoint(1.5F, 3.0F, -2.0F);
        this.body.addChild(this.frontLegs);
        this.frontLegs.addBox(/* "frontLegBox", */-5.0F, 0.0F, 0.0F, 7, 2, 0/* , 0.0F, 26, 1 */);
        this.middleLegs = new ModelRenderer(this, 26, 3);
        this.middleLegs.setRotationPoint(1.5F, 3.0F, 0.0F);
        this.body.addChild(this.middleLegs);
        this.middleLegs.addBox(/* "midLegBox", */-5.0F, 0.0F, 0.0F, 7, 2, 0/* , 0.0F, 26, 3 */);
        this.backLegs = new ModelRenderer(this, 26, 5);
        this.backLegs.setRotationPoint(1.5F, 3.0F, 2.0F);
        this.body.addChild(this.backLegs);
        this.backLegs.addBox(/* "backLegBox", */-5.0F, 0.0F, 0.0F, 7, 2, 0/* , 0.0F, 26, 5 */);
    }

    @Override
    public void setLivingAnimations(EntityLivingBase entityIn, float limbSwing, float limbSwingAmount,
        float partialTick) {
        setBeeLivingAnimations((EntityBee) entityIn, limbSwing, limbSwingAmount, partialTick);
    }

    public void setBeeLivingAnimations(EntityBee entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
        super.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTick);
        this.bodyPitch = entityIn.getBodyPitch(partialTick);
        this.stinger.showModel = !entityIn.hasStung();
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
        float headPitch, float p_78087_6_, Entity entityIn) {
        setBeeRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, (EntityBee) entityIn);
    }

    private void setBeeRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
        float headPitch, EntityBee entityIn) {
        this.rightWing.rotateAngleX = 0.0F;
        this.leftAntenna.rotateAngleX = 0.0F;
        this.rightAntenna.rotateAngleX = 0.0F;
        this.body.rotateAngleX = 0.0F;
        this.body.rotationPointY = 19.0F;
        boolean flag = entityIn.onGround && Math.abs(getMovementLengthSquared(entityIn)) < 0.05D;
        if (flag) {
            this.rightWing.rotateAngleY = -0.2618F;
            this.rightWing.rotateAngleZ = 0.0F;
            this.leftWing.rotateAngleX = 0.0F;
            this.leftWing.rotateAngleY = 0.2618F;
            this.leftWing.rotateAngleZ = 0.0F;
            this.frontLegs.rotateAngleX = 0.0F;
            this.middleLegs.rotateAngleX = 0.0F;
            this.backLegs.rotateAngleX = 0.0F;
        } else {
            float f = ageInTicks * 2.1F;
            this.rightWing.rotateAngleY = 0.0F;
            this.rightWing.rotateAngleZ = MathHelper.cos(f) * (float) Math.PI * 0.15F;
            this.leftWing.rotateAngleX = this.rightWing.rotateAngleX;
            this.leftWing.rotateAngleY = this.rightWing.rotateAngleY;
            this.leftWing.rotateAngleZ = -this.rightWing.rotateAngleZ;
            this.frontLegs.rotateAngleX = ((float) Math.PI / 4F);
            this.middleLegs.rotateAngleX = ((float) Math.PI / 4F);
            this.backLegs.rotateAngleX = ((float) Math.PI / 4F);
            this.body.rotateAngleX = 0.0F;
            this.body.rotateAngleY = 0.0F;
            this.body.rotateAngleZ = 0.0F;
        }

        if (!entityIn.isAngry()) {
            this.body.rotateAngleX = 0.0F;
            this.body.rotateAngleY = 0.0F;
            this.body.rotateAngleZ = 0.0F;
            if (!flag) {
                float f1 = MathHelper.cos(ageInTicks * 0.18F);
                this.body.rotateAngleX = 0.1F + f1 * (float) Math.PI * 0.025F;
                this.leftAntenna.rotateAngleX = f1 * (float) Math.PI * 0.03F;
                this.rightAntenna.rotateAngleX = f1 * (float) Math.PI * 0.03F;
                this.frontLegs.rotateAngleX = -f1 * (float) Math.PI * 0.1F + ((float) Math.PI / 8F);
                this.backLegs.rotateAngleX = -f1 * (float) Math.PI * 0.05F + ((float) Math.PI / 4F);
                this.body.rotationPointY = 19.0F - MathHelper.cos(ageInTicks * 0.18F) * 0.9F;
            }
        }

        if (this.bodyPitch > 0.0F) {
            this.body.rotateAngleX = func_228283_a_(this.body.rotateAngleX, 3.0915928F, this.bodyPitch);
        }
    }

    public static float func_228283_a_(float p_228283_0_, float p_228283_1_, float p_228283_2_) {
        float f = p_228283_1_ - p_228283_0_;
        while (f < -(float) Math.PI) {
            f += ((float) Math.PI * 2F);
        }

        while (f >= (float) Math.PI) {
            f -= ((float) Math.PI * 2F);
        }

        return p_228283_0_ + p_228283_2_ * f;
    }

    public double getMovementLengthSquared(EntityBee entity) {
        return entity.motionX * entity.motionX + entity.motionY * entity.motionY + entity.motionZ * entity.motionZ;
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
        float headPitch, float scale) {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
        if (isChild) {
            OpenGLHelper.scale(0.5f, 0.5f, 0.5f);
            OpenGLHelper.translate(0.0f, 1.5f, 0.0f);
        }
        this.body.render(scale);
        if (isChild) {
            OpenGLHelper.scale(1.0F, 1.0F, 1.0F);
            OpenGLHelper.translate(1.0F, 1.0F, 1.0F);
        }
    }
}
