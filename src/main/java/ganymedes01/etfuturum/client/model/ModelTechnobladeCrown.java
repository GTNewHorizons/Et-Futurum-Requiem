package ganymedes01.etfuturum.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;

public class ModelTechnobladeCrown extends ModelBase {

    public ModelRenderer crown = new ModelRenderer(this, 0, 0);
    protected float childYOffset = 4.0F;
    protected float childZOffset = 4.0F;

    public ModelTechnobladeCrown() {
        this(6, 0);
    }

    public ModelTechnobladeCrown(int p_i1154_1_, float p_i1154_2_) {
        this.crown.addBox(-4.0F, -4.0F, -8.0F, 8, 8, 8, p_i1154_2_ + 0.5F);
        this.crown.setRotationPoint(0.0F, (float) (18 - p_i1154_1_), -6.0F);
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */
    @Override
    public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_,
        float p_78088_6_, float p_78088_7_) {
        this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_, p_78088_1_);
        if (this.isChild) {
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, this.childYOffset * p_78088_7_, this.childZOffset * p_78088_7_);
            this.crown.render(p_78088_7_);
            GL11.glPopMatrix();
        } else {
            this.crown.render(p_78088_7_);
        }
    }

    /**
     * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
     * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
     * "far" arms and legs can swing at most.
     */
    @Override
    public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_,
        float p_78087_5_, float p_78087_6_, Entity p_78087_7_) {
        this.crown.rotateAngleX = p_78087_5_ / (180F / (float) Math.PI);
        this.crown.rotateAngleY = p_78087_4_ / (180F / (float) Math.PI);
    }
}
