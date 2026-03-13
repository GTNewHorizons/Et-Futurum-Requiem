package ganymedes01.etfuturum.client.model;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelZombie;

public class ModelVillagerZombie extends ModelZombie {

    public ModelVillagerZombie(float size) {
        this(size, 64, 64);
    }

    public ModelVillagerZombie(float size, int width, int height) {
        super(size, 0, width, height);

        bipedHeadwear.isHidden = true;

        bipedHead = new ModelRenderer(this).setTextureSize(width, height);
        bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
        bipedHead.setTextureOffset(0, 0)
            .addBox(-4.0F, -10.0F, -4.0F, 8, 10, 8, size);

        ModelRenderer nose = new ModelRenderer(this).setTextureSize(width, height);
        nose.setRotationPoint(0.0F, -2.0F, 0.0F);
        nose.setTextureOffset(24, 0)
            .addBox(-1.0F, -1.0F, -6.0F, 2, 4, 2, size);
        bipedHead.addChild(nose);

        bipedBody = new ModelRenderer(this).setTextureSize(width, height);
        bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
        bipedBody.setTextureOffset(16, 20)
            .addBox(-4.0F, 0.0F, -3.0F, 8, 12, 6, size);
        bipedBody.setTextureOffset(0, 38)
            .addBox(-4.0F, 0.0F, -3.0F, 8, 18, 6, size + 0.5F);

        bipedRightArm = new ModelRenderer(this, 44, 38).setTextureSize(width, height);
        bipedRightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, size);
        bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);

        bipedLeftArm = new ModelRenderer(this, 44, 38).setTextureSize(width, height);
        bipedLeftArm.mirror = true;
        bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, size);
        bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);

        bipedRightLeg = new ModelRenderer(this, 0, 22).setTextureSize(width, height);
        bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
        bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, size);

        bipedLeftLeg = new ModelRenderer(this, 0, 22).setTextureSize(width, height);
        bipedLeftLeg.mirror = true;
        bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
        bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, size);
    }
}
