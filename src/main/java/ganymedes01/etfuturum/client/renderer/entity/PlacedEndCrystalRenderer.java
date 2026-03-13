package ganymedes01.etfuturum.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelEnderCrystal;
import net.minecraft.client.renderer.tileentity.RenderEnderCrystal;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import ganymedes01.etfuturum.client.OpenGLHelper;

public class PlacedEndCrystalRenderer extends RenderEnderCrystal {

    private static final ResourceLocation TEXTURE = new ResourceLocation(
        "textures/entity/endercrystal/endercrystal.png");
    private static final ModelBase MODEL = new ModelEnderCrystal(0.0F, false);

    @Override
    public void doRender(EntityEnderCrystal crystal, double x, double y, double z, float p_76986_8_,
        float partialTickTime) {
        float rotation = crystal.innerRotation + partialTickTime;
        OpenGLHelper.pushMatrix();
        OpenGLHelper.translate(x, y, z);
        bindTexture(TEXTURE);
        float f3 = MathHelper.sin(rotation * 0.2F) / 2.0F + 0.5F;
        f3 += f3 * f3;
        MODEL.render(crystal, 0.0F, rotation * 3.0F, f3 * 0.2F, 0.0F, 0.0F, 0.0625F);
        OpenGLHelper.popMatrix();
    }
}
