package ganymedes01.etfuturum.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelPig;
import net.minecraft.client.renderer.entity.RenderPig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.util.ResourceLocation;

import ganymedes01.etfuturum.client.model.ModelTechnobladeCrown;

public class TechnobladeCrownRenderer extends RenderPig {

    private static final ResourceLocation pigTextures = new ResourceLocation("textures/entity/pig/pig.png");

    private static final ResourceLocation crownTexture = new ResourceLocation(
        "etfuturum:textures/entity/pig/technoblade_crown.png");
    private final ModelBase crown = new ModelTechnobladeCrown();
    private ModelBase prevRenderPassModel;

    public TechnobladeCrownRenderer() {
        super(new ModelPig(), new ModelPig(0.5F), 0.7F);
    }

    @Override
    protected int shouldRenderPass(EntityPig p_77032_1_, int p_77032_2_, float p_77032_3_) {
        if ("Technoblade".equals(p_77032_1_.getCustomNameTag())) {
            if (p_77032_2_ == 1) {
                prevRenderPassModel = renderPassModel;
                renderPassModel = crown;
                crown.isChild = this.mainModel.isChild;
                this.bindTexture(crownTexture);
                return 1;
            }
            if (p_77032_2_ == 2) {
                renderPassModel = prevRenderPassModel;
            }
        }
        return super.shouldRenderPass(p_77032_1_, p_77032_2_, p_77032_3_);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
        return pigTextures;
    }

}
