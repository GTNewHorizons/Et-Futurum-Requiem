package ganymedes01.etfuturum.client.renderer.entity;

import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import ganymedes01.etfuturum.entities.EntityHusk;

public class HuskRenderer extends RenderBiped {

    public static final ResourceLocation texture;
    private final float scale;

    public HuskRenderer() {
        super(new ModelZombie(), 0.4f);
        this.scale = 1.1f;
    }

    @Override
    protected void preRenderCallback(EntityLivingBase par1EntityLiving, float par2) {
        if ((par1EntityLiving instanceof EntityHusk)) {
            GL11.glScalef(this.scale, this.scale, this.scale);
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(final Entity p_110775_1_) {
        return texture;
    }

    static {
        texture = new ResourceLocation("textures/entity/zombie/husk.png");
    }
}
