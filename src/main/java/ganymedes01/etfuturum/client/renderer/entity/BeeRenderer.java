package ganymedes01.etfuturum.client.renderer.entity;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import ganymedes01.etfuturum.client.model.ModelBee;
import ganymedes01.etfuturum.entities.EntityBee;

public class BeeRenderer extends RenderLiving {

    private static final ResourceLocation field_229040_a_ = new ResourceLocation("textures/entity/bee/bee_angry.png");
    private static final ResourceLocation field_229041_g_ = new ResourceLocation(
        "textures/entity/bee/bee_angry_nectar.png");
    private static final ResourceLocation field_229042_h_ = new ResourceLocation("textures/entity/bee/bee.png");
    private static final ResourceLocation field_229043_i_ = new ResourceLocation("textures/entity/bee/bee_nectar.png");

    public BeeRenderer() {
        super(new ModelBee(), 0.4F);
    }

    @Override
    public ResourceLocation getEntityTexture(Entity entity) {
        return getBeeTexture((EntityBee) entity);
    }

    public ResourceLocation getBeeTexture(EntityBee entity) {
        if (entity.isAngry()) {
            return entity.hasNectar() ? field_229041_g_ : field_229040_a_;
        } else {
            return entity.hasNectar() ? field_229043_i_ : field_229042_h_;
        }
    }
}
