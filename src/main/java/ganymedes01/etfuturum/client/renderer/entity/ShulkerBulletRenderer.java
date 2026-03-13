package ganymedes01.etfuturum.client.renderer.entity;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import ganymedes01.etfuturum.client.OpenGLHelper;
import ganymedes01.etfuturum.client.model.ModelShulkerBullet;

public class ShulkerBulletRenderer extends Render {

    private static final ResourceLocation SHULKER_SPARK_TEXTURE = new ResourceLocation(
        "textures/entity/shulker/spark.png");
    private final ModelShulkerBullet model = new ModelShulkerBullet();

    public ShulkerBulletRenderer() {}

    private float rotLerp(float p_188347_1_, float p_188347_2_, float p_188347_3_) {
        float f;

        for (f = p_188347_2_ - p_188347_1_; f < -180.0F; f += 360.0F) {}

        while (f >= 180.0F) {
            f -= 360.0F;
        }

        return p_188347_1_ + p_188347_3_ * f;
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    @Override
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GL11.glPushMatrix();
        float f = this.rotLerp(entity.prevRotationYaw, entity.rotationYaw, partialTicks);
        float f1 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
        float f2 = (float) entity.ticksExisted + partialTicks;
        GL11.glTranslatef((float) x, (float) y + 0.15F, (float) z);
        GL11.glRotatef(MathHelper.sin(f2 * 0.1F) * 180.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(MathHelper.cos(f2 * 0.1F) * 180.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(MathHelper.sin(f2 * 0.15F) * 360.0F, 0.0F, 0.0F, 1.0F);
        float f3 = 0.03125F;
        // OpenGLHelper.enableRescaleNormal();
        GL11.glScalef(-1.0F, -1.0F, 1.0F);
        this.bindEntityTexture(entity);
        GL11.glColor4f(1, 1, 1, 1);
        this.model.render(entity, 0.0F, 0.0F, 0.0F, f, f1, f3);
        OpenGLHelper.enableBlend();
        OpenGLHelper.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1, 1, 1, 0.5F);
        GL11.glScalef(1.5F, 1.5F, 1.5F);
        this.model.render(entity, 0.0F, 0.0F, 0.0F, f, f1, f3);
        OpenGLHelper.disableBlend();
        GL11.glPopMatrix();
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return SHULKER_SPARK_TEXTURE;
    }
}
