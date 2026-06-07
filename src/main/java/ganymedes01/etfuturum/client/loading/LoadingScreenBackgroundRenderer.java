package ganymedes01.etfuturum.client.loading;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;

public class LoadingScreenBackgroundRenderer {

    public void render(Minecraft mc, int width, int height) {
        renderDefaultBackground(mc, width, height);
    }

    private void renderDefaultBackground(Minecraft mc, int width, int height) {
        mc.getTextureManager().bindTexture(Gui.optionsBackground);
        drawTexturedQuad(width, height, 32.0F, 0.0F, 0xFF404040);
    }

    private void drawTexturedQuad(int width, int height, float scale, float verticalOffset, int color) {
        Tessellator tessellator = Tessellator.instance;
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_F(red, green, blue);
        tessellator.addVertexWithUV(0.0D, height, 0.0D, 0.0D, height / scale + verticalOffset);
        tessellator.addVertexWithUV(width, height, 0.0D, width / scale, height / scale + verticalOffset);
        tessellator.addVertexWithUV(width, 0.0D, 0.0D, width / scale, verticalOffset);
        tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, 0.0D, verticalOffset);
        tessellator.draw();
    }
}
