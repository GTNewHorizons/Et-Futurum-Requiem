package ganymedes01.etfuturum.client.renderer.item;

import net.minecraft.block.BlockBed;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

/**
 * @author TheMasterCaver
 *         Ported by Roadhgo360 and Venn
 *         Thanks to Venn for helping me fix this bed model to work as an IItemRenderer
 */
public class Item3DBedRenderer implements IItemRenderer {

    private final BlockBed bed;

    public Item3DBedRenderer(BlockBed block) {
        bed = block;
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return type != ItemRenderType.FIRST_PERSON_MAP;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return helper != ItemRendererHelper.BLOCK_3D || type == ItemRenderType.EQUIPPED;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        if (type == ItemRenderType.INVENTORY) {
            GL11.glEnable(GL11.GL_ALPHA_TEST);
        }
        renderBed(type);
        if (type == ItemRenderType.INVENTORY) {
            GL11.glDisable(GL11.GL_ALPHA_TEST);
        }
    }

    private void renderBed(ItemRenderType type) {
        TextureManager tm = Minecraft.getMinecraft()
            .getTextureManager();
        ResourceLocation rl = tm.getResourceLocation(0);
        tm.bindTexture(rl);

        Tessellator tessellator = Tessellator.instance;
        // boolean useInventoryTint = renderer.useInventoryTint;
        tessellator.startDrawingQuads();

        int meta = 10; // head facing north, foot is to the south
        float xOffset;
        float yOffset;
        float zOffset;
        float scale;

        switch (type) {
            case ENTITY:
                // Rendered as a dropped item
                GL11.glScalef(0.5F, 0.5F, 0.5F);
                xOffset = -0.35F;
                yOffset = 0F;
                zOffset = -0.7F;
                scale = 0.7F;
                break;
            case EQUIPPED:
                // Rendered in 3rd person
                xOffset = 0.0F;
                yOffset = 0.0F;
                zOffset = -0.5F;
                scale = 0.7F;
                break;
            case EQUIPPED_FIRST_PERSON:
                // Rendered in first person
                xOffset = 0F;
                yOffset = .5F;
                zOffset = 0F;
                scale = 0.7F;
                break;
            default:
            case INVENTORY:
                GL11.glRotatef(20.0F, 0.0F, 1.0F, 0.0F);
                xOffset = -0.02F;
                yOffset = 0.24F;
                zOffset = 0.05F;
                scale = 0.8F;
                break;
        }

        float minX = xOffset;
        float minZ = zOffset;
        float maxX = minX + scale;
        float maxZ = minZ + scale;

        // Renders head first, then foot
        for (int i = 0; i < 2; ++i) {
            float minY = yOffset + 0.1875F * scale;
            float maxY = yOffset + 0.5625F * scale;
            IIcon icon = bed.getIcon(0, meta);
            float minU = icon.getMinU();
            float maxU = icon.getMaxU();
            float minV = icon.getMinV();
            float maxV = icon.getMaxV();
            tessellator.setNormal(0.0F, -1.0F, 0.0F);
            tessellator.addVertexWithUV(minX, minY, maxZ, minU, maxV);
            tessellator.addVertexWithUV(minX, minY, minZ, minU, minV);
            tessellator.addVertexWithUV(maxX, minY, minZ, maxU, minV);
            tessellator.addVertexWithUV(maxX, minY, maxZ, maxU, maxV);
            icon = bed.getIcon(1, meta);
            minU = icon.getMinU();
            maxU = icon.getMaxU();
            minV = icon.getMinV();
            maxV = icon.getMaxV();
            tessellator.setNormal(0.0F, 1.0F, 0.0F);
            tessellator.addVertexWithUV(maxX, maxY, maxZ, minU, minV);
            tessellator.addVertexWithUV(maxX, maxY, minZ, maxU, minV);
            tessellator.addVertexWithUV(minX, maxY, minZ, maxU, maxV);
            tessellator.addVertexWithUV(minX, maxY, maxZ, minU, maxV);
            int var49 = (i == 0 ? Direction.directionToFacing[Direction.rotateOpposite[2]]
                : Direction.directionToFacing[2]);
            minY = yOffset;
            maxY = yOffset + scale;

            if (var49 != 2) {
                icon = bed.getIcon(2, meta);
                minU = icon.getMinU();
                maxU = icon.getMaxU();
                minV = icon.getMinV();
                maxV = icon.getMaxV();
                tessellator.setNormal(0.0F, 0.0F, -1.0F);
                tessellator.addVertexWithUV(minX, maxY, minZ, maxU, minV);
                tessellator.addVertexWithUV(maxX, maxY, minZ, minU, minV);
                tessellator.addVertexWithUV(maxX, minY, minZ, minU, maxV);
                tessellator.addVertexWithUV(minX, minY, minZ, maxU, maxV);
            }

            if (var49 != 3) {
                icon = bed.getIcon(3, meta);
                minU = icon.getMinU();
                maxU = icon.getMaxU();
                minV = icon.getMinV();
                maxV = icon.getMaxV();
                tessellator.setNormal(0.0F, 0.0F, 1.0F);
                tessellator.addVertexWithUV(minX, maxY, maxZ, minU, minV);
                tessellator.addVertexWithUV(minX, minY, maxZ, minU, maxV);
                tessellator.addVertexWithUV(maxX, minY, maxZ, maxU, maxV);
                tessellator.addVertexWithUV(maxX, maxY, maxZ, maxU, minV);
            }

            if (var49 != 4) {
                icon = bed.getIcon(4, meta);
                minU = icon.getMaxU();
                maxU = icon.getMinU();
                minV = icon.getMinV();
                maxV = icon.getMaxV();
                tessellator.setNormal(-1.0F, 0.0F, 0.0F);
                tessellator.addVertexWithUV(minX, maxY, maxZ, maxU, minV);
                tessellator.addVertexWithUV(minX, maxY, minZ, minU, minV);
                tessellator.addVertexWithUV(minX, minY, minZ, minU, maxV);
                tessellator.addVertexWithUV(minX, minY, maxZ, maxU, maxV);
            }

            if (var49 != 5) {
                icon = bed.getIcon(5, meta);
                minU = icon.getMinU();
                maxU = icon.getMaxU();
                minV = icon.getMinV();
                maxV = icon.getMaxV();
                tessellator.setNormal(1.0F, 0.0F, 0.0F);
                tessellator.addVertexWithUV(maxX, minY, maxZ, minU, maxV);
                tessellator.addVertexWithUV(maxX, minY, minZ, maxU, maxV);
                tessellator.addVertexWithUV(maxX, maxY, minZ, maxU, minV);
                tessellator.addVertexWithUV(maxX, maxY, maxZ, minU, minV);
            }

            meta -= 8;
            minZ += scale;
            maxZ += scale;
        }

        tessellator.draw();
    }
}
