package ganymedes01.etfuturum.client.renderer.entity;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import ganymedes01.etfuturum.ModItems;
import ganymedes01.etfuturum.client.OpenGLHelper;
import ganymedes01.etfuturum.entities.EntityLingeringPotion;

public class LingeringPotionRenderer extends RenderSnowball {

    public LingeringPotionRenderer() {
        super(ModItems.LINGERING_POTION.get());
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float p_76986_8_, float p_76986_9_) {
        if (!(entity instanceof EntityLingeringPotion)) return;

        ItemStack stack = ((EntityLingeringPotion) entity).getStack();
        if (stack == null || stack.getItem() == null) return;

        int passes;
        if (stack.getItem()
            .requiresMultipleRenderPasses())
            passes = stack.getItem()
                .getRenderPasses(0);
        else passes = 1;

        OpenGLHelper.pushMatrix();
        OpenGLHelper.translate(x, y, z);
        OpenGLHelper.enableRescaleNormal();
        OpenGLHelper.scale(0.5F, 0.5F, 0.5F);
        bindEntityTexture(entity);

        for (int pass = 0; pass < passes; pass++) {
            IIcon icon = stack.getItem()
                .getIcon(stack, pass);

            if (icon != null) {
                OpenGLHelper.pushMatrix();
                OpenGLHelper.colour(
                    stack.getItem()
                        .getColorFromItemStack(stack, pass));
                renderIcon(icon);
                OpenGLHelper.popMatrix();
            }
        }

        OpenGLHelper.colour(1, 1, 1);
        OpenGLHelper.disableRescaleNormal();
        OpenGLHelper.popMatrix();
    }

    private void renderIcon(IIcon icon) {
        Tessellator tessellator = Tessellator.instance;

        float minU = icon.getMinU();
        float maxU = icon.getMaxU();
        float minV = icon.getMinV();
        float maxV = icon.getMaxV();

        OpenGLHelper.rotate(180.0F - renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        OpenGLHelper.rotate(-renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        tessellator.addVertexWithUV(-0.5F, -0.25F, 0.0D, minU, maxV);
        tessellator.addVertexWithUV(0.5F, -0.25F, 0.0D, maxU, maxV);
        tessellator.addVertexWithUV(0.5F, 0.75F, 0.0D, maxU, minV);
        tessellator.addVertexWithUV(-0.5F, 0.75F, 0.0D, minU, minV);
        tessellator.draw();
    }
}
