package ganymedes01.etfuturum.client.renderer.tileentity;

import java.util.Random;

import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import com.google.common.primitives.SignedBytes;

import ganymedes01.etfuturum.client.model.ModelShulker;
import ganymedes01.etfuturum.client.renderer.entity.ShulkerRenderer;
import ganymedes01.etfuturum.tileentities.TileEntityShulkerBox;

public class TileEntityShulkerBoxRenderer extends TileEntitySpecialRenderer {

    public int tier = 0;
    private final Random random;
    private final RenderItem itemRenderer;
    private static final float[][] shifts = { { 0.3F, 0.45F, 0.3F }, { 0.7F, 0.45F, 0.3F }, { 0.3F, 0.45F, 0.7F },
        { 0.7F, 0.45F, 0.7F }, { 0.3F, 0.1F, 0.3F }, { 0.7F, 0.1F, 0.3F }, { 0.3F, 0.1F, 0.7F }, { 0.7F, 0.1F, 0.7F },
        { 0.5F, 0.32F, 0.5F }, };
    protected static final ResourceLocation[] DESTROY_STAGES = new ResourceLocation[] {
        new ResourceLocation("textures/blocks/destroy_stage_0.png"),
        new ResourceLocation("textures/blocks/destroy_stage_1.png"),
        new ResourceLocation("textures/blocks/destroy_stage_2.png"),
        new ResourceLocation("textures/blocks/destroy_stage_3.png"),
        new ResourceLocation("textures/blocks/destroy_stage_4.png"),
        new ResourceLocation("textures/blocks/destroy_stage_5.png"),
        new ResourceLocation("textures/blocks/destroy_stage_6.png"),
        new ResourceLocation("textures/blocks/destroy_stage_7.png"),
        new ResourceLocation("textures/blocks/destroy_stage_8.png"),
        new ResourceLocation("textures/blocks/destroy_stage_9.png") };
    private final ModelShulker modelShulker;

    public TileEntityShulkerBoxRenderer(ModelShulker p_i47216_1_) {
        this.modelShulker = p_i47216_1_;
        this.random = new Random();
        itemRenderer = new RenderItem() {

            @Override
            public byte getMiniBlockCount(ItemStack stack, byte original) {
                return SignedBytes.saturatedCast(Math.min(stack.stackSize / 32, 15) + 1);
            }

            @Override
            public byte getMiniItemCount(ItemStack stack, byte original) {
                return SignedBytes.saturatedCast(Math.min(stack.stackSize / 32, 7) + 1);
            }

            @Override
            public boolean shouldBob() {
                return false;
            }

            @Override
            public boolean shouldSpreadItems() {
                return false;
            }
        };
        itemRenderer.setRenderManager(RenderManager.instance);
    }

    public void renderTileEntityAt(TileEntityShulkerBox te, double x, double y, double z, float partialTicks,
        int destroyStage) {
        ForgeDirection enumfacing = ForgeDirection.UP;

        tier = te.type.ordinal();

        if (te.hasWorldObj()) {
            int facing = te.facing;
            enumfacing = ForgeDirection.VALID_DIRECTIONS[facing];
        }

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT);

