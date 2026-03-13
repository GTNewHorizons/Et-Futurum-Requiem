package ganymedes01.etfuturum.client.renderer.tileentity;

import java.nio.FloatBuffer;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import ganymedes01.etfuturum.configuration.configs.ConfigBlocksItems;
import ganymedes01.etfuturum.tileentities.TileEntityGateway;

public class TileEntityGatewayRenderer extends TileEntitySpecialRenderer {

    private static final ResourceLocation END_SKY_TEXTURE = new ResourceLocation("textures/environment/end_sky.png");
    private static final ResourceLocation END_PORTAL_TEXTURE = new ResourceLocation("textures/entity/end_portal.png");
    private static final ResourceLocation END_GATEWAY_BEAM_TEXTURE = new ResourceLocation(
        "textures/entity/end_gateway_beam.png");
    private static final Random RANDOM = new Random(31100L);
    private static final FloatBuffer MODELVIEW = GLAllocation.createDirectFloatBuffer(16);
    private static final FloatBuffer PROJECTION = GLAllocation.createDirectFloatBuffer(16);
    FloatBuffer buffer = GLAllocation.createDirectFloatBuffer(16);

    public void renderTileEntityAt(TileEntityGateway te, double x, double y, double z, float destroyStage) {
        GL11.glDisable(GL11.GL_FOG);

        if (te.isSpawning() || te.isCoolingDown()) {
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
            this.bindTexture(END_GATEWAY_BEAM_TEXTURE);
            float f = te.isSpawning() ? te.getSpawnPercent() : te.getCooldownPercent();
            double d0 = te.isSpawning() ? 256.0D - y : 25.0D;
            f = MathHelper.sin(f * (float) Math.PI);
            int j = MathHelper.floor_double((double) f * d0);
            float[] afloat = EntitySheep.fleeceColorTable[te.isSpawning() ? ConfigBlocksItems.endGatewaySpawnColor
                : ConfigBlocksItems.endGatewayEntryColor];
            TileEntityNewBeaconRenderer.renderBeamSegment(
                x,
                y,
                z,
                destroyStage,
                f,
                (double) te.getWorldObj()
                    .getTotalWorldTime(),
                0,
                j,
                afloat,
                0.15D,
                0.175D);
            TileEntityNewBeaconRenderer.renderBeamSegment(
                x,
                y,
                z,
                destroyStage,
                f,
                (double) te.getWorldObj()
                    .getTotalWorldTime(),
                0,
                -j,
                afloat,
                0.15D,
                0.175D);
        }

        GL11.glDisable(GL11.GL_LIGHTING);
        RANDOM.setSeed(31100L);
        GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, MODELVIEW);
        GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, PROJECTION);
        double d1 = x * x + y * y + z * z;
        int i;

        if (d1 > 36864.0D) {
            i = 2;
        } else if (d1 > 25600.0D) {
            i = 4;
        } else if (d1 > 16384.0D) {
            i = 6;
        } else if (d1 > 9216.0D) {
            i = 8;
        } else if (d1 > 4096.0D) {
            i = 10;
        } else if (d1 > 1024.0D) {
            i = 12;
        } else if (d1 > 576.0D) {
            i = 14;
        } else if (d1 > 256.0D) {
            i = 15;
        } else {
            i = 16;
        }

        for (int k = 0; k < i; ++k) {
            GL11.glPushMatrix();
            float f5 = 2.0F / (float) (18 - k);

            if (k == 0) {
                this.bindTexture(END_SKY_TEXTURE);
                f5 = 0.15F;
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            }

            if (k >= 1) {
                this.bindTexture(END_PORTAL_TEXTURE);
            }

            if (k == 1) {
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
            }

            GL11.glTexGeni(GL11.GL_S, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_EYE_LINEAR);
            GL11.glTexGeni(GL11.GL_T, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_EYE_LINEAR);
            GL11.glTexGeni(GL11.GL_R, GL11.GL_TEXTURE_GEN_MODE, GL11.GL_EYE_LINEAR);
            GL11.glTexGen(GL11.GL_S, GL11.GL_EYE_PLANE, this.func_147525_a(1.0F, 0.0F, 0.0F, 0.0F));
            GL11.glTexGen(GL11.GL_T, GL11.GL_EYE_PLANE, this.func_147525_a(0.0F, 1.0F, 0.0F, 0.0F));
            GL11.glTexGen(GL11.GL_R, GL11.GL_EYE_PLANE, this.func_147525_a(0.0F, 0.0F, 1.0F, 0.0F));
            GL11.glEnable(GL11.GL_TEXTURE_GEN_S);
            GL11.glEnable(GL11.GL_TEXTURE_GEN_T);
            GL11.glEnable(GL11.GL_TEXTURE_GEN_R);
            GL11.glPopMatrix();
            GL11.glMatrixMode(GL11.GL_TEXTURE);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glTranslatef(0.5F, 0.5F, 0.0F);
            GL11.glScalef(0.5F, 0.5F, 1.0F);
            float f1 = (float) (k + 1);
            GL11.glTranslatef(
                17.0F / f1,
                (2.0F + f1 / 1.5F) * ((float) Minecraft.getSystemTime() % 800000.0F / 800000.0F),
                0.0F);
            GL11.glRotatef((f1 * f1 * 4321.0F + f1 * 9.0F) * 2.0F, 0.0F, 0.0F, 1.0F);
            GL11.glScalef(4.5F - f1 / 4.0F, 4.5F - f1 / 4.0F, 1.0F);
            GL11.glMultMatrix(PROJECTION);
            GL11.glMultMatrix(MODELVIEW);
            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();
            float f2 = (RANDOM.nextFloat() * 0.5F + 0.1F) * f5;
            float f3 = (RANDOM.nextFloat() * 0.5F + 0.4F) * f5;
            float f4 = (RANDOM.nextFloat() * 0.5F + 0.5F) * f5;

            if (k == 0) {
                f2 = f3 = f4 = f5;
            }

            tessellator.setColorOpaque_F(f2, f3, f4);
            if (te.shouldRenderFace(EnumFacing.SOUTH)) {
                tessellator.addVertex(x, y, z + 1.0D);
                tessellator.addVertex(x + 1.0D, y, z + 1.0D);
                tessellator.addVertex(x + 1.0D, y + 1.0D, z + 1.0D);
                tessellator.addVertex(x, y + 1.0D, z + 1.0D);
            }

            if (te.shouldRenderFace(EnumFacing.NORTH)) {
                tessellator.addVertex(x, y + 1.0D, z);
                tessellator.addVertex(x + 1.0D, y + 1.0D, z);
                tessellator.addVertex(x + 1.0D, y, z);
                tessellator.addVertex(x, y, z);
            }

            // TODO: Swapped east and west since their X value is wrong. Maybe I'll find a better way to fix it,
            // hopefully.
            if (te.shouldRenderFace(EnumFacing.WEST)) {
                tessellator.addVertex(x + 1.0D, y + 1.0D, z);
                tessellator.addVertex(x + 1.0D, y + 1.0D, z + 1.0D);
                tessellator.addVertex(x + 1.0D, y, z + 1.0D);
                tessellator.addVertex(x + 1.0D, y, z);
            }

            if (te.shouldRenderFace(EnumFacing.EAST)) {
                tessellator.addVertex(x, y, z);
                tessellator.addVertex(x, y, z + 1.0D);
                tessellator.addVertex(x, y + 1.0D, z + 1.0D);
                tessellator.addVertex(x, y + 1.0D, z);
            }

            if (te.shouldRenderFace(EnumFacing.DOWN)) {
                tessellator.addVertex(x, y, z);
                tessellator.addVertex(x + 1.0D, y, z);
                tessellator.addVertex(x + 1.0D, y, z + 1.0D);
                tessellator.addVertex(x, y, z + 1.0D);
            }

            if (te.shouldRenderFace(EnumFacing.UP)) {
                tessellator.addVertex(x, y + 1.0D, z + 1.0D);
                tessellator.addVertex(x + 1.0D, y + 1.0D, z + 1.0D);
                tessellator.addVertex(x + 1.0D, y + 1.0D, z);
                tessellator.addVertex(x, y + 1.0D, z);
            }

            tessellator.draw();
            GL11.glPopMatrix();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            this.bindTexture(END_SKY_TEXTURE);
        }

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_GEN_S);
        GL11.glDisable(GL11.GL_TEXTURE_GEN_T);
        GL11.glDisable(GL11.GL_TEXTURE_GEN_R);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_FOG);
    }

    private FloatBuffer func_147525_a(float p_147525_1_, float p_147525_2_, float p_147525_3_, float p_147525_4_) {
        this.buffer.clear();
        this.buffer.put(p_147525_1_)
            .put(p_147525_2_)
            .put(p_147525_3_)
            .put(p_147525_4_);
        this.buffer.flip();
        return this.buffer;
    }

    @Override
    public void renderTileEntityAt(TileEntity p_147500_1_, double p_147500_2_, double p_147500_4_, double p_147500_6_,
        float p_147500_8_) {
        this.renderTileEntityAt((TileEntityGateway) p_147500_1_, p_147500_2_, p_147500_4_, p_147500_6_, p_147500_8_);
    }
}
