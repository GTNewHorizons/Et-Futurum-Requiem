package ganymedes01.etfuturum.client.renderer.entity;

import ganymedes01.etfuturum.client.model.ModelPanda;
import ganymedes01.etfuturum.entities.EntityPanda;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class PandaRenderer extends RenderLiving {

	private static final ResourceLocation NORMAL_TEXTURE =
			new ResourceLocation("minecraft", "textures/entity/panda/panda.png");
	private static final ResourceLocation AGGRESSIVE_TEXTURE =
			new ResourceLocation("minecraft", "textures/entity/panda/aggressive_panda.png");
	private static final ResourceLocation LAZY_TEXTURE =
			new ResourceLocation("minecraft", "textures/entity/panda/lazy_panda.png");
	private static final ResourceLocation PLAYFUL_TEXTURE =
			new ResourceLocation("minecraft", "textures/entity/panda/playful_panda.png");
	private static final ResourceLocation WORRIED_TEXTURE =
			new ResourceLocation("minecraft", "textures/entity/panda/worried_panda.png");
	private static final ResourceLocation BROWN_TEXTURE =
			new ResourceLocation("minecraft", "textures/entity/panda/brown_panda.png");
	private static final ResourceLocation WEAK_TEXTURE =
			new ResourceLocation("minecraft", "textures/entity/panda/weak_panda.png");

	public PandaRenderer() {
		super(new ModelPanda(), 0.9F);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		if (!(entity instanceof EntityPanda)) {
			return NORMAL_TEXTURE;
		}

		EntityPanda.Gene variant = ((EntityPanda) entity).getVariant();
		if (variant == null) {
			return NORMAL_TEXTURE;
		}

		switch (variant) {
			case AGGRESSIVE:
				return AGGRESSIVE_TEXTURE;
			case LAZY:
				return LAZY_TEXTURE;
			case PLAYFUL:
				return PLAYFUL_TEXTURE;
			case WORRIED:
				return WORRIED_TEXTURE;
			case BROWN:
				return BROWN_TEXTURE;
			case WEAK:
				return WEAK_TEXTURE;
			case NORMAL:
			default:
				return NORMAL_TEXTURE;
		}
	}
}