        // GlStateManager.enableDepth();
        // GlStateManager.depthFunc(515);
        // GlStateManager.depthMask(true);
        // GlStateManager.disableCull();
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_ALPHA_TEST); // Needed because the texture has transparent pixels

        if (destroyStage >= 0) {
            this.bindTexture(DESTROY_STAGES[destroyStage]);
            GL11.glMatrixMode(GL11.GL_TEXTURE);
            // GL11.glPushMatrix();
            GL11.glScalef(4.0F, 4.0F, 1.0F);
            GL11.glTranslatef(0.0625F, 0.0625F, 0.0625F);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
        } else {
            this.bindTexture(
                ShulkerRenderer.SHULKER_ENDERGOLEM_TEXTURES[te.color
                    % ShulkerRenderer.SHULKER_ENDERGOLEM_TEXTURES.length
                    + (tier % (TileEntityShulkerBox.tiers.length + 1) * 17)]);
        }

        GL11.glPushMatrix();
        // GlStateManager.enableRescaleNormal();

        if (destroyStage < 0) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }

        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
        GL11.glScalef(1.0F, -1.0F, -1.0F);
        GL11.glTranslatef(0.0F, 1.0F, 0.0F);
        GL11.glScalef(0.9995F, 0.9995F, 0.9995F);
        GL11.glTranslatef(0.0F, -1.0F, 0.0F);

        switch (enumfacing) {
            case DOWN:
                GL11.glTranslatef(0.0F, 2.0F, 0.0F);
                GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);

            case UP:
            default:
                break;

            case NORTH:
                GL11.glTranslatef(0.0F, 1.0F, 1.0F);
                GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
                break;

            case SOUTH:
                GL11.glTranslatef(0.0F, 1.0F, -1.0F);
                GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                break;

            case WEST:
                GL11.glTranslatef(-1.0F, 1.0F, 0.0F);
                GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
                break;

            case EAST:
                GL11.glTranslatef(1.0F, 1.0F, 0.0F);
                GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
        }

        this.modelShulker.base.render(0.0625F);
        GL11.glTranslatef(0.0F, -te.getProgress(partialTicks) * 0.5F, 0.0F);
        GL11.glRotatef(270.0F * te.getProgress(partialTicks), 0.0F, 1.0F, 0.0F);
        this.modelShulker.lid.render(0.0625F);
        // GlStateManager.enableCull();
        GL11.glEnable(GL11.GL_CULL_FACE);
        // GlStateManager.disableRescaleNormal();
        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        if (destroyStage >= 0) {
            GL11.glMatrixMode(GL11.GL_TEXTURE);
            GL11.glPopMatrix();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
        }
        if (te.hasWorldObj() && te.type.getIsClear()
            && te.getDistanceFrom(
                this.field_147501_a.field_147560_j,
                this.field_147501_a.field_147561_k,
                this.field_147501_a.field_147558_l) < 128d) {
            random.setSeed(254L);
            float shiftX;
            float shiftY;
            float shiftZ;
            int shift = 0;
            float blockScale = 0.70F;
            float timeD = (float) (360.0 * (double) (System.currentTimeMillis() & 0x3FFFL) / (double) 0x3FFFL);
            if (te.getTopItemStacks()[1] == null) {
                shift = 8;
                blockScale = 0.85F;
            }
            GL11.glPushMatrix();
            GL11.glDisable(GL11.GL_LIGHTING /* GL_LIGHTING */);
            GL11.glTranslatef((float) x, (float) y, (float) z);
            EntityItem customitem = new EntityItem(field_147501_a.field_147550_f/* worldObj */);
            customitem.hoverStart = 0f;
            for (ItemStack item : te.getTopItemStacks()) {
                if (shift > shifts.length) {
                    break;
                }
                if (item == null) {
                    shift++;
                    continue;
                }
                shiftX = shifts[shift][0];
                shiftY = shifts[shift][1];
                shiftZ = shifts[shift][2];
                shift++;
                GL11.glPushMatrix();
                GL11.glTranslatef(shiftX, shiftY, shiftZ);
                GL11.glRotatef(timeD, 0.0F, 1.0F, 0.0F);
                GL11.glScalef(blockScale, blockScale, blockScale);
                customitem.setEntityItemStack(item);
                itemRenderer.doRender(customitem, 0, 0, 0, 0, 0);
                GL11.glPopMatrix();
            }
            GL11.glEnable(GL11.GL_LIGHTING /* GL_LIGHTING */);
            GL11.glPopMatrix();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }

        GL11.glPopAttrib();
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks) {
        this.renderTileEntityAt((TileEntityShulkerBox) te, x, y, z, partialTicks, -1);
    }

}
