package ganymedes01.etfuturum.client.renderer.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import ganymedes01.etfuturum.client.model.ModelEndermite;

public class EndermiteRenderer extends RenderLiving {

    private static final ResourceLocation texture = new ResourceLocation("textures/entity/endermite.png");

    public EndermiteRenderer() {
        super(new ModelEndermite(), 0.3F);
    }

    @Override
    protected float getDeathMaxRotation(EntityLivingBase entity) {
        return 180.0F;
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return texture;
    }
}
