package ganymedes01.etfuturum.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelHead extends ModelBase {

    private final ModelRenderer head;
    private final ModelRenderer overlay;

    public ModelHead() {
        this(32);
    }

    public ModelHead(int height) {
        textureWidth = 64;
        textureHeight = height;
        head = new ModelRenderer(this, 0, 0);
        overlay = new ModelRenderer(this, 32, 0);
        head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
        head.setRotationPoint(0.0F, 0.0F, 0.0F);
        overlay.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.5F);
        overlay.setRotationPoint(0.0F, 0.0F, 0.0F);
    }

    public void render(float rotationY) {
        render(0.0F, rotationY);
    }

    public void render(float rotationX, float rotationY) {
        head.rotateAngleX = rotationX / (180F / (float) Math.PI);
        head.rotateAngleY = rotationY / (180F / (float) Math.PI);
        overlay.rotateAngleY = head.rotateAngleY;
        overlay.rotateAngleX = head.rotateAngleX;

        head.render(0.0625F);
        overlay.render(0.0625F);
    }
}
