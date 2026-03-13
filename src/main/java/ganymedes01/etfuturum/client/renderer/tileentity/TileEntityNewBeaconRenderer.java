package ganymedes01.etfuturum.client.renderer.tileentity;

import java.util.List;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import ganymedes01.etfuturum.client.OpenGLHelper;
import ganymedes01.etfuturum.tileentities.TileEntityNewBeacon;
import ganymedes01.etfuturum.tileentities.TileEntityNewBeacon.BeamSegment;

public class TileEntityNewBeaconRenderer extends TileEntitySpecialRenderer {

    private static final ResourceLocation BEAM_TEXTURE = new ResourceLocation("textures/entity/beacon_beam.png");

    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTickTime) {
        TileEntityNewBeacon beacon = (TileEntityNewBeacon) tile;

        float f1 = beacon.func_146002_i(); // shouldBeamRender
        OpenGLHelper.alphaFunc(GL11.GL_GREATER, 0.1F);

        if (f1 > 0.0F) {
            List<BeamSegment> list = beacon.getSegments();
            int j = 0;

            for (int i = 0; i < list.size(); i++) {
                BeamSegment beamsegment = list.get(i);
                int l = j + beamsegment.getHeight();
                bindTexture(BEAM_TEXTURE);
                renderBeamSegment(
                    x,
                    y,
                    z,
                    partialTickTime,
                    f1,
                    (double) tile.getWorldObj()
                        .getTotalWorldTime(),
                    j,
                    beamsegment.getHeight(),
                    beamsegment.getColor());
                j = l;
            }
        }
    }

    public static void renderBeamSegment(double x, double y, double z, double partialTickTime, double shouldRender,
        double worldTime, int yOffset, int partialHeight, float[] color) {
        renderBeamSegment(
            x,
            y,
            z,
            partialTickTime,
            shouldRender,
            worldTime,
            yOffset,
            partialHeight,
            color,
            0.2D,
            0.25D);
    }

    public static void renderBeamSegment(double x, double y, double z, double partialTickTime, double shouldRender,
        double worldTime, int partialHeight, int height, float[] color, double p_188205_15_, double p_188205_17_) {
        Tessellator tessellator = Tessellator.instance;
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, 10497.0F);
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, 10497.0F);
        OpenGLHelper.disableLighting();
        OpenGLHelper.disableCull();
        OpenGLHelper.disableBlend();
        OpenGLHelper.depthMask(true);
        OpenGlHelper.glBlendFunc(770, 1, 1, 0);
        int i = partialHeight + height;
        double d0 = worldTime + partialTickTime;
        double d1 = height < 0 ? d0 : -d0;
        double frac = d1 * 0.2D - (double) MathHelper.floor_double(d1 * 0.1D);
        double d2 = frac - MathHelper.floor_double(frac);
        float f = color[0];
        float f1 = color[1];
        float f2 = color[2];
        double d3 = d0 * 0.025D * -1.5D;
        double d4 = 0.5D + Math.cos(d3 + 2.356194490192345D) * p_188205_15_;
        double d5 = 0.5D + Math.sin(d3 + 2.356194490192345D) * p_188205_15_;
        double d6 = 0.5D + Math.cos(d3 + (Math.PI / 4D)) * p_188205_15_;
        double d7 = 0.5D + Math.sin(d3 + (Math.PI / 4D)) * p_188205_15_;
        double d8 = 0.5D + Math.cos(d3 + 3.9269908169872414D) * p_188205_15_;
        double d9 = 0.5D + Math.sin(d3 + 3.9269908169872414D) * p_188205_15_;
        double d10 = 0.5D + Math.cos(d3 + 5.497787143782138D) * p_188205_15_;
        double d11 = 0.5D + Math.sin(d3 + 5.497787143782138D) * p_188205_15_;
        double d13 = 1.0D;
        double d14 = -1.0D + d2;
        double d15 = (double) height * shouldRender * (0.5D / p_188205_15_) + d14;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(f, f1, f2, 1.0F);
        tessellator.addVertexWithUV(x + d4, y + (double) i, z + d5, 1.0D, d15);
        tessellator.addVertexWithUV(x + d4, y + (double) partialHeight, z + d5, 1.0D, d14);
        tessellator.addVertexWithUV(x + d6, y + (double) partialHeight, z + d7, 0.0D, d14);
        tessellator.addVertexWithUV(x + d6, y + (double) i, z + d7, 0.0D, d15);
        tessellator.addVertexWithUV(x + d10, y + (double) i, z + d11, 1.0D, d15);
        tessellator.addVertexWithUV(x + d10, y + (double) partialHeight, z + d11, 1.0D, d14);
        tessellator.addVertexWithUV(x + d8, y + (double) partialHeight, z + d9, 0.0D, d14);
        tessellator.addVertexWithUV(x + d8, y + (double) i, z + d9, 0.0D, d15);
        tessellator.addVertexWithUV(x + d6, y + (double) i, z + d7, 1.0D, d15);
        tessellator.addVertexWithUV(x + d6, y + (double) partialHeight, z + d7, 1.0D, d14);
        tessellator.addVertexWithUV(x + d10, y + (double) partialHeight, z + d11, 0.0D, d14);
        tessellator.addVertexWithUV(x + d10, y + (double) i, z + d11, 0.0D, d15);
        tessellator.addVertexWithUV(x + d8, y + (double) i, z + d9, 1.0D, d15);
        tessellator.addVertexWithUV(x + d8, y + (double) partialHeight, z + d9, 1.0D, d14);
        tessellator.addVertexWithUV(x + d4, y + (double) partialHeight, z + d5, 0.0D, d14);
        tessellator.addVertexWithUV(x + d4, y + (double) i, z + d5, 0.0D, d15);
        tessellator.draw();
        OpenGLHelper.enableBlend();
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        OpenGLHelper.depthMask(false);
        tessellator.setColorRGBA_F(color[0], color[1], color[2], 0.125F);
        d3 = 0.5D - p_188205_17_;
        d4 = 0.5D - p_188205_17_;
        d5 = 0.5D + p_188205_17_;
        d6 = 0.5D - p_188205_17_;
        d7 = 0.5D - p_188205_17_;
        d8 = 0.5D + p_188205_17_;
        d9 = 0.5D + p_188205_17_;
        d10 = 0.5D + p_188205_17_;
        d11 = 0.0D;
        d13 = -1.0D + d2;
        d14 = (double) height * shouldRender + d13;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(f, f1, f2, 0.125F);
        tessellator.addVertexWithUV(x + d3, y + (double) i, z + d4, 1.0D, d14);
        tessellator.addVertexWithUV(x + d3, y + (double) partialHeight, z + d4, 1.0D, d13);
        tessellator.addVertexWithUV(x + d5, y + (double) partialHeight, z + d6, 0.0D, d13);
        tessellator.addVertexWithUV(x + d5, y + (double) i, z + d6, 0.0D, d14);
        tessellator.addVertexWithUV(x + d9, y + (double) i, z + d10, 1.0D, d14);
        tessellator.addVertexWithUV(x + d9, y + (double) partialHeight, z + d10, 1.0D, d13);
        tessellator.addVertexWithUV(x + d7, y + (double) partialHeight, z + d8, 0.0D, d13);
        tessellator.addVertexWithUV(x + d7, y + (double) i, z + d8, 0.0D, d14);
        tessellator.addVertexWithUV(x + d5, y + (double) i, z + d6, 1.0D, d14);
        tessellator.addVertexWithUV(x + d5, y + (double) partialHeight, z + d6, 1.0D, d13);
        tessellator.addVertexWithUV(x + d9, y + (double) partialHeight, z + d10, 0.0D, d13);
        tessellator.addVertexWithUV(x + d9, y + (double) i, z + d10, 0.0D, d14);
        tessellator.addVertexWithUV(x + d7, y + (double) i, z + d8, 1.0D, d14);
        tessellator.addVertexWithUV(x + d7, y + (double) partialHeight, z + d8, 1.0D, d13);
        tessellator.addVertexWithUV(x + d3, y + (double) partialHeight, z + d4, 0.0D, d13);
        tessellator.addVertexWithUV(x + d3, y + (double) i, z + d4, 0.0D, d14);
        tessellator.draw();
        OpenGLHelper.enableLighting();
        OpenGLHelper.enableTexture2D();
        OpenGLHelper.depthMask(true);
    }
}
