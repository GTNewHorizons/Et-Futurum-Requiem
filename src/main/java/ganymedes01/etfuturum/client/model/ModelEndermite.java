package ganymedes01.etfuturum.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelEndermite extends ModelBase {

    private static final int[][] BODY_SIZES = new int[][] { { 4, 3, 2 }, { 6, 4, 5 }, { 3, 3, 1 }, { 1, 2, 1 } };
    private static final int[][] BODY_TEXS = new int[][] { { 0, 0 }, { 0, 5 }, { 0, 14 }, { 0, 18 } };
    private static final int BODY_COUNT = BODY_SIZES.length;
    private final ModelRenderer[] bodyParts;

    public ModelEndermite() {
        bodyParts = new ModelRenderer[BODY_COUNT];
        float f = -3.5F;

        for (int i = 0; i < bodyParts.length; ++i) {
            bodyParts[i] = new ModelRenderer(this, BODY_TEXS[i][0], BODY_TEXS[i][1]);
            bodyParts[i].addBox(
                BODY_SIZES[i][0] * -0.5F,
                0.0F,
                BODY_SIZES[i][2] * -0.5F,
                BODY_SIZES[i][0],
                BODY_SIZES[i][1],
                BODY_SIZES[i][2]);
            bodyParts[i].setRotationPoint(0.0F, 24 - BODY_SIZES[i][1], f);

            if (i < bodyParts.length - 1) f += (BODY_SIZES[i][2] + BODY_SIZES[i + 1][2]) * 0.5F;
        }
    }

    @Override
    public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_,
        float p_78088_6_, float p_78088_7_) {
        setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_, p_78088_1_);

        for (int i = 0; i < bodyParts.length; ++i) bodyParts[i].render(p_78088_7_);
    }

    @Override
    public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_,
        float p_78087_5_, float p_78087_6_, Entity p_78087_7_) {
        for (int i = 0; i < bodyParts.length; ++i) {
            bodyParts[i].rotateAngleY = MathHelper.cos(p_78087_3_ * 0.9F + i * 0.15F * (float) Math.PI)
                * (float) Math.PI
                * 0.01F
                * (1 + Math.abs(i - 2));
            bodyParts[i].rotationPointX = MathHelper.sin(p_78087_3_ * 0.9F + i * 0.15F * (float) Math.PI)
                * (float) Math.PI
                * 0.1F
                * Math.abs(i - 2);
        }
    }
}
