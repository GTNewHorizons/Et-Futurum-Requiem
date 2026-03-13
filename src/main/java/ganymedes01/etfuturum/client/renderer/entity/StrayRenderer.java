package ganymedes01.etfuturum.client.renderer.entity;

import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.renderer.entity.RenderSkeleton;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class StrayRenderer extends RenderSkeleton {

    public static final ResourceLocation texture = new ResourceLocation("textures/entity/skeleton/stray.png");
    private static final ResourceLocation overlay_texture = new ResourceLocation(
        "textures/entity/skeleton/stray_overlay.png");
    private final ModelZombie clothesModel;

    public StrayRenderer() {
        super();
        clothesModel = new ModelZombie(0.5F, true);
    }

    @Override
    protected int shouldRenderPass(EntityLivingBase p_77032_1_, int p_77032_2_, float p_77032_3_) {
        if (p_77032_2_ == 1) {
            setRenderPassModel(clothesModel);
            bindTexture(overlay_texture);
            return 1;
        }
        return super.shouldRenderPass(p_77032_1_, p_77032_2_, p_77032_3_);
    }

    @Override
    protected ResourceLocation getEntityTexture(final Entity p_110775_1_) {
        return StrayRenderer.texture;
    }
}
