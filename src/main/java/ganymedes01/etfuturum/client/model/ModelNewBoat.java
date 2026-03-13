package ganymedes01.etfuturum.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

import org.lwjgl.opengl.GL11;

import ganymedes01.etfuturum.entities.EntityNewBoat;

public class ModelNewBoat extends ModelBase {

    public final ModelRenderer[] boatSides;
    public final ModelRenderer[] paddles;

    /**
     * Part of the model rendered to make it seem like there's no water in the boat
     */
    public ModelRenderer noWater;

    public ModelNewBoat() {
        this(new ModelRenderer[5], new ModelRenderer[2]);
    }

    public ModelNewBoat(ModelRenderer[] boatSides, ModelRenderer[] paddles) {
        this.boatSides = boatSides;
        this.paddles = paddles;
        setupModel();
    }

    protected void setupModel() {
        this.boatSides[0] = (new ModelRenderer(this, 0, 0)).setTextureSize(128, 64);
        this.boatSides[1] = (new ModelRenderer(this, 0, 19)).setTextureSize(128, 64);
        this.boatSides[2] = (new ModelRenderer(this, 0, 27)).setTextureSize(128, 64);
        this.boatSides[3] = (new ModelRenderer(this, 0, 35)).setTextureSize(128, 64);
        this.boatSides[4] = (new ModelRenderer(this, 0, 43)).setTextureSize(128, 64);
        int i = 32;
        int j = 6;
        int k = 20;
        int l = 4;
        int i1 = 28;
        this.boatSides[0].addBox((float) (-i1 / 2), (float) (-k / 2 + 1), -3.0F, i1, k - 4, 3, 0.0F);
        this.boatSides[0].setRotationPoint(0.0F, (float) (l - 1), 1.0F);
        this.boatSides[1].addBox((float) (-i / 2 + 3), (float) (-j - 1), -1.0F, k - 2, j, 2, 0.0F);
        this.boatSides[1].setRotationPoint((float) (-i / 2 + 1), (float) l, 4.0F);
        this.boatSides[2].addBox((float) (-i / 2 + 8), (float) (-j - 1), -1.0F, k - 4, j, 2, 0.0F);
        this.boatSides[2].setRotationPoint((float) (i / 2 - 1), (float) l, 0.0F);
        this.boatSides[3].addBox((float) (-i / 2 + 2), (float) (-j - 1), -1.0F, i - 4, j, 2, 0.0F);
        this.boatSides[3].setRotationPoint(0.0F, (float) l, (float) (-k / 2 + 1));
        this.boatSides[4].addBox((float) (-i / 2 + 2), (float) (-j - 1), -1.0F, i - 4, j, 2, 0.0F);
        this.boatSides[4].setRotationPoint(0.0F, (float) l, (float) (k / 2 - 1));
        this.boatSides[0].rotateAngleX = ((float) Math.PI / 2F);
        this.boatSides[1].rotateAngleY = ((float) Math.PI * 3F / 2F);
        this.boatSides[2].rotateAngleY = ((float) Math.PI / 2F);
        this.boatSides[3].rotateAngleY = (float) Math.PI;
        this.paddles[0] = this.makePaddle(true);
        this.paddles[0].setRotationPoint(3.0F, -5.0F, 9.0F);
        this.paddles[1] = this.makePaddle(false);
        this.paddles[1].setRotationPoint(3.0F, -5.0F, -9.0F);
        this.paddles[1].rotateAngleY = (float) Math.PI;
        this.paddles[0].rotateAngleZ = this.paddles[1].rotateAngleZ = 0.19634955F;
        this.noWater = (new ModelRenderer(this, 0, 0)).setTextureSize(128, 64);
        this.noWater.addBox((float) (-i1 / 2), (float) (-k / 2 + 1), -3.0F, i1, k - 4, 3, 0.0F);
        this.noWater.setRotationPoint(0.0F, (float) (l - 7), 1.0F);
        this.noWater.rotateAngleX = ((float) Math.PI / 2F);
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
        float headPitch, float scale) {
        GL11.glPushMatrix();
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        EntityNewBoat entityboat = (EntityNewBoat) entityIn;
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);

        for (int i = 0; i < boatSides.length; ++i) {
            this.boatSides[i].render(scale);
        }

        this.renderPaddle(entityboat, 0, scale, limbSwing);
        this.renderPaddle(entityboat, 1, scale, limbSwing);
        GL11.glPopMatrix();
    }

    public void renderMultipass(Entity p_187054_1_, float p_187054_2_, float p_187054_3_, float p_187054_4_,
        float p_187054_5_, float p_187054_6_, float scale) {
        GL11.glPushMatrix();
        GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glColorMask(false, false, false, false);
        this.noWater.render(scale);
        GL11.glColorMask(true, true, true, true);
        GL11.glPopMatrix();
    }

    /**
     * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
     * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
     * "far" arms and legs can swing at most.
     */
    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
        float headPitch, float scaleFactor, Entity entityIn) {}

    ModelRenderer makePaddle(boolean p_187056_1_) {
        ModelRenderer modelrenderer = (new ModelRenderer(this, 62, p_187056_1_ ? 0 : 20)).setTextureSize(128, 64);
        int i = 20;
        int j = 7;
        int k = 6;
        float f = -5.0F;
        modelrenderer.addBox(-1.0F, 0.0F, f, 2, 2, i - 2);
        modelrenderer.addBox(p_187056_1_ ? -1.001F : 0.001F, (float) (-k / 2), (float) (i - j) + f, 1, k, j);
        return modelrenderer;
    }

    public void renderPaddle(EntityNewBoat boat, int paddle, float scale, float limbSwing) {
        float f = boat.getRowingTime(paddle, limbSwing);
        ModelRenderer modelrenderer = this.paddles[paddle];
        modelrenderer.rotateAngleX = (float) MathHelper
            .denormalizeClamp(-1.0471975803375244D, -0.2617993950843811D, (MathHelper.sin(-f) + 1.0F) / 2.0F);
        modelrenderer.rotateAngleY = (float) MathHelper
            .denormalizeClamp(-(Math.PI / 4D), (Math.PI / 4D), (MathHelper.sin(-f + 1.0F) + 1.0F) / 2.0F);

        if (paddle == 1) {
            modelrenderer.rotateAngleY = (float) Math.PI - modelrenderer.rotateAngleY;
        }

        modelrenderer.render(scale);
    }
}
