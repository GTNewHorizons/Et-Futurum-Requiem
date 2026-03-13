package ganymedes01.etfuturum.client.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

import ganymedes01.etfuturum.entities.EntityArmourStand;

public class ModelArmorStandArmor extends ModelBiped {

    public ModelArmorStandArmor() {
        this(0.0F);
    }

    public ModelArmorStandArmor(float p_i46307_1_) {
        this(p_i46307_1_, 64, 32);
    }

    protected ModelArmorStandArmor(float p_i46308_1_, int p_i46308_2_, int p_i46308_3_) {
        super(p_i46308_1_, 0.0F, p_i46308_2_, p_i46308_3_);
    }

    @Override
    public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_,
        float p_78087_5_, float p_78087_6_, Entity entity) {
        if (entity instanceof EntityArmourStand entityarmorstand) {
            bipedHead.rotateAngleX = 0.017453292F * entityarmorstand.getHeadRotation()
                .getX();
            bipedHead.rotateAngleY = 0.017453292F * entityarmorstand.getHeadRotation()
                .getY();
            bipedHead.rotateAngleZ = 0.017453292F * entityarmorstand.getHeadRotation()
                .getZ();
            bipedHead.setRotationPoint(0.0F, 1.0F, 0.0F);
            bipedBody.rotateAngleX = 0.017453292F * entityarmorstand.getBodyRotation()
                .getX();
            bipedBody.rotateAngleY = 0.017453292F * entityarmorstand.getBodyRotation()
                .getY();
            bipedBody.rotateAngleZ = 0.017453292F * entityarmorstand.getBodyRotation()
                .getZ();
            bipedLeftArm.rotateAngleX = 0.017453292F * entityarmorstand.getLeftArmRotation()
                .getX();
            bipedLeftArm.rotateAngleY = 0.017453292F * entityarmorstand.getLeftArmRotation()
                .getY();
            bipedLeftArm.rotateAngleZ = 0.017453292F * entityarmorstand.getLeftArmRotation()
                .getZ();
            bipedRightArm.rotateAngleX = 0.017453292F * entityarmorstand.getRightArmRotation()
                .getX();
            bipedRightArm.rotateAngleY = 0.017453292F * entityarmorstand.getRightArmRotation()
                .getY();
            bipedRightArm.rotateAngleZ = 0.017453292F * entityarmorstand.getRightArmRotation()
                .getZ();
            bipedLeftLeg.rotateAngleX = 0.017453292F * entityarmorstand.getLeftLegRotation()
                .getX();
            bipedLeftLeg.rotateAngleY = 0.017453292F * entityarmorstand.getLeftLegRotation()
                .getY();
            bipedLeftLeg.rotateAngleZ = 0.017453292F * entityarmorstand.getLeftLegRotation()
                .getZ();
            bipedLeftLeg.setRotationPoint(1.9F, 11.0F, 0.0F);
            bipedRightLeg.rotateAngleX = 0.017453292F * entityarmorstand.getRightLegRotation()
                .getX();
            bipedRightLeg.rotateAngleY = 0.017453292F * entityarmorstand.getRightLegRotation()
                .getY();
            bipedRightLeg.rotateAngleZ = 0.017453292F * entityarmorstand.getRightLegRotation()
                .getZ();
            bipedRightLeg.setRotationPoint(-1.9F, 11.0F, 0.0F);
            copyModelAngles(bipedHead, bipedHeadwear);
        }
    }

    public static void copyModelAngles(ModelRenderer source, ModelRenderer dest) {
        dest.rotateAngleX = source.rotateAngleX;
        dest.rotateAngleY = source.rotateAngleY;
        dest.rotateAngleZ = source.rotateAngleZ;
        dest.rotationPointX = source.rotationPointX;
        dest.rotationPointY = source.rotationPointY;
        dest.rotationPointZ = source.rotationPointZ;
    }
}
