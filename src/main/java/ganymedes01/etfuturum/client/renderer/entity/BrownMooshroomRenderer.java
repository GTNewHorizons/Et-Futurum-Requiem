package ganymedes01.etfuturum.client.renderer.entity;

import net.minecraft.client.model.ModelCow;
import net.minecraft.client.model.ModelQuadruped;
import net.minecraft.client.renderer.entity.RenderMooshroom;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class BrownMooshroomRenderer extends RenderMooshroom {

    public BrownMooshroomRenderer() {
        super(new ModelCow(), 0.7F);
    }

    private static final ResourceLocation mooshroomTextures = new ResourceLocation(
        "textures/entity/cow/brown_mooshroom.png");

    @Override
    protected ResourceLocation getEntityTexture(EntityMooshroom p_110775_1_) {
        return mooshroomTextures;
    }

    @Override
    protected void renderEquippedItems(EntityMooshroom p_77029_1_, float p_77029_2_) {
        if (!p_77029_1_.isChild()) {
            this.bindTexture(TextureMap.locationBlocksTexture);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glPushMatrix();
            GL11.glScalef(1.0F, -1.0F, 1.0F);
            GL11.glTranslatef(0.2F, 0.4F, 0.5F);
            GL11.glRotatef(42.0F, 0.0F, 1.0F, 0.0F);
            this.field_147909_c.renderBlockAsItem(Blocks.brown_mushroom, 0, 1.0F);
            GL11.glTranslatef(0.1F, 0.0F, -0.6F);
            GL11.glRotatef(42.0F, 0.0F, 1.0F, 0.0F);
            this.field_147909_c.renderBlockAsItem(Blocks.brown_mushroom, 0, 1.0F);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            ((ModelQuadruped) this.mainModel).head.postRender(0.0625F);
            GL11.glScalef(1.0F, -1.0F, 1.0F);
            GL11.glTranslatef(0.0F, 0.75F, -0.2F);
            GL11.glRotatef(12.0F, 0.0F, 1.0F, 0.0F);
            this.field_147909_c.renderBlockAsItem(Blocks.brown_mushroom, 0, 1.0F);
            GL11.glPopMatrix();
            GL11.glDisable(GL11.GL_CULL_FACE);
        }
    }
}
