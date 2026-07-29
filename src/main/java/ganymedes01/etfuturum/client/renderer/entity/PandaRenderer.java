package ganymedes01.etfuturum.client.renderer.entity;

import ganymedes01.etfuturum.client.model.ModelPanda;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class PandaRenderer extends RenderLiving {

	private static final ResourceLocation PANDA_TEXTURE =
			new ResourceLocation("minecraft", "textures/entity/panda/panda.png");

	public PandaRenderer() {
		super(new ModelPanda(), 0.9F);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return PANDA_TEXTURE;
	}
}
