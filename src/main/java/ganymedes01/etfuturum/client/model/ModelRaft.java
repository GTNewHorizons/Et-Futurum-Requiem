package ganymedes01.etfuturum.client.model;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelRaft extends ModelNewBoat {

    public ModelRaft() {
        super(new ModelRenderer[2], new ModelRenderer[2]);
    }

    @Override
    protected void setupModel() {
        this.boatSides[0] = (new ModelRenderer(this, 0, 0)).setTextureSize(128, 64);
        this.boatSides[1] = (new ModelRenderer(this, 0, 0)).setTextureSize(128, 64);

        this.boatSides[0].addBox(-14.0F, -11.0F, -4.0F, 28, 20, 4)
            .setRotationPoint(1.5708F, 0.0F, 0.0F);
        this.boatSides[1].addBox(-14.0F, -9.0F, -8.0F, 28, 16, 4)
            .setRotationPoint(1.5708F, 0.0F, 0.0F);
        boatSides[0].rotateAngleX = ((float) Math.PI / 2F);
        boatSides[0].offsetX = -0.09375F;
        boatSides[0].offsetY = -0.125F;
        boatSides[0].offsetZ = 0.09375F;
        boatSides[1].rotateAngleX = ((float) Math.PI / 2F);
        boatSides[1].offsetX = -0.09375F;
        boatSides[1].offsetY = -0.125F;
        boatSides[1].offsetZ = 0.09375F;
        this.paddles[0] = this.makePaddle(true);
        this.paddles[0].setRotationPoint(3.0F, -4.0F, 9.0F);
        this.paddles[1] = this.makePaddle(false);
        this.paddles[1].setRotationPoint(3.0F, -4.0F, 9.0F);
        this.paddles[1].rotateAngleY = (float) Math.PI;
        this.paddles[0].rotateAngleZ = this.paddles[1].rotateAngleZ = 0.19634955F;
        paddles[1].offsetZ = -1.0625F;
    }

    @Override
    public void renderMultipass(Entity p_187054_1_, float p_187054_2_, float p_187054_3_, float p_187054_4_,
        float p_187054_5_, float p_187054_6_, float scale) {}

    @Override
    ModelRenderer makePaddle(boolean p_187056_1_) {
        ModelRenderer modelrenderer = (new ModelRenderer(this, p_187056_1_ ? 0 : 40, 24)).setTextureSize(128, 64);
        modelrenderer.addBox(-1.0F, 0.0F, -5.0F, 2, 2, 18);
        modelrenderer.addBox(p_187056_1_ ? -1.001F : 0.01F, -3.0F, 8.0F, 1, 6, 7);
        return modelrenderer;
    }
}
